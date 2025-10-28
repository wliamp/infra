package io.wliamp.auth.compo.helper;

import java.util.Map;
import reactor.core.publisher.Mono;
import io.wliamp.auth.dto.Tokens;

public interface PartyHelper {
    Mono<String> getSubject(String token);

    Mono<Tokens> issueToken(String token, Map<String, Object> claims);
}
