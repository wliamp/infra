package io.wliamp.auth.compo.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import io.wliamp.auth.service.authenticate.AuthenticateService;

@Component
@RequiredArgsConstructor
public class RouteHandler {
    private final AuthenticateService authenticateService;

    private final ResponseHandler responseHandler;

    public Mono<ServerResponse> guest(ServerRequest request) {
        return authenticateService.guestLogin().flatMap(responseHandler::buildTokenResponse);
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        String party = request.pathVariable("party");
        return request.bodyToMono(String.class)
                .flatMap(external -> authenticateService.loginWithoutHeader(party, external))
                .flatMap(responseHandler::buildTokenResponse);
    }

    public Mono<ServerResponse> relog(ServerRequest request) {
        String token = request.headers().firstHeader("X-Refresh-Token");
        if (token == null) {
            return ServerResponse.badRequest().build();
        }
        return authenticateService.loginWithHeader(token).flatMap(responseHandler::buildTokenResponse);
    }

    public Mono<ServerResponse> link(ServerRequest request) {
        String oldToken = request.headers().firstHeader("X-Refresh-Token");
        String party = request.pathVariable("party");
        return request.bodyToMono(String.class)
                .flatMap(newToken -> {
                    assert oldToken != null;
                    return authenticateService.linkAccount(party, oldToken, newToken);
                })
                .flatMap(responseHandler::buildTokenResponse);
    }

    public Mono<ServerResponse> logout(ServerRequest request) {
        String token = request.headers().firstHeader("X-Refresh-Token");
        assert token != null;
        return authenticateService.logout(token).then(ServerResponse.ok().build());
    }
}
