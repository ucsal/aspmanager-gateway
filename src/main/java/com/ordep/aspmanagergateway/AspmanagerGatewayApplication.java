package com.ordep.aspmanagergateway;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "ASPManager API - Gateway",
                version = "1.0.0",
                description = "API GATEWAY para gestão de instituições, escolas, usuários, espaços acadêmicos, " +
                        "softwares e solicitações de uso.\n\n" +
                        "Padrões para integração frontend:\n" +
                        "- Autenticação via JWT Bearer Token (obrigatória, exceto login).\n" +
                        "- Paginação com parâmetros page, size e sort nos endpoints listados.\n" +
                        "- Datas no formato ISO-8601 (yyyy-MM-dd).\n" +
                        "- Horários no formato ISO-8601 (HH:mm:ss).\n" +
                        "- Erros padronizados no modelo ErroApiResponse.",
                contact = @Contact(
                        name = "Gabriel Barreto - SCRUM MASTER do ASPManager",
                        email = "gabriel.fraga@ucsal.edu.br"
                ),
                license = @License(
                        name = "Uso acadêmico interno UCSAL"
                )
        ),
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT Token access",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class AspmanagerGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AspmanagerGatewayApplication.class, args);
    }

}
