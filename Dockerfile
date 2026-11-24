# ---------- Etapa 1: build com Maven + JDK 21 ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Baixa as dependencias primeiro (camada reaproveitada enquanto o pom.xml nao mudar)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- Etapa 2: imagem final, so com o JRE e o jar ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# Roda com um usuario sem privilegios
RUN useradd --system --uid 1001 thindesk
USER thindesk

COPY --from=build /app/target/thindesk-1.0.0.jar app.jar

# Ajustes de memoria para caber no plano gratuito (512 MB)
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=65 -XX:+UseSerialGC -Xss512k -XX:MaxMetaspaceSize=160m -XX:ReservedCodeCacheSize=64m"

# O Render informa a porta pela variavel PORT (lida em application.properties)
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
