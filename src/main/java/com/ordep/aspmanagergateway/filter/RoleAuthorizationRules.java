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

        // ── ms-usuario (/usuarios/**, /professores/**) ────────────────────────

        // Endpoint interno chamado pelo ms-auth via Feign — sem cargo necessário
        RULES.put("GET:/usuarios/email/**",                    List.of("ADMIN", "PROFESSOR"));

        // Operações exclusivas de ADMIN
        RULES.put("POST:/usuarios",                            List.of("ADMIN"));
        RULES.put("GET:/usuarios",                             List.of("ADMIN"));
        RULES.put("PATCH:/usuarios/*",                         List.of("ADMIN"));
        RULES.put("DELETE:/usuarios/*",                        List.of("ADMIN"));

        RULES.put("GET:/professores",                          List.of("ADMIN"));
        RULES.put("GET:/professores/*",                        List.of("ADMIN"));
        RULES.put("PUT:/professores/*",                        List.of("ADMIN"));
        RULES.put("DELETE:/professores/*",                     List.of("ADMIN"));

        // Operações de qualquer autenticado (próprio usuário ou admin)
        RULES.put("GET:/usuarios/*",                           List.of("ADMIN", "PROFESSOR"));
        RULES.put("PUT:/usuarios/*",                           List.of("ADMIN", "PROFESSOR"));
        RULES.put("PATCH:/usuarios/*/alterar-senha",           List.of("ADMIN", "PROFESSOR"));

        // ── ms-escola (/instituicoes/**, /escolas/**, /disciplinas/**) ─────────

        // Operações exclusivas de ADMIN
        RULES.put("POST:/instituicoes",                        List.of("ADMIN"));
        RULES.put("PUT:/instituicoes/*",                       List.of("ADMIN"));
        RULES.put("PATCH:/instituicoes/*",                     List.of("ADMIN"));
        RULES.put("DELETE:/instituicoes/*",                    List.of("ADMIN"));

        RULES.put("POST:/escolas",                             List.of("ADMIN"));
        RULES.put("PUT:/escolas/*",                            List.of("ADMIN"));
        RULES.put("PATCH:/escolas/*",                          List.of("ADMIN"));
        RULES.put("DELETE:/escolas/*",                         List.of("ADMIN"));
        RULES.put("POST:/escolas/*/disciplinas",               List.of("ADMIN"));

        RULES.put("PUT:/disciplinas/*",                        List.of("ADMIN"));
        RULES.put("PATCH:/disciplinas/*",                      List.of("ADMIN"));
        RULES.put("DELETE:/disciplinas/*",                     List.of("ADMIN"));

        // Leitura disponível para ambos os cargos
        RULES.put("GET:/instituicoes/**",                      List.of("ADMIN", "PROFESSOR"));
        RULES.put("GET:/escolas/**",                           List.of("ADMIN", "PROFESSOR"));
        RULES.put("GET:/disciplinas/**",                       List.of("ADMIN", "PROFESSOR"));

        // ── ms-espaco (/espacos/**) ───────────────────────────────────────────

        // Mais específicos primeiro
        RULES.put("POST:/espacos/solicitacoes",                List.of("PROFESSOR"));
        RULES.put("GET:/espacos/solicitacoes/minhas",          List.of("PROFESSOR"));
        RULES.put("GET:/espacos/solicitacoes/**",              List.of("ADMIN"));
        RULES.put("PATCH:/espacos/solicitacoes/**",            List.of("ADMIN"));
        RULES.put("DELETE:/espacos/solicitacoes/**",           List.of("ADMIN"));

        // CRUD de espaços — só ADMIN gerencia, ambos consultam
        RULES.put("POST:/espacos",                             List.of("ADMIN"));
        RULES.put("PUT:/espacos/*",                            List.of("ADMIN"));
        RULES.put("PATCH:/espacos/*",                          List.of("ADMIN"));
        RULES.put("DELETE:/espacos/*",                         List.of("ADMIN"));
        RULES.put("GET:/espacos/**",                           List.of("ADMIN", "PROFESSOR"));

        // ── ms-software (/softwares/**) ───────────────────────────────────────

        // Mais específicos primeiro
        RULES.put("POST:/softwares/solicitacoes",              List.of("PROFESSOR"));
        RULES.put("GET:/softwares/solicitacoes/minhas",        List.of("PROFESSOR"));
        RULES.put("GET:/softwares/solicitacoes/**",            List.of("ADMIN"));
        RULES.put("PATCH:/softwares/solicitacoes/**",          List.of("ADMIN"));
        RULES.put("DELETE:/softwares/solicitacoes/**",         List.of("ADMIN"));

        // CRUD de softwares — só ADMIN gerencia, ambos consultam
        RULES.put("POST:/softwares",                           List.of("ADMIN"));
        RULES.put("PUT:/softwares/*",                          List.of("ADMIN"));
        RULES.put("PATCH:/softwares/*",                        List.of("ADMIN"));
        RULES.put("DELETE:/softwares/*",                       List.of("ADMIN"));
        RULES.put("GET:/softwares/**",                         List.of("ADMIN", "PROFESSOR"));
    }

    private RoleAuthorizationRules() {}

    /**
     * Verifica se o cargo informado tem permissão para acessar o par (método, path).
     *
     * @param method  método HTTP (GET, POST, PUT, PATCH, DELETE)
     * @param path    path da requisição (ex: /espacos/solicitacoes/5)
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
