# 1. Etapa de Build (compila o projeto com Maven)
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app

# Copia arquivos do Maven Wrapper e dependências
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN ./mvnw dependency:go-offline -B

# Copia o código fonte e gera o jar (pulando os testes para o deploy ser rápido)
COPY src ./src
RUN ./mvnw clean package -DskipTests

# 2. Etapa de Execução (imagem final leve)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copia o .jar gerado na etapa anterior
COPY --from=build /app/target/*.jar app.jar

# O Render injeta a porta na variável $PORT automaticamente
ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]