@echo off
echo Iniciando workspace ASPManager...

set REPOS=aspmanager-service-discovery aspmanager-auth-service aspmanager-espaco-service aspmanager-usuario-service aspmanager-escola-service aspmanager-software-service

echo Verificando repositorios...
for %%R in (%REPOS%) do (
    if not exist "..\%%R" (
        echo Clonando %%R...
        git clone git@github.com:ucsal/%%R.git "..\%%R"
    ) else (
        echo %%R ja existe.
    )
)

if not exist "..\postgres" (
    echo Criando estrutura de pastas do Postgres...
    mkdir "..\postgres"
    copy "src\main\resources\init.sql" "..\postgres\init.sql" >nul
)

echo Subindo os conteineres do Docker...

set BUILD_FLAG=
if "%1"=="--build" (
    set BUILD_FLAG=--build
    echo Modo rebuild ativado.
)

docker compose up -d %BUILD_FLAG%
if %errorlevel% neq 0 (
    echo Ocorreu um erro ao subir os conteineres. O processo foi interrompido.
    exit /b %errorlevel%
)

echo Projetos rodando! Digite 'docker compose logs -f' para acompanhar os logs.
