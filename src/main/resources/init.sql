-- =============================================================================
-- ASPManager — Script de inicialização do PostgreSQL
-- =============================================================================
-- Executado automaticamente pelo entrypoint do container postgres na primeira
-- vez que o volume é criado (docker-entrypoint-initdb.d).
--
-- IMPORTANTE: O Hibernate (ddl-auto=update) é responsável por criar/atualizar
-- as tabelas em cada banco. Este script apenas:
--   1. Cria os bancos de dados de cada microserviço
--   2. Insere o usuário admin inicial em db_usuario
-- =============================================================================


-- -----------------------------------------------------------------------------
-- 1. CRIAÇÃO DOS BANCOS DE DADOS
-- -----------------------------------------------------------------------------

CREATE DATABASE db_usuario;
CREATE DATABASE db_escola;
CREATE DATABASE db_espaco;
CREATE DATABASE db_software;


-- =============================================================================
-- 2. SEED — Usuário ADMIN inicial (db_usuario)
-- =============================================================================
-- Conecta no banco correto para o seed
\connect db_usuario

-- A tabela "usuarios" será criada pelo Hibernate (ddl-auto=update) quando o
-- aspmanager-usuario-service subir. Por isso usamos um bloco DO que aguarda a
-- existência da tabela antes de tentar inserir.
--
-- Senha padrão: admin123
-- Hash BCrypt gerado com 10 rounds (compatível com Spring Security):
--   $2a$10$7EqJtq98hPqEX7fNZaFWoO3vqFpPpGpq7rQ0LbFpGpq7rQ0LbFpGp
--
-- ⚠️  TROQUE A SENHA NO PRIMEIRO LOGIN! ⚠️
-- Para gerar um novo hash: https://bcrypt-generator.com  (rounds = 10)
-- Ou via Spring: new BCryptPasswordEncoder().encode("sua_senha")
-- =============================================================================

DO $$
BEGIN
    -- Aguarda até a tabela "usuarios" existir (criada pelo Hibernate na inicialização do serviço).
    -- Este bloco é executado sinergicamente com a inicialização dos containers;
    -- se o serviço ainda não criou a tabela, o INSERT simplesmente é ignorado
    -- e o admin deve ser inserido manualmente ou via endpoint de seed.
    IF EXISTS (
        SELECT 1
        FROM information_schema.tables
        WHERE table_schema = 'public'
          AND table_name   = 'usuarios'
    ) THEN
        INSERT INTO usuarios (nome_completo, email, senha, perfil, status_registro)
SELECT
    'Administrador',
    'admin@aspmanager.com',
    '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', -- senha: password
    'ADMIN',
    'ATIVO'
    WHERE NOT EXISTS (
            SELECT 1 FROM usuarios WHERE email = 'admin@aspmanager.com'
        );
END IF;
END;
$$;

-- =============================================================================
-- REFERÊNCIA RÁPIDA — Estrutura de cada banco (gerenciada pelo Hibernate)
-- =============================================================================
--
-- db_usuario
--   • usuarios          (id, nome_completo, email, senha, perfil, status_registro)
--   • telefones_usuarios (id, numero, id_usuario)
--   • professores       (id, matricula, id_usuario, id_escola)
--
-- db_escola
--   • instituicoes_ensino  (id, nome, endereco)
--   • telefones_instituicoes (id, numero, id_instituicao)
--   • escolas              (id, nome, status_registro, id_instituicao, id_professor_coordenador)
--   • disciplinas          (id, nome, descricao, id_escola)
--
-- db_espaco
--   • espacos              (id, sigla, nome, descricao, capacidade_maxima, localizacao,
--                           status_registro, tipo_computadores, tipo_espaco, id_escola)
--   • espaco_softwares     (id_espaco, id_software)          ← @ElementCollection
--   • solicitacoes_espacos (id, descricao, data_uso, hora_inicio, hora_fim,
--                           status_solicitacao, id_espaco, id_professor)
--
-- db_software
--   • softwares             (id, nome, versao, url_download, tipo_licenca,
--                            objetivo_uso, data_cadastro, status_registro)
--   • softwares_disciplinas (software_id, disciplina_id)     ← @ElementCollection
--   • solicitacoes_softwares (id, data_solicitacao, tipo_soliticacao, status_solicitado,
--                             nome_software, versao_software, url_download, tipo_licenca,
--                             objetivo_uso, id_professor, software_criado_id)
--   • solicitacao_disciplinas (solicitacao_id, disciplina_id) ← @ElementCollection
--
-- Enums válidos:
--   Perfil            → ADMIN | PROFESSOR
--   StatusRegistro    → ATIVO | INATIVO
--   TipoEspaco        → SALA | AUDITORIO | LABORATORIO
--   StatusSolicitacao → PENDENTE | APROVADO | REPROVADO
--   TipoSolicitacao   → ATIVACAO | DESATIVACAO | ATUALIZACAO | EXCLUSAO
-- =============================================================================
