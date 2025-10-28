package io.wliamp.auth.compo.handler;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import io.wliamp.auth.dto.Tokens;

import static reactor.core.publisher.Mono.*;

@Component
@RequiredArgsConstructor
public class TokenHandler {
    private Mono<Map<String, Object>> getFacebookInfo(String token) {
        return externalToken
                .getFacebook()
                .verify(token)
                .flatMap(valid -> valid ? externalToken.getFacebook().getInfo(token) : empty());
    }

    private Mono<Map<String, Object>> getGoogleInfo(String token) {
        return externalToken
                .getGoogle()
                .verify(token)
                .flatMap(valid -> valid ? externalToken.getGoogle().getInfo(token) : empty());
    }

    private Mono<Map<String, Object>> getZaloInfo(String token) {
        return externalToken
                .getZalo()
                .verify(token)
                .flatMap(valid -> valid ? externalToken.getZalo().getInfo(token) : empty());
    }

    public Mono<Tokens> issueGuestToken(String sub, Map<String, Object> extraClaims) {
        Mono<String> accessMono = internalToken.issue(sub, Type.ACCESS, 3600, extraClaims);
        Mono<String> refreshMono = internalToken.issue(sub, Type.REFRESH, 2592000);
        return zip(accessMono, refreshMono).map(tuple -> new Tokens(tuple.getT1(), tuple.getT2()));
    }

    public Mono<String> getFacebookId(String token) {
        return getFacebookInfo(token).map(info -> info.get("id").toString());
    }

    public Mono<String> getGoogleId(String token) {
        return getGoogleInfo(token).map(info -> info.get("sub").toString());
    }

    public Mono<String> getZaloId(String token) {
        return getZaloInfo(token).map(info -> info.get("id").toString());
    }

    public Mono<Tokens> issueTokenByFacebook(String token, Map<String, Object> extraClaims) {
        return getFacebookId(token).map(id -> "facebook:" + id).flatMap(cred -> issueGuestToken(cred, extraClaims));
    }

    public Mono<Tokens> issueTokenByGoogle(String token, Map<String, Object> extraClaims) {
        return getGoogleId(token).map(id -> "google:" + id).flatMap(cred -> issueGuestToken(cred, extraClaims));
    }

    public Mono<Tokens> issueTokenByZalo(String token, Map<String, Object> extraClaims) {
        return getZaloId(token).map(id -> "zalo:" + id).flatMap(cred -> issueGuestToken(cred, extraClaims));
    }
}
