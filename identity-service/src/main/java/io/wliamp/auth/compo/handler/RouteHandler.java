package io.wliamp.auth.compo.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import io.wliamp.auth.service.authenticate.AuthenticateService;

import static java.util.Objects.*;
import static java.util.Optional.*;

@Component
@RequiredArgsConstructor
public class RouteHandler {
    private final AuthenticateService authenticateService;

    private final ResponseHandler responseHandler;

    public Mono<ServerResponse> guest(ServerRequest request) {
        return authenticateService.guestLogin().flatMap(responseHandler::buildTokenResponse);
    }

    public Mono<ServerResponse> login(ServerRequest request) {
        var party = request.pathVariable("party");
        return request.bodyToMono(String.class)
                .flatMap(external -> authenticateService.loginWithoutHeader(party, external))
                .flatMap(responseHandler::buildTokenResponse);
    }

    public Mono<ServerResponse> relog(ServerRequest request) {
        var token = request.headers().firstHeader("X-Refresh-Token");
        return ofNullable(token).map(s -> authenticateService.loginWithHeader(s).flatMap(responseHandler::buildTokenResponse)).orElseGet(() -> ServerResponse.badRequest().build());
    }

    public Mono<ServerResponse> link(ServerRequest request) {
        var oldToken = request.headers().firstHeader("X-Refresh-Token");
        var party = request.pathVariable("party");
        return request.bodyToMono(String.class)
                .flatMap(newToken -> {
                    requireNonNull(oldToken);
                    return authenticateService.linkAccount(party, oldToken, newToken);
                })
                .flatMap(responseHandler::buildTokenResponse);
    }

    public Mono<ServerResponse> logout(ServerRequest request) {
        var token = request.headers().firstHeader("X-Refresh-Token");
        requireNonNull(token);
        return authenticateService.logout(token).then(ServerResponse.ok().build());
    }
}
