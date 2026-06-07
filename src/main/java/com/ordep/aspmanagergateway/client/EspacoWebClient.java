package com.ordep.aspmanagergateway.client;

import com.ordep.aspmanagergateway.dto.EspacoResponse;
import com.ordep.aspmanagergateway.dto.SolicitacaoEspacoResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class EspacoWebClient {
    private final WebClient espacoClient;

    public EspacoWebClient(WebClient.Builder webBuilder) {
        this.espacoClient = webBuilder
                .baseUrl("lb://ASPMANAGER-ESPACO-SERVICE")
                .build();
    }

    public Mono<EspacoResponse> buscarEspacoPorId(Long id) {
        return espacoClient.get()
                .uri("/api/v1/espaco/{id}", id)
                .header("X-User-Id", "0")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new ResponseStatusException(resp.statusCode(),
                                "Espaço não encontrado: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new RuntimeException("Erro interno em ms-espaco ao buscar id: " + id)))
                .bodyToMono(EspacoResponse.class);
    }

    public Mono<SolicitacaoEspacoResponse> buscarSolicitacaoPorId(Long id) {
        return espacoClient.get()
                .uri("/api/v1/espaco/solicitacao/{id}", id)
                .header("X-User-Id", "0")
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new ResponseStatusException(resp.statusCode(),
                                "Solicitação de espaço não encontrada: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new RuntimeException("Erro interno em ms-espaco ao buscar solicitação id: " + id)))
                .bodyToMono(SolicitacaoEspacoResponse.class);
    }

}
