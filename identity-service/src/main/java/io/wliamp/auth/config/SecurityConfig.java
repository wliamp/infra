package io.wliamp.auth.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;
import reactor.core.publisher.Mono;
import io.wliamp.auth.compo.property.BypassProperties;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        return authentication -> Mono.just(new UsernamePasswordAuthenticationToken("test-user", "N/A", List.of()));
    }

    @Bean
    public ServerSecurityContextRepository securityContextRepository(ReactiveAuthenticationManager manager) {
        return new WebSessionServerSecurityContextRepository();
        // return new ServerSecurityContextRepository() { ... };
    }

    private final BypassProperties bypassProperties;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            ReactiveAuthenticationManager authManager,
            ServerSecurityContextRepository securityContextRepository) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authenticationManager(authManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(
                        ex -> ex.pathMatchers("/auth/**")
                                .access((authentication, context) -> {
                                    ServerHttpRequest request =
                                            context.getExchange().getRequest();
                                    HttpHeaders headers = request.getHeaders();
                                    for (String header : bypassProperties.getBypassHeaders()) {
                                        if (headers.containsKey(header)) {
                                            return Mono.just(new AuthorizationDecision(true));
                                            ////                              Optional:
                                            //                                    String expectedToken =
                                            // bypassProperties.getBypassTokens().get(header);
                                            //                                    if (expectedToken == null ||
                                            // expectedToken.equals(headers.getFirst(header))) {
                                            //                                        return Mono.just(new
                                            // AuthorizationDecision(true));
                                            //                                    }
                                        }
                                    }
                                    return Mono.justOrEmpty(authentication)
                                            .map(auth -> new AuthorizationDecision(true))
                                            .defaultIfEmpty(new AuthorizationDecision(false));
                                })
                                .anyExchange()
                                .permitAll() // ongoing dev/test
                        ////                        When Production use
                        //                         .authenticated()
                        ////                        -> need to handle ReactiveAuthenticationManager,
                        // ServerSecurityContextRepository
                        )
                .build();
    }

    //    @Bean
    //    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
    //        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
    //                .authorizeExchange(
    //                        ex -> ex.anyExchange().permitAll()
    //                        )
    //                .build();
    //    }
}
