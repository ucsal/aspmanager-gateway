#!/bin/bash

echo "Encerrando a stack de microserviços ASPManager..."

# O comando down para os contêineres e remove a rede virtual criada
docker compose down

echo "Ambiente encerrado."
