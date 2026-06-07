package com.ordep.aspmanagergateway.client;

import com.ordep.aspmanagergateway.dto.SolicitacaoSoftwareResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class SoftwareWebClient {

    private final WebClient softwareClient;

    public SoftwareWebClient(WebClient.Builder webBuilder) {
        this.softwareClient = webBuilder
                .baseUrl("lb://ASPMANAGER-SOFTWARE-SERVICE")
                .build();
    }

    public Mono<SolicitacaoSoftwareResponse> buscarSolicitacaoPorId(Long id) {
        return softwareClient.get()
                .uri("/api/v1/software/solicitacao/{id}", id)
                .header("X-User-Id", "0")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new ResponseStatusException(resp.statusCode(),
                                "Solicitação de software não encontrada: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new RuntimeException("Erro interno em ms-software ao buscar solicitação id: " + id)))
                .bodyToMono(SolicitacaoSoftwareResponse.class);
    }

}
