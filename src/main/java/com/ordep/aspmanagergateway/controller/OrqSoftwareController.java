package com.ordep.aspmanagergateway.controller;

import com.ordep.aspmanagergateway.dto.SolicitacaoSoftwareCompletoResponse;
import com.ordep.aspmanagergateway.service.OrquestracaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orq/software")
public class OrqSoftwareController {

    private final OrquestracaoService orquestracaoService;

    public OrqSoftwareController(OrquestracaoService orquestracaoService) {
        this.orquestracaoService = orquestracaoService;
    }

    @GetMapping("/solicitacao/{id}/completo")
    public Mono<ResponseEntity<SolicitacaoSoftwareCompletoResponse>> buscarSolicitacaoEspacoCompleto(@PathVariable Long id) {
        return orquestracaoService.buscarSolicitacaoSoftwareCompleto(id).map(ResponseEntity::ok);
    }

}
