# Multi-stage Dockerfile for StockMaster Spring Boot Application
# Stage 1: Build JAR using Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy POM and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build package
COPY src ./src
RUN mvn package -DskipTests=true -B

# Stage 2: Minimal JRE Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Ensure latest system CA certificates are installed for public TLS verification
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates && rm -rf /var/lib/apt/lists/*

# Non-root security user
RUN groupadd -r stockmaster && useradd -r -g stockmaster stockmaster

# Copy compiled fat JAR from build stage
COPY --from=build /app/target/stockmaster-inventory-system-1.0.0.jar app.jar

# Set ownership
RUN chown -R stockmaster:stockmaster /app
USER stockmaster

# Dynamic port assignment for cloud environments (Render, Railway, Heroku)
EXPOSE 8080
ENV PORT=8080

ENTRYPOINT ["sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -jar app.jar --server.port=${PORT:-8080}"]
