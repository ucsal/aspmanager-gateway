package com.ordep.aspmanagergateway.filter;

import com.ordep.aspmanagergateway.dto.UsuarioAuthResponse;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Component
public class JwtAuthFilter extends AbstractGatewayFilterFactory<JwtAuthFilter.Config> {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private final WebClient authClient;

    public JwtAuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.authClient = webClientBuilder
                .baseUrl("lb://ASPMANAGER-AUTH-SERVICE")
                .build();
    }

    @Override
    public GatewayFilter apply(Config config) {
        List<String> excludePaths = config.excludePaths();

        return (exchange, chain) -> {
            String requestPath = exchange.getRequest().getPath().pathWithinApplication().value();

            if (isExcluded(requestPath, excludePaths)) {
                return chain.filter(exchange);
            }

            String authorizationHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (!StringUtils.hasText(authorizationHeader)) {
                return unauthorized(exchange);
            }

            return authClient.get()
                    .uri("/api/auth/validate")
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader)
                    .retrieve()
                    .bodyToMono(UsuarioAuthResponse.class)
                    .flatMap(usuarioAuthResponse -> {
                        if (usuarioAuthResponse == null) {
                            return unauthorized(exchange);
                        }

                        String method = exchange.getRequest().getMethod().name();
                        String path   = exchange.getRequest().getPath().pathWithinApplication().value();
                        String role   = Objects.toString(usuarioAuthResponse.perfil(), "");

                        if (!RoleAuthorizationRules.isAuthorized(method, path, role)) {
                            return forbidden(exchange);
                        }

                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(exchange.getRequest().mutate()
                                        .headers(headers -> {
                                            headers.remove(HttpHeaders.AUTHORIZATION);
                                            headers.set("X-User-Id",   String.valueOf(usuarioAuthResponse.id()));
                                            headers.set("X-User-Role", role);
                                        })
                                        .build())
                                .build();

                        return chain.filter(mutatedExchange);
                    })
                    .onErrorResume(throwable -> unauthorized(exchange));
        };
    }

    private static boolean isExcluded(String requestPath, List<String> excludePaths) {
        return excludePaths.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .anyMatch(pattern -> PATH_MATCHER.match(pattern, requestPath));
    }

    private static Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private static Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return List.of("excludePaths");
    }

    public static class Config {

        private String excludePaths;

        public String getExcludePaths() {
            return excludePaths;
        }

        public void setExcludePaths(String excludePaths) {
            this.excludePaths = excludePaths;
        }

        public List<String> excludePaths() {
            if (!StringUtils.hasText(excludePaths)) {
                return List.of();
            }

            return List.of(excludePaths.split(","));
        }
    }
}
