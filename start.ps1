# start.ps1
Write-Host "Iniciando workspace ASPManager..." -ForegroundColor Cyan

# Dicionário com os repositórios (Chave = Pasta, Valor = URL Git)
$repos = [ordered]@{
    "aspmanager-service-discovery" = "git@github.com:ucsal/aspmanager-service-discovery.git"
    "aspmanager-auth-service"      = "git@github.com:ucsal/aspmanager-auth-service.git"
    "aspmanager-espaco-service"    = "git@github.com:ucsal/aspmanager-espaco-service.git"
    "aspmanager-usuario-service"   = "git@github.com:ucsal/aspmanager-usuario-service.git"
    "aspmanager-escola-service"    = "git@github.com:ucsal/aspmanager-escola-service.git"
    "aspmanager-software-service"  = "git@github.com:ucsal/aspmanager-software-service.git"
}

Write-Host "Verificando repositórios..."
foreach ($dir in $repos.Keys) {
    $url = $repos[$dir]
    $targetPath = "..\$dir"

    if (-Not (Test-Path -Path $targetPath)) {
        Write-Host "Clonando $dir..." -ForegroundColor Yellow
        git clone $url $targetPath
    } else {
        Write-Host "$dir já existe." -ForegroundColor DarkGray
    }
}

# Tratamento para a pasta do Postgres
if (-Not (Test-Path -Path "..\postgres")) {
    Write-Host "Criando estrutura de pastas do Postgres..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Force -Path "..\postgres" | Out-Null
    New-Item -ItemType File -Force -Path "..\postgres\init.sql" | Out-Null
}

Write-Host "Subindo os contêineres do Docker..." -ForegroundColor Cyan

# Verificando argumento --build: .\start.ps1 --build
$buildFlag = ""
if ($args -contains "--build") {
    $buildFlag = "--build"
    Write-Host "Modo rebuild ativado." -ForegroundColor Magenta
}

# Executa o Docker Compose
if ($buildFlag) {
    docker compose up -d --build
} else {
    docker compose up -d
}

# Checa se o comando anterior (Docker) deu certo ($LASTEXITCODE equivalente ao $? do Bash)
if ($LASTEXITCODE -eq 0) {
    Write-Host "Projetos rodando! Digite 'docker compose logs -f' para acompanhar os logs." -ForegroundColor Green
} else {
    Write-Host "Ocorreu um erro ao subir os contêineres. O processo foi interrompido." -ForegroundColor Red
    exit 1
}
