## Define a imagem base
FROM openjdk:17-jdk-alpine

## Define o diretório de trabalho da imagem
WORKDIR /app

## Copia a aplicação para o diretório de trabalho da imagem
COPY target/app-producao-*.jar .

## Script para inicializar a aplicação
COPY s1-run-project.sh .
RUN chmod +x s1-run-project.sh
CMD ["sh", "-c", "./s1-run-project.sh"]
