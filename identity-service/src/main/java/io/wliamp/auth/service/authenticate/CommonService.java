package io.wliamp.auth.service.authenticate;

import io.wliamp.auth.service.data.*;

import java.time.Duration;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import io.wliamp.auth.compo.handler.CacheHandler;
import io.wliamp.auth.compo.helper.PartyHelper;
import io.wliamp.auth.dto.Tokens;
import io.wliamp.auth.entity.Acc;

import static io.wliamp.auth.util.Builder.*;
import static java.lang.Math.*;
import static java.lang.System.*;
import static java.time.Duration.*;
import static reactor.core.publisher.Mono.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommonService {
    private final AccService accService;

    private final AudService audService;

    private final ScopeService scopeService;

    private final AccAudService accAudService;

    private final AccScopeService accScopeService;

    private final CacheHandler cacheHandler;

    @Value("${cache.ttl.days}")
    private Duration CACHE_TTL;

    public Mono<Long> initAccountIfNotExists(String cred) {
        return accService
                .getAccountByCred(cred)
                .doOnNext(_ -> log.info("Found existing account for {}", cred))
                .map(Acc::getId)
                .switchIfEmpty(accService
                        .addNewAccount(cred)
                        .flatMap(accId -> when(
                                accScopeService.addNewAccount(accId),
                                accAudService.addNewAccount(accId)
                        )
                                .thenReturn(accId))
                        .doOnNext(newId -> log.info("Created new account {} for {}", newId, cred)));
    }

    public Mono<Map<String, Object>> buildScopeAndAudiencesClaims(Long accId) {
        return zip(
                scopeService.getScopesByAccountId(accId).collectList(),
                audService.getAudiencesByAccountId(accId).collectList())
                .map(t -> buildTokenExtraClaims(t.getT1(), t.getT2()));
    }

    public Mono<Tokens> loginFlow(String cred, PartyHelper partyHelper, String token) {
        return initAccountIfNotExists(cred)
                .doOnNext(id -> log.info("AccountId = {}", id))
                .flatMap(this::buildScopeAndAudiencesClaims)
                .doOnNext(_ -> log.info("Claims built"))
                .flatMap(claims -> partyHelper.issueToken(token, claims))
                .doOnNext(_ -> log.info("Token issued"))
                .flatMap(userToken ->
                        cacheHandler.put("auth:" + cred, userToken, CACHE_TTL).thenReturn(userToken));
    }

    public Mono<Void> evictAndBlacklist(String subject, Tokens oldToken, String refreshToken) {
        var now = currentTimeMillis() / 1000;
        var accessExp = getExpClaim(oldToken.access());
        var accessTTL = max(0, accessExp - now);
        var refreshExp = getExpClaim(refreshToken);
        var refreshTTL = max(0, refreshExp - now);
        return cacheHandler
                .evict("auth:" + subject)
                .then(cacheHandler.blacklistToken(oldToken.access(), ofSeconds(accessTTL)))
                .then(cacheHandler.blacklistToken(refreshToken, ofSeconds(refreshTTL)))
                .then();
    }

    public long getExpClaim(String token) {
        try {
            Map<String, Object> claims = internalToken.getClaims(token).block();
            assert claims != null;
            Object expObj = claims.get("exp");
            return expObj != null ? Long.parseLong(expObj.toString()) : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
