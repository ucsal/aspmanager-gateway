package com.ordep.aspmanagergateway.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.util.List;

@Schema(description = "Resposta de usuário")
@Builder
public record UsuarioResponse(
        @Schema(description = "ID do usuário", example = "12")
        Long id,

        @Schema(description = "Nome completo", example = "Ana Paula Souza")
        String nomeCompleto,

        @Schema(description = "Email", example = "ana.souza@ucsal.br")
        String email,

        @Schema(description = "Perfil de acesso", example = "PROFESSOR")
        String perfil,

        @Schema(description = "Status do registro", example = "ATIVO")
        String statusRegistro,

        @Schema(description = "Matrícula do professor quando aplicável", example = "202600123")
        String matricula,

        @Schema(description = "Identificador do professor quando aplicável", example = "1")
        Long idProfessor,

        @Schema(description = "Identificador de escola vinculada a professor quando aplicável", example = "1")
        Long idEscola,

        @Schema(description = "Telefones do usuário", example = "[\"71999998888\", \"7132017000\"]")
        List<String> telefones) {
}
