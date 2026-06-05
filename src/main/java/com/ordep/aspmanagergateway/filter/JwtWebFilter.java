package com.ordep.aspmanagergateway.filter;

import com.ordep.aspmanagergateway.dto.UsuarioAuthResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * WebFilter para proteger endpoints locais do gateway (ex: /api/v1/orq/**).
 * Reusa a mesma regra de autorização central (RoleAuthorizationRules) utilizada
 * para as rotas proxied. Retorna 401 quando não autenticado e 403 quando sem role.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtWebFilter implements WebFilter {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private final WebClient authClient;

    public JwtWebFilter(WebClient.Builder webClientBuilder) {
        this.authClient = webClientBuilder
                .baseUrl("lb://ASPMANAGER-AUTH-SERVICE")
                .build();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String requestPath = exchange.getRequest().getPath().pathWithinApplication().value();

        // proteger somente endpoints orquestração do gateway
        if (!PATH_MATCHER.matchStart("/api/v1/orq/**", requestPath)) {
            return chain.filter(exchange);
        }

        String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorizationHeader)) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        return authClient.get()
                .uri("/api/auth/validate")
                .accept(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                .retrieve()
                .bodyToMono(UsuarioAuthResponse.class)
                .flatMap(usuarioAuthResponse -> {
                    if (usuarioAuthResponse == null) {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return exchange.getResponse().setComplete();
                    }

                    String method = exchange.getRequest().getMethod().name();
                    String path   = requestPath;
                    String role   = Objects.toString(usuarioAuthResponse.perfil(), "");

                    if (!RoleAuthorizationRules.isAuthorized(method, path, role)) {
                        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                        return exchange.getResponse().setComplete();
                    }

                    ServerWebExchange mutatedExchange = exchange.mutate()
                            .request(exchange.getRequest().mutate()
                                    .headers(headers -> {
                                        headers.set("X-User-Id", String.valueOf(usuarioAuthResponse.id()));
                                        headers.set("X-User-Role", role);
                                    })
                                    .build())
                            .build();

                    return chain.filter(mutatedExchange);
                })
                .onErrorResume(throwable -> {
                    exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    return exchange.getResponse().setComplete();
                });
    }
}
