package com.ordep.aspmanagergateway.service;

import com.ordep.aspmanagergateway.client.EscolaWebClient;
import com.ordep.aspmanagergateway.client.EspacoWebClient;
import com.ordep.aspmanagergateway.client.SoftwareWebClient;
import com.ordep.aspmanagergateway.client.UsuarioWebClient;
import com.ordep.aspmanagergateway.dto.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class OrquestracaoService {

    private final UsuarioWebClient usuarioWebClient;
    private final EscolaWebClient escolaWebClient;
    private final SoftwareWebClient softwareWebClient;
    private final EspacoWebClient espacoWebClient;

    public OrquestracaoService(UsuarioWebClient usuarioWebClient, EscolaWebClient escolaWebClient, SoftwareWebClient softwareWebClient, EspacoWebClient espacoWebClient) {
        this.usuarioWebClient = usuarioWebClient;
        this.escolaWebClient = escolaWebClient;
        this.softwareWebClient = softwareWebClient;
        this.espacoWebClient = espacoWebClient;
    }

    public Mono<EscolaCompletoResponse> buscarEscolaCompleto(Long escolaId) {
        return escolaWebClient.buscarPorId(escolaId)
                .flatMap(escolaResponse -> usuarioWebClient.buscarPorId(escolaResponse.idCoordenador()).map(usuarioResponse -> EscolaCompletoResponse.builder()
                        .id(escolaResponse.id())
                        .coordenador(usuarioResponse)
                        .idInstituicao(escolaResponse.idInstituicao())
                        .idsDisciplinas(escolaResponse.idsDisciplinas())
                        .statusRegistro(escolaResponse.statusRegistro())
                        .build())
                );
    }

    public Mono<SolicitacaoEspacoCompletoResponse> buscarSolicitacaoEspacoCompleto(Long solicitacaoId) {
        return espacoWebClient.buscarSolicitacaoPorId(solicitacaoId)
                .flatMap(solicitacaoEspacoResponse ->
                        Mono.zip(
                                espacoWebClient.buscarEspacoPorId(solicitacaoEspacoResponse.idEspaco()),
                                usuarioWebClient.buscarPorId(solicitacaoEspacoResponse.idProfessor())
                        ).map(objects -> {
                            EspacoResponse t1 = objects.getT1();
                            UsuarioResponse t2 = objects.getT2();

                            return SolicitacaoEspacoCompletoResponse.builder()
                                    .id(solicitacaoEspacoResponse.id())
                                    .espaco(t1)
                                    .dataUso(solicitacaoEspacoResponse.dataUso())
                                    .horaInicio(solicitacaoEspacoResponse.horaInicio())
                                    .horaFim(solicitacaoEspacoResponse.horaFim())
                                    .professor(t2)
                                    .statusSolicitacao(solicitacaoEspacoResponse.statusSolicitacao())
                                    .descricao(solicitacaoEspacoResponse.descricao())
                                    .build();
                        })
                );
    }

    public Mono<SolicitacaoSoftwareCompletoResponse> buscarSolicitacaoSoftwareCompleto(Long solicitacaoId) {
        return softwareWebClient.buscarSolicitacaoPorId(solicitacaoId)
                .flatMap(solicitacaoSoftwareResponse -> Mono.zip(
                                        usuarioWebClient.buscarPorId(solicitacaoSoftwareResponse.idProfessor()),
                                        Flux.fromIterable(solicitacaoSoftwareResponse.disciplinaIds()).flatMap(escolaWebClient::buscarDisciplinaPorId).collectList()
                                )
                                .map(objects -> {
                                    UsuarioResponse t1 = objects.getT1();
                                    List<DisciplinaResponse> t2 = objects.getT2();

                                    return SolicitacaoSoftwareCompletoResponse.builder()
                                            .id(solicitacaoSoftwareResponse.id())
                                            .statusSolicitacao(solicitacaoSoftwareResponse.statusSolicitacao())
                                            .tipoSolicitacaoSoftware(solicitacaoSoftwareResponse.tipoSolicitacaoSoftware())
                                            .professor(t1)
                                            .nomeSoftware(solicitacaoSoftwareResponse.nome())
                                            .dataSolicitacao(solicitacaoSoftwareResponse.dataSolicitacao())
                                            .versaoSoftware(solicitacaoSoftwareResponse.versao())
                                            .disciplinas(t2)
                                            .build();
                                })
                );
    }
}
