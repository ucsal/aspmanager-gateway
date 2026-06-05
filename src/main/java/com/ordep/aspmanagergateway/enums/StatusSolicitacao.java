package com.ordep.aspmanagergateway.enums;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Status de análise de uma solicitação")
public enum StatusSolicitacao {
    PENDENTE,
    APROVADO,
    REPROVADO
}
