package com.ordep.aspmanagergateway.client;

import com.ordep.aspmanagergateway.dto.DisciplinaResponse;
import com.ordep.aspmanagergateway.dto.EscolaResponse;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
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
                .uri("/api/v1/escolas/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new ResponseStatusException(resp.statusCode(),
                                "Escola não encontrada: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new RuntimeException("Erro interno em ms-escola ao buscar id: " + id)))
                .bodyToMono(EscolaResponse.class);
    }

    public Mono<DisciplinaResponse> buscarDisciplinaPorId(Long id) {
        return escolaClient.get()
                .uri("/api/v1/disciplinas/{id}", id)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        resp -> Mono.error(new ResponseStatusException(resp.statusCode(),
                                "Disciplina não encontrada: " + id)))
                .onStatus(HttpStatusCode::is5xxServerError,
                        resp -> Mono.error(new RuntimeException("Erro interno em ms-escola ao buscar disciplina id: " + id)))
                .bodyToMono(DisciplinaResponse.class);
    }
}
