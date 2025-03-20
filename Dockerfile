# Use uma imagem base do OpenJDK 21
FROM openjdk:21-jdk-slim

# Defina o diretório de trabalho
WORKDIR /app

# Adicione o script wait-for-it
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /wait-for-it.sh

# Atualize o repositório e instale as dependências necessárias
RUN apt-get update && \
    apt-get install -y --no-install-recommends fontconfig libfreetype6 libfreetype6-dev fonts-dejavu libx11-6 libxext6 libxrender1 && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Crie o diretório necessário e copie a imagem
RUN mkdir -p /home/diogenes/Imagens
COPY imagem/LOGO-VITORIA-DEMONSTRA-TEU-VALOR.png /home/diogenes/Imagens/LOGO-VITORIA-DEMONSTRA-TEU-VALOR.png

# Copie os arquivos do projeto para o contêiner
COPY target/*.jar /api.jar

# Exponha a porta 8080
EXPOSE 8080

# Comando para iniciar a aplicação
CMD ["java", "-jar", "/api.jar"]

