# usando a imagem OpenJDK 21
FROM openjdk:21-jdk-slim

# criando um diretório de trabalho
WORKDIR /app

# script wait-for-it -> https://github.com/vishnubob/wait-for-it/blob/master/wait-for-it.sh
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Atualizando o repositório e instale as dependências necessárias
RUN apt-get update && \
    apt-get install -y --no-install-recommends fontconfig libfreetype6 libfreetype6-dev fonts-dejavu libx11-6 libxext6 libxrender1 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Criando o diretório Imagens necessário para gerar e relatórios e copiando a imagem da raiz desse projeto.
RUN mkdir -p /home/diogenes/Imagens
COPY imagem/LOGO-VITORIA-DEMONSTRA-TEU-VALOR.png /home/diogenes/Imagens/LOGO-VITORIA-DEMONSTRA-TEU-VALOR.png

# copiando os arquivos jar para raiz do imagem
COPY target/*.jar /api.jar

# expondo na porta 8080
EXPOSE 8080

# Comando para iniciar a aplicação
CMD ["java", "-jar", "/api.jar"]

