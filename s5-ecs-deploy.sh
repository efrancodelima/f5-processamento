#!/bin/bash

# Inicia o script
echo "Script iniciado."

CLUSTER_NAME="cluster-tc5"
SERVICE_NAME="video-service"
TASK_DEF_NAME="video-task-def"

# Clona a task definition mais recente, removendo os campos desnecessários
NEW_TASK_DEFINITION=$(aws ecs describe-task-definition \
  --task-definition ${TASK_DEF_NAME} --output json | \
  jq '.taskDefinition | del(.taskDefinitionArn, .revision, .status, .requiresAttributes, .compatibilities, .registeredAt, .registeredBy)')

# Registra a nova task definition
REGISTERED_TASK=$(aws ecs register-task-definition --cli-input-json \
  "${NEW_TASK_DEFINITION}" --output json)

echo "Registrou a nova task definition."

# Pega o número da revisão da nova task definition
NR_REV_NOVA=$(aws ecs describe-task-definition --task-definition ${TASK_DEF_NAME} \
  --output json | jq -r '.taskDefinition.revision')

# Atualiza o service
UPDATE=$(aws ecs update-service --cluster ${CLUSTER_NAME} --service ${SERVICE_NAME} \
  --task-definition ${TASK_DEF_NAME}:${NR_REV_NOVA})

echo "Atualizou o service."

# Encerra o script
echo "Script finalizado."