package com.ordep.aspmanagergateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Resposta de solicitação de espaço")
@Builder
public record SolicitacaoEspacoResponse(
        @Schema(description = "ID da solicitação", example = "10")
        Long id,

        @Schema(description = "Descrição da solicitação", example = "Aula prática de redes")
        String descricao,

        @Schema(description = "Data de uso", example = "2026-05-12", format = "date")
        LocalDate dataUso,

        @Schema(description = "Horário inicial", example = "08:00:00", format = "time")
        LocalTime horaInicio,

        @Schema(description = "Horário final", example = "10:00:00", format = "time")
        LocalTime horaFim,

        @Schema(description = "ID do espaço solicitado", example = "5")
        Long idEspaco,

        @Schema(description = "ID do professor solicitante", example = "12")
        Long idProfessor,

        @Schema(description = "Status da solicitação", example = "PENDENTE")
        String statusSolicitacao
) {
}
