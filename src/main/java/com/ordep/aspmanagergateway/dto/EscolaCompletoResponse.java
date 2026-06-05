package com.ordep.aspmanagergateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Resposta composta de escola")
@Builder
public record EscolaCompletoResponse(
        @Schema(description = "ID da escola", example = "2")
        Long id,

        @Schema(description = "Nome da escola", example = "Escola de Ciências Sociais e Aplicadas")
        String nome,

        @Schema(description = "Status do registro da escola", example = "ATIVO")
        String statusRegistro,

        @Schema(description = "ID da instituição de ensino vinculada", example = "1")
        Long idInstituicao,

        @Schema(description = "IDs de disciplinas associadas", example = "[3, 8, 15]")
        List<Long> idsDisciplinas,

        @Schema(description = "Coordenador completo da escola")
        UsuarioResponse coordenador
) {
}
