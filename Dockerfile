# Etapa de build: adiciona Maven manualmente à imagem base
FROM eclipse-temurin:19-jdk as build

# Instala Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean

# Diretório de trabalho
WORKDIR /app

# Copia os arquivos do projeto
COPY pom.xml .
COPY . .

# Compila o projeto
RUN mvn clean package -DskipTests -pl backend -am

# Etapa final: executa o JAR com uma imagem JRE leve
FROM eclipse-temurin:19-jre

WORKDIR /app

# Copia o JAR compilado da etapa anterior
COPY --from=build /app/backend/target/*.jar app.jar

# Expõe a porta da aplicação
EXPOSE 8080

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
