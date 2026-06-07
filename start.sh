#!/bin/bash

echo "Iniciando workspace ASPManager..."

# Dicionário com os repositórios (Formato: Pasta|URL_Git)
REPOS=(
  "aspmanager-service-discovery|git@github.com:ucsal/aspmanager-service-discovery.git"
  "aspmanager-auth-service|git@github.com:ucsal/aspmanager-auth-service.git"
  "aspmanager-espaco-service|git@github.com:ucsal/aspmanager-espaco-service.git"
  "aspmanager-usuario-service|git@github.com:ucsal/aspmanager-usuario-service.git"
  "aspmanager-escola-service|git@github.com:ucsal/aspmanager-escola-service.git"
  "aspmanager-software-service|git@github.com:ucsal/aspmanager-software-service.git"
)

# Loop para clonar os repositórios faltantes
echo "Verificando repositórios..."
for repo in "${REPOS[@]}"; do
  DIR="${repo%%|*}"
  URL="${repo##*|}"

  if [ ! -d "../$DIR" ]; then
    echo "Clonando $DIR..."
    git clone "$URL" "../$DIR"
  else
    echo "$DIR já existe."
  fi
done

# Tratamento para a pasta do Postgres
if [ ! -d "../postgres" ]; then
  echo "Criando estrutura de pastas do Postgres..."
  mkdir -p ../postgres
  cp src/main/resources/init.sql ../postgres/init.sql
fi

echo "Subindo os contêineres do Docker..."

# Use --build apenas quando explicitamente solicitado: ./start.sh --build
BUILD_FLAG=""
if [[ "$1" == "--build" ]]; then
  BUILD_FLAG="--build"
  echo "Modo rebuild ativado."
fi

if docker compose up -d $BUILD_FLAG; then
    echo "Projetos rodando! Digite 'docker compose logs -f' para acompanhar os logs."
else
    echo "Ocorreu um erro ao subir os contêineres. O processo foi interrompido."
    exit 1
fi
