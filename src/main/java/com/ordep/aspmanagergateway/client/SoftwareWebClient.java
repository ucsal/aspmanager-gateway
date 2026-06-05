package com.ordep.aspmanagergateway.client;

import com.ordep.aspmanagergateway.dto.SolicitacaoSoftwareResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class SoftwareWebClient {

    private final WebClient softwareClient;

    public SoftwareWebClient(WebClient.Builder webBuilder) {
        this.softwareClient = webBuilder
                .baseUrl("lb://ASPMANAGER-SOFTWARE-SERVICE")
                .build();
    }

    public Mono<SolicitacaoSoftwareResponse> buscarSolicitacaoPorId(Long id)  {
        return softwareClient.get()
                .uri("/api/v1/softwares/solicitacoes/{id}")
                .attribute("id", id)
                .retrieve()
                .bodyToMono(SolicitacaoSoftwareResponse.class)
                .onErrorResume(throwable -> Mono.error(new RuntimeException("Não foi possível buscar espaço por Id")));
    }

}
