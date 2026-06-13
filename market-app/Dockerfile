FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY pom.xml .

COPY ./payment-service/pom.xml /app/payment-service

COPY ./market-app /app/market-app

WORKDIR /app/market-app
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/market-app/target/*.jar app.jar
EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]