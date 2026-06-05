package com.ordep.aspmanagergateway.controller;

import com.ordep.aspmanagergateway.dto.EscolaCompletoResponse;
import com.ordep.aspmanagergateway.service.OrquestracaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/orq/escolas")
public class OrqEscolaController {

    private final OrquestracaoService orquestracaoService;

    public OrqEscolaController(OrquestracaoService orquestracaoService) {
        this.orquestracaoService = orquestracaoService;
    }

    @GetMapping("/{id}/completo")
    public Mono<ResponseEntity<EscolaCompletoResponse>> buscarEscolaCompleto(@PathVariable Long id) {
        return orquestracaoService.buscarEscolaCompleto(id).map(ResponseEntity::ok);
    }
}
