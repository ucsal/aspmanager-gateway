package com.ordep.aspmanagergateway.client;

import com.ordep.aspmanagergateway.dto.EspacoResponse;
import com.ordep.aspmanagergateway.dto.SolicitacaoEspacoResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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
                .uri("/api/v1/espacos/{id}")
                .attribute("id", id)
                .retrieve()
                .bodyToMono(EspacoResponse.class)
                .onErrorResume(throwable -> Mono.error(new RuntimeException("Não foi possível buscar espaço por Id")));
    }

    public Mono<SolicitacaoEspacoResponse> buscarSolicitacaoPorId(Long id) {
        return espacoClient.get()
                .uri("/api/v1/espacos/solicitacoes/{id}")
                .attribute("id", id)
                .retrieve()
                .bodyToMono(SolicitacaoEspacoResponse.class);
    }

}
