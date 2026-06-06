package com.ordep.aspmanagergateway.filter;

import org.springframework.util.AntPathMatcher;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Centraliza as regras de autorização por cargo para todas as rotas dos microserviços.
 *
 * A ordem das entradas importa: o primeiro padrão que casar com o par (método, path)
 * é o que define os cargos permitidos. Coloque paths mais específicos antes dos genéricos.
 *
 * Regras extraídas do SecurityConfig.java do monolito aspmanager-api.
 */
public final class RoleAuthorizationRules {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    // "MÉTODO:PATH_PATTERN" → cargos permitidos
    private static final Map<String, List<String>> RULES = new LinkedHashMap<>();

    static {

        // ── ms-usuario (/api/v1/usuarios/**, /api/v1/usuarios/professores/**) ──

        // Endpoint interno chamado pelo ms-auth via Feign — sem cargo necessário
        RULES.put("GET:/api/v1/usuarios/email/**",                    List.of("ADMIN", "PROFESSOR"));

        // Operações exclusivas de ADMIN
        RULES.put("POST:/api/v1/usuarios",                            List.of("ADMIN"));
        RULES.put("GET:/api/v1/usuarios",                             List.of("ADMIN"));
        RULES.put("PATCH:/api/v1/usuarios/*",                         List.of("ADMIN"));
        RULES.put("DELETE:/api/v1/usuarios/*",                        List.of("ADMIN"));

        RULES.put("GET:/api/v1/usuarios/professores",                 List.of("ADMIN"));
        RULES.put("GET:/api/v1/usuarios/professores/*",               List.of("ADMIN"));
        RULES.put("PUT:/api/v1/usuarios/professores/*",               List.of("ADMIN"));
        RULES.put("DELETE:/api/v1/usuarios/professores/*",            List.of("ADMIN"));

        // Operações de qualquer autenticado (próprio usuário ou admin)
        RULES.put("GET:/api/v1/usuarios/*",                           List.of("ADMIN", "PROFESSOR"));
        RULES.put("PUT:/api/v1/usuarios/*",                           List.of("ADMIN", "PROFESSOR"));
        RULES.put("PATCH:/api/v1/usuarios/*/alterar-senha",           List.of("ADMIN", "PROFESSOR"));

        // ── ms-escola (/api/v1/instituicoes/**, /api/v1/escolas/**, /api/v1/disciplinas/**) ──

        // Operações exclusivas de ADMIN
        RULES.put("POST:/api/v1/instituicoes",                        List.of("ADMIN"));
        RULES.put("PUT:/api/v1/instituicoes/*",                       List.of("ADMIN"));
        RULES.put("PATCH:/api/v1/instituicoes/*",                     List.of("ADMIN"));
        RULES.put("DELETE:/api/v1/instituicoes/*",                    List.of("ADMIN"));

        RULES.put("POST:/api/v1/escolas",                             List.of("ADMIN"));
        RULES.put("PUT:/api/v1/escolas/*",                            List.of("ADMIN"));
        RULES.put("PATCH:/api/v1/escolas/*",                          List.of("ADMIN"));
        RULES.put("DELETE:/api/v1/escolas/*",                         List.of("ADMIN"));
        RULES.put("POST:/api/v1/escolas/*/disciplinas",               List.of("ADMIN"));

        RULES.put("PUT:/api/v1/disciplinas/*",                        List.of("ADMIN"));
        RULES.put("PATCH:/api/v1/disciplinas/*",                      List.of("ADMIN"));
        RULES.put("DELETE:/api/v1/disciplinas/*",                     List.of("ADMIN"));

        // Leitura disponível para ambos os cargos
        RULES.put("GET:/api/v1/instituicoes/**",                      List.of("ADMIN", "PROFESSOR"));
        RULES.put("GET:/api/v1/escolas/**",                           List.of("ADMIN", "PROFESSOR"));
        RULES.put("GET:/api/v1/disciplinas/**",                       List.of("ADMIN", "PROFESSOR"));

        // ── ms-espaco (/api/v1/espacos/**) ──────────────────────────────────────

        // Mais específicos primeiro
        RULES.put("POST:/api/v1/espacos/solicitacoes",                List.of("PROFESSOR"));
        RULES.put("GET:/api/v1/espacos/solicitacoes/minhas",          List.of("PROFESSOR"));
        RULES.put("GET:/api/v1/espacos/solicitacoes/**",              List.of("ADMIN"));
        RULES.put("PATCH:/api/v1/espacos/solicitacoes/**",            List.of("ADMIN"));
        RULES.put("DELETE:/api/v1/espacos/solicitacoes/**",           List.of("ADMIN"));

        // CRUD de espaços — só ADMIN gerencia, ambos consultam
        RULES.put("POST:/api/v1/espacos",                             List.of("ADMIN"));
        RULES.put("PUT:/api/v1/espacos/*",                            List.of("ADMIN"));
        RULES.put("PATCH:/api/v1/espacos/*",                          List.of("ADMIN"));
        RULES.put("DELETE:/api/v1/espacos/*",                         List.of("ADMIN"));
        RULES.put("GET:/api/v1/espacos/**",                           List.of("ADMIN", "PROFESSOR"));

        // ── ms-software (/api/v1/softwares/**) ──────────────────────────────────

        // Mais específicos primeiro
        RULES.put("POST:/api/v1/softwares/solicitacoes",              List.of("PROFESSOR"));
        RULES.put("GET:/api/v1/softwares/solicitacoes/minhas",        List.of("PROFESSOR"));
        RULES.put("GET:/api/v1/softwares/solicitacoes/**",            List.of("ADMIN"));
        RULES.put("PATCH:/api/v1/softwares/solicitacoes/**",          List.of("ADMIN"));
        RULES.put("DELETE:/api/v1/softwares/solicitacoes/**",         List.of("ADMIN"));

        // CRUD de softwares — só ADMIN gerencia, ambos consultam
        RULES.put("POST:/api/v1/softwares",                           List.of("ADMIN"));
        RULES.put("PUT:/api/v1/softwares/*",                          List.of("ADMIN"));
        RULES.put("PATCH:/api/v1/softwares/*",                        List.of("ADMIN"));
        RULES.put("DELETE:/api/v1/softwares/*",                       List.of("ADMIN"));
        RULES.put("GET:/api/v1/softwares/**",                         List.of("ADMIN", "PROFESSOR"));
    }

    private RoleAuthorizationRules() {}

    /**
     * Verifica se o cargo informado tem permissão para acessar o par (método, path).
     *
     * @param method  método HTTP (GET, POST, PUT, PATCH, DELETE)
     * @param path    path da requisição (ex: /api/v1/espacos/solicitacoes/5)
     * @param role    cargo do usuário autenticado (ex: "ADMIN", "PROFESSOR")
     * @return {@code true} se autorizado, {@code false} caso contrário
     */
    public static boolean isAuthorized(String method, String path, String role) {
        if (role == null || role.isBlank()) {
            return false;
        }

        return RULES.entrySet().stream()
                .filter(entry -> {
                    String[] parts = entry.getKey().split(":", 2);
                    String ruleMethod = parts[0];
                    String rulePattern = parts[1];
                    return ruleMethod.equalsIgnoreCase(method)
                            && PATH_MATCHER.match(rulePattern, path);
                })
                .findFirst()
                .map(entry -> entry.getValue().contains(role))
                .orElse(false); // nenhuma regra encontrada → nega por padrão
    }
}
