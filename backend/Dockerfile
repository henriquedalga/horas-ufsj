# Use a imagem oficial do OpenJDK 19
FROM eclipse-temurin:19-jdk as build

# Defina o diretório de trabalho
WORKDIR /app

# Copie o arquivo pom.xml para o diretório de trabalho
COPY pom.xml .

# Copie o código-fonte
COPY src ./src

# Instale a biblioteca google-drive-integration localmente 
# (assumindo que ela está disponível no diretório 'libs')
COPY libs/google-drive-integration-1.0.0.jar /tmp/
RUN mvn install:install-file \
    -Dfile=/tmp/google-drive-integration-1.0.0.jar \
    -DgroupId=com.exemplo \
    -DartifactId=google-drive-integration \
    -Dversion=1.0.0 \
    -Dpackaging=jar

# Construa o aplicativo
RUN mvn clean package -DskipTests

# Segunda etapa: cria a imagem final apenas com o JRE e o JAR
FROM eclipse-temurin:19-jre

WORKDIR /app

# Copie o JAR compilado da etapa de construção
COPY --from=build /app/target/*.jar app.jar

# Exponha a porta 8080
EXPOSE 8080

# Defina as variáveis de ambiente para a conexão com o banco de dados
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/horas_ufsj
ENV SPRING_DATASOURCE_USERNAME=postgres
ENV SPRING_DATASOURCE_PASSWORD=postgres
ENV SPRING_JPA_HIBERNATE_DDL_AUTO=update

# Comando para iniciar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]