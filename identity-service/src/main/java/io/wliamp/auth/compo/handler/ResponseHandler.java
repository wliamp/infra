package io.wliamp.auth.compo.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import io.wliamp.auth.dto.Tokens;

@Component
public class ResponseHandler {
    public Mono<ServerResponse> buildTokenResponse(Tokens token) {
        return ServerResponse.ok()
                .header("X-Access-Token", token.access())
                .header("X-Refresh-Token", token.refresh())
                .build();
    }
}
