package com.ordep.aspmanagergateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Resposta composta de solicitação de software")
@Builder
public record SolicitacaoSoftwareCompletoResponse(
        @Schema(description = "ID da solicitação", example = "21")
        Long id,

        @Schema(description = "Data da solicitação", example = "2026-04-23", format = "date")
        LocalDate dataSolicitacao,

        @Schema(description = "Tipo da solicitação de software", example = "ATIVACAO")
        String tipoSolicitacaoSoftware,

        @Schema(description = "Status da solicitação", example = "PENDENTE")
        String statusSolicitacao,

        @Schema(description = "Nome do software", example = "IntelliJ IDEA")
        String nomeSoftware,

        @Schema(description = "Versão do software", example = "2026.1")
        String versaoSoftware,

        @Schema(description = "Professor solicitante completo")
        UsuarioResponse professor,

        @Schema(description = "Lista completa de disciplinas")
        List<DisciplinaResponse> disciplinas
) {
}
