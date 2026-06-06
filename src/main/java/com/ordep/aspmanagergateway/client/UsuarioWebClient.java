package com.ordep.aspmanagergateway.client;

import com.ordep.aspmanagergateway.dto.UsuarioResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class UsuarioWebClient {

    private final WebClient usuarioClient;

    public UsuarioWebClient(WebClient.Builder webBuilder) {
        this.usuarioClient = webBuilder
                .baseUrl("lb://ASPMANAGER-USUARIO-SERVICE")
                .build();
    }

    public Mono<UsuarioResponse> buscarPorId(Long id) {
        return usuarioClient.get()
                .uri("/api/v1/usuarios/{id}", id)
                .header("X-User-Id", "0")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new ResponseStatusException(resp.statusCode(),
                                "Usuário não encontrado: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new RuntimeException("Erro interno em ms-usuario ao buscar id: " + id)))
                .bodyToMono(UsuarioResponse.class);
    }

}
