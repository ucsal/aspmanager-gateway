package com.ordep.aspmanagergateway.client;

import com.ordep.aspmanagergateway.dto.UsuarioResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class UsuarioWebClient {

    private final WebClient usuarioClient;

    public UsuarioWebClient(WebClient.Builder webBuilder) {
        this.usuarioClient = WebClient.builder()
                .baseUrl("lb://ASPMANAGER-USUARIO-SERVICE")
                .build();
    }

    public Mono<UsuarioResponse> buscarPorId(Long id) {
        return usuarioClient.get()
                .uri("/api/v1/usuarios/{id}")
                .attribute("id", id)
                .retrieve()
                .bodyToMono(UsuarioResponse.class);
    }

}
