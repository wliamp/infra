package io.wliamp.auth.service.data;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import io.wliamp.auth.compo.handler.CacheHandler;
import io.wliamp.auth.dto.Tokens;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final CacheHandler cacheHandler;

    public Mono<Tokens> loadUserToken(String key) {
        return cacheHandler.get(key, Object.class).flatMap(obj -> switch (obj) {
            case Tokens token -> Mono.just(token);
            case Map<?, ?> map -> Mono.just(new Tokens((String) map.get("access"), (String) map.get("refresh")));
            case null, default -> Mono.empty();
        });
    }
}
