package com.ordep.aspmanagergateway.client;

import com.ordep.aspmanagergateway.dto.DisciplinaResponse;
import com.ordep.aspmanagergateway.dto.EscolaResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class EscolaWebClient {

    private final WebClient escolaClient;

    public EscolaWebClient(WebClient.Builder webBuilder) {
        this.escolaClient = webBuilder
                .baseUrl("lb://ASPMANAGER-ESCOLA-SERVICE")
                .build();
    }

    public Mono<EscolaResponse> buscarPorId(Long id) {
        return escolaClient.get()
                .uri("/api/v1/escolas/{id}")
                .attribute("id", id)
                .retrieve()
                .bodyToMono(EscolaResponse.class);
    }

    public Mono<DisciplinaResponse> buscarDisciplinaPorId(Long id) {
        return escolaClient.get()
                .uri("/api/v1/disciplinas/{id}")
                .attribute("id", id)
                .retrieve()
                .bodyToMono(DisciplinaResponse.class);
    }
}
