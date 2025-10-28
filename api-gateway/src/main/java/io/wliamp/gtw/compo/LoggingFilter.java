package io.wliamp.gtw.compo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RefreshScope
@Slf4j
@Component
public class LoggingFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String traceId = UUID.randomUUID().toString();
        ServerHttpRequest requestWithTraceId = exchange.getRequest().mutate()
                .header("X-Trace-Id", traceId)
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(requestWithTraceId)
                .build();
        long startTime = System.currentTimeMillis();
        log.info("[TRACE {}] --> {} {}", traceId,
                exchange.getRequest().getMethod(),
                exchange.getRequest().getURI());
        return chain.filter(mutatedExchange)
                .doOnSuccess(done -> {
                    long duration = System.currentTimeMillis() - startTime;
                    log.info("[TRACE {}] <-- Completed in {} ms", traceId, duration);
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
