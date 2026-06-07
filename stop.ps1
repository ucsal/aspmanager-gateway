# stop.ps1
Write-Host "Encerrando a stack de microserviços ASPManager..." -ForegroundColor Cyan

# O comando down para os contêineres e remove a rede virtual criada
docker compose down

# Checa se o encerramento ocorreu sem erros
if ($LASTEXITCODE -eq 0) {
    Write-Host "Ambiente encerrado com sucesso." -ForegroundColor Green
} else {
    Write-Host "Ocorreu um erro ao tentar encerrar o ambiente." -ForegroundColor Red
}
