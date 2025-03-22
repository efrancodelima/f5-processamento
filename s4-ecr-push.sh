#!/bin/sh
# Bourne-Again shell script, ASCII text executable

# Pega a versão do projeto para uma variável de ambiente
APP="app-video"
JAVA_HOME="/opt/hostedtoolcache/Java_Temurin-Hotspot_jdk/17.0.13-11/x64"
FULL_PATH_IMAGE="${AWS_ACCOUNT_ID}.dkr.ecr.us-east-1.amazonaws.com/${APP}"
PROJECT_VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)

# Faz o upload da imagem docker para a AWS ECR com a tag da versão do projeto
docker tag $APP $FULL_PATH_IMAGE:$PROJECT_VERSION
docker push $FULL_PATH_IMAGE:$PROJECT_VERSION

# Faz o upload da imagem docker para a AWS ECR com a tag latest
docker tag $APP $FULL_PATH_IMAGE:latest
docker push $FULL_PATH_IMAGE:latest
