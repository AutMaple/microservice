package com.autmaple.filters;

import brave.Span;
import brave.Tracer;
import brave.propagation.TraceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ResponseFilter implements GlobalFilter {
    private final Tracer tracer;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String tracingId = Optional.ofNullable(tracer.currentSpan())
                .map(Span::context)
                .map(TraceContext::traceIdString)
                .orElse("null");
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.warn("Adding the correlation id to the outbound headers: {}", tracingId);
            exchange.getResponse().getHeaders().add(FilterUtils.CORRELATION_ID, tracingId);
            log.warn("Completing outgoing request for {}", exchange.getRequest().getURI());
        }));
    }
}
