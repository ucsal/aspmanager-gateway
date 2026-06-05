package com.ordep.aspmanagergateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Resposta de solicitação de software")
@Builder
public record SolicitacaoSoftwareResponse(
        @Schema(description = "ID da solicitação", example = "21")
        Long id,

        @Schema(description = "Data da solicitação", example = "2026-04-23", format = "date")
        LocalDate dataSolicitacao,

        @Schema(description = "Tipo da solicitação de software", example = "ATIVACAO")
        String tipoSolicitacaoSoftware,

        @Schema(description = "Status da solicitação", example = "PENDENTE")
        String statusSolicitacao,

        @Schema(description = "Nome do software", example = "IntelliJ IDEA")
        String nome,

        @Schema(description = "Versão do software", example = "2026.1")
        String versao,

        @Schema(description = "URL de download do software", example = "https://www.jetbrains.com/idea/download")
        String urlDownload,

        @Schema(description = "Tipo de licença do software", example = "Educacional")
        String tipoLicenca,

        @Schema(description = "Objetivo de uso do software", example = "Apoio às disciplinas de programação e engenharia de software")
        String objetivoUso,

        @Schema(description = "ID do professor solicitante", example = "12")
        Long idProfessor,

        @Schema(description = "IDs das disciplinas relacionadas", example = "[3, 8]")
        List<Long> disciplinaIds

) {
}
