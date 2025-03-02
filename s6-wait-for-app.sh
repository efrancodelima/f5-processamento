#!/bin/bash

# Endpoint URL
URL="http://localhost:8082/"

# Tempo limite em segundos
TIMEOUT=60

# Tempo de espera entre as tentativas em segundos
INTERVALO=5

# Função para verificar o status do endpoint
verificar_endpoint() {
  STATUS=$(curl -o /dev/null -s -w "%{http_code}\n" "$URL")
  if [ "$STATUS" -eq 200 ]; then
    echo "Aplicação ok!"
    exit 0
  else
    echo "Tentando novamente em $INTERVALO segundos..."
  fi
}

# Início do tempo limite
INICIO=$(date +%s)

# Loop para verificar o endpoint até o tempo limite ser atingido
while true; do
  verificar_endpoint
  AGORA=$(date +%s)
  TEMPO_EXECUTADO=$((AGORA - INICIO))
  if [ "$TEMPO_EXECUTADO" -ge "$TIMEOUT" ]; then
    echo "Tempo limite de $TIMEOUT segundos atingido."
    exit 1
  fi
  sleep $INTERVALO
done
