package com.ordep.aspmanagergateway.controller;

import com.ordep.aspmanagergateway.dto.SolicitacaoEspacoCompletoResponse;
import com.ordep.aspmanagergateway.service.OrquestracaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orq/espacos")
public class OrqEspacoController {

    private final OrquestracaoService orquestracaoService;

    public OrqEspacoController(OrquestracaoService orquestracaoService) {
        this.orquestracaoService = orquestracaoService;
    }

    @GetMapping("/solicitacoes/{id}/completo")
    public Mono<ResponseEntity<SolicitacaoEspacoCompletoResponse>> buscarSolicitacaoEspacoCompleto(@PathVariable Long id) {
        return orquestracaoService.buscarSolicitacaoEspacoCompleto(id).map(ResponseEntity::ok);
    }
}
