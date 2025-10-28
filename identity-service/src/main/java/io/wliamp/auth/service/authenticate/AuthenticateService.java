package io.wliamp.auth.service.authenticate;

import io.wliamp.auth.service.data.AccService;
import io.wliamp.auth.service.data.CacheService;
import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import io.wliamp.auth.compo.handler.CacheHandler;
import io.wliamp.auth.compo.handler.TokenHandler;
import io.wliamp.auth.compo.helper.PartyHelper;
import io.wliamp.auth.dto.Tokens;
import io.wliamp.auth.util.Generator;

import static io.wliamp.auth.util.Parser.mask;
import static reactor.core.publisher.Mono.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticateService {
    private final CommonService commonService;

    private final CacheService cacheService;

    private final AccService accService;

    private final CacheHandler cacheHandler;

    private final TokenHandler tokenHandler;

    private final Map<String, PartyHelper> loginHelpers;

    @Value("${cache.ttl.days}")
    private Duration CACHE_TTL;

    public Mono<Tokens> guestLogin() {
        var cred = "guest:" + Generator.generateCode(32);
        return commonService
                .initAccountIfNotExists(cred)
                .doOnNext(id -> log.info("created GUEST account ID = {}", id))
                .flatMap(commonService::buildScopeAndAudiencesClaims)
                .doOnNext(_ -> log.debug("claims(scope, aud) built for {}", cred))
                .flatMap(claims -> tokenHandler.issueGuestToken(cred, claims))
                .doOnNext(_ -> log.info("GUEST token issued for {}", mask(cred)))
                .flatMap(tokens -> cacheHandler
                        .put("auth:" + cred, tokens, CACHE_TTL)
                        .doOnSuccess(_ -> log.info("token cached for {}", mask(cred)))
                        .thenReturn(tokens));
    }

    public Mono<Tokens> loginWithoutHeader(String party, String token) {
        var partyHelper = loginHelpers.get(party);
        return partyHelper
                .getSubject(token)
                .switchIfEmpty(
                        error(new IllegalArgumentException("Unsupported login party: " + party.toUpperCase())))
                .doOnSubscribe(_ -> log.debug("start login flow for {}", party.toUpperCase()))
                .doOnNext(sub -> log.debug("extracted subject for {} -> {}", party.toUpperCase(), mask(sub)))
                .switchIfEmpty(defer(() -> {
                    log.warn("getSubject returned empty");
                    return error(new RuntimeException("Invalid token"));
                }))
                .map(sub -> party + ":" + sub)
                .flatMap(cred -> commonService.loginFlow(cred, partyHelper, token))
                .doOnNext(resp -> log.info(
                        "login success for {} with tokens(access, refresh) = ({}, {})",
                        party.toUpperCase(),
                        mask(resp.access()),
                        mask(resp.refresh())))
                .doOnError(err -> log.error("login failed for {}", party.toUpperCase(), err));
    }

    public Mono<Tokens> linkAccount(String party, String oldToken, String newToken) {
        return zip(internalToken.verify(oldToken), cacheHandler.isTokenBlacklisted(oldToken))
                .filter(tuple -> tuple.getT1() && !tuple.getT2())
                .switchIfEmpty(error(new IllegalArgumentException("Invalid or blacklisted token")))
                .then(internalToken.getClaims(oldToken))
                .flatMap(claims -> justOrEmpty((String) claims.get("sub"))
                        .doOnNext(oldCred -> log.info("link account start {}", mask(oldCred)))
                        .switchIfEmpty(error(new IllegalArgumentException("Missing subject in old token"))))
                .flatMap(oldCred -> justOrEmpty(loginHelpers.get(party))
                        .switchIfEmpty(error(
                                new IllegalArgumentException("Unsupported login party: " + party.toUpperCase())))
                        .flatMap(partyHelper -> {
                            var newCred = party + ":" + partyHelper.getSubject(newToken);
                            log.info("Attempt link from {} to {}", mask(newCred), mask(oldCred));
                            return accService
                                    .getAccountByCred(newCred)
                                    .flatMap(_ -> Mono.<Tokens>error(
                                            new IllegalStateException("This party account is already linked")))
                                    .switchIfEmpty(accService
                                            .updateCred(oldCred, newCred)
                                            .then(cacheHandler.evict("auth:" + oldCred))
                                            .then(commonService.initAccountIfNotExists(newCred))
                                            .flatMap(commonService::buildScopeAndAudiencesClaims)
                                            .flatMap(_ ->
                                                    commonService.loginFlow(newCred, partyHelper, newToken)));
                        }));
    }

    public Mono<Tokens> loginWithHeader(String token) {
        return internalToken
                .verify(token)
                .filter(Boolean::booleanValue)
                .switchIfEmpty(defer(() -> {
                    log.warn("refresh token verification failed (invalid)");
                    return error(new IllegalArgumentException("Invalid refresh token"));
                }))
                .flatMap(_ -> cacheHandler
                        .isTokenBlacklisted(token)
                        .filter(isBlacklisted -> !isBlacklisted)
                        .switchIfEmpty(defer(() -> {
                            log.warn("refresh token is blacklisted");
                            return error(new IllegalStateException("Token revoked or blacklisted"));
                        }))
                        .then(internalToken.getClaims(token))
                        .flatMap(claims -> justOrEmpty((String) claims.get("sub"))
                                .switchIfEmpty(
                                        error(new IllegalArgumentException("Missing subject in refresh token"))))
                        .flatMap(subject -> cacheService
                                .loadUserToken("auth:" + subject)
                                .switchIfEmpty(defer(() -> {
                                    log.warn("Session expired for {}", subject);
                                    return error(new IllegalStateException("Session expired, login required"));
                                }))
                                .flatMap(oldToken -> zip(
                                                internalToken.refresh(oldToken.access(), 3600),
                                                internalToken.refresh(token, 2592000))
                                        .flatMap(tuple -> {
                                            var newTokens = new Tokens(tuple.getT1(), tuple.getT2());
                                            log.info("refreshed tokens for {}", mask(subject));
                                            return commonService
                                                    .evictAndBlacklist(subject, oldToken, token)
                                                    .then(cacheHandler.put("auth:" + subject, newTokens, CACHE_TTL))
                                                    .thenReturn(newTokens);
                                        }))));
    }

    public Mono<Void> logout(String token) {
        return internalToken.getClaims(token).flatMap(claims -> {
            log.info("logout requested for {}", mask(claims.get("sub")));
            return commonService.evictAndBlacklist(claims.get("sub").toString(), new Tokens(token, token), token);
        });
    }
}
