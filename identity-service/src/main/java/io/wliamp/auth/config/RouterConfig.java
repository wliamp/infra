package io.wliamp.auth.config;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import io.wliamp.auth.compo.handler.RouteHandler;

@Configuration
@RequiredArgsConstructor
public class RouterConfig {
    private final RouteHandler routeHandler;

    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return route().nest(path("/auth"), () -> route().POST("/guest", routeHandler::guest)
                        .nest(path("/{party}"), () -> route().POST("/login", routeHandler::login)
                                .POST("/link", routeHandler::link)
                                .build())
                        .POST("/relog", routeHandler::relog)
                        .POST("/logout", routeHandler::logout)
                        .build())
                .build();
    }
}
