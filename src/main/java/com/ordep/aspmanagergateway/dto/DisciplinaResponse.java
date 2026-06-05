package com.ordep.aspmanagergateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Schema(description = "Resposta de disciplina")
@Builder
public record DisciplinaResponse(
        @Schema(description = "ID da disciplina", example = "5")
        Long id,

        @Schema(description = "Nome da disciplina", example = "Estruturas de Dados")
        String nome,

        @Schema(description = "Descrição da disciplina", example = "Conteúdos de listas, filas, pilhas e árvores.")
        String descricao,

        @Schema(description = "ID da escola vinculada", example = "2")
        Long idEscola
) {
}
