#!/bin/sh
# Bourne-Again shell script, ASCII text executable
# Encontra e executa o primeiro arquivo .jar no diretorio

JAR_FILE=$(find . -maxdepth 1 -type f -name "*.jar" | head -n 1)

if [ -z "$JAR_FILE" ]; then
    echo "Nenhum arquivo .jar encontrado."
else
    echo "Executando o arquivo: $JAR_FILE"
    java -jar "$JAR_FILE"
fi
