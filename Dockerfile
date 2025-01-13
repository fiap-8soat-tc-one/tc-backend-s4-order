FROM maven:3.8.4-openjdk-17-slim AS build
WORKDIR /app
COPY pom.xml /app/pom.xml
RUN mvn dependency:go-offline
COPY src /app/src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim AS deploy

LABEL org.opencontainers.image.title="TC Order Backend API"
LABEL org.opencontainers.image.description="Order Backend API para o projeto TC da FIAP 8SOAT"
LABEL org.opencontainers.image.version="3.0.0"
LABEL org.opencontainers.image.url="https://github.com/fiap-8soat-tc-one/tc-backend-s4-order"
LABEL org.opencontainers.image.source="https://github.com/fiap-8soat-tc-one/tc-backend-s4-order"
LABEL org.opencontainers.image.authors="FIAP 8SOAT TEAM 32"
LABEL org.opencontainers.image.licenses="GNU General Public License v3.0"

WORKDIR /app
COPY --from=build /app/target/*.jar /app/tc-order-backend-api.jar
ENTRYPOINT ["java", "-jar", "tc-order-backend-api.jar"]
