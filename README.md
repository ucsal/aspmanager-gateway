# ASPManager Gateway

Este e o servico de API Gateway do projeto ASPManager, responsavel por centralizar o roteamento, orquestracao e seguranca (validacao de tokens JWT) para todos os outros microsservicos. Ele tambem agrupa a documentacao Swagger de todos os servicos.

## Requisitos

- Java 21
- Maven
- Docker e Docker Compose

## Como rodar o projeto

O gateway inclui scripts que facilitam a inicializacao de toda a arquitetura de microsservicos e infraestrutura de banco de dados associada. Estao disponiveis scripts para Linux/Mac (`.sh`) e Windows (`.bat`).

### Ambiente Local (Desenvolvimento Nativo)

Para rodar apenas o Gateway localmente via Maven (exigira que o Eureka e os demais servicos estejam sendo executados separadamente):

1. Certifique-se de configurar as variaveis de ambiente no arquivo `application.yml` ou em sua IDE:
   - `EUREKA_URL`: URL do Service Discovery (ex: `http://localhost:8761/eureka`)
   - `JWT_SECRET`: Chave secreta para validacao de tokens.
2. Execute o projeto via Maven:
   ```bash
   mvn spring-boot:run
   ```

### Ambiente Dockerizado (Recomendado)

A melhor forma de subir toda a infraestrutura, incluindo o banco de dados PostgreSQL e todos os microsservicos, e usando o Docker Compose atraves dos scripts fornecidos na pasta do Gateway.

1. Configure um arquivo `.env` na pasta do Gateway contendo as configuracoes de banco e seguranca necessarias:
   ```env
   POSTGRES_USER=postgres
   POSTGRES_PASSWORD=postgres
   EUREKA_URL=http://aspmanager-service-discovery:8761/eureka
   JWT_SECRET=sua-chave-secreta-aqui
   SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/
   SPRING_DATASOURCE_USERNAME=postgres
   SPRING_DATASOURCE_PASSWORD=postgres
   ```

2. Execute o script de inicializacao correspondente ao seu sistema operacional:
   - **Linux / Mac**:
     ```bash
     ./start.sh
     ```
   - **Windows**:
     ```cmd
     start.bat
     ```
   
   O script ira verificar se todos os repositorios dos microsservicos estao clonados na pasta pai (clonando-os se necessario), criara a pasta do postgres copiando o arquivo `init.sql` inicial e, em seguida, subira os conteineres do Docker em background.

   **Flag `--build`**: 
   Caso voce tenha feito alteracoes no codigo de algum microsservico e precise que as imagens Docker sejam recriadas com o codigo mais recente, execute o script passando a flag `--build`:
   - Linux/Mac: `./start.sh --build`
   - Windows: `start.bat --build`

3. Para acompanhar os logs, execute:
   ```bash
   docker compose logs -f
   ```

4. Para parar e remover os conteineres, execute o script de parada:
   - **Linux / Mac**:
     ```bash
     ./stop.sh
     ```
   - **Windows**:
     ```cmd
     stop.bat
     ```

## Acesso as Documentacoes (Swagger)

Apos subir a infraestrutura completa, a documentacao unificada estara disponivel no Gateway:

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

A partir da interface, voce podera selecionar no menu suspenso (canto superior direito) qual API deseja explorar (Gateway, Auth, Usuario, Escola, Espaco, Software) e podera realizar testes se autenticando pelo botao "Authorize".
