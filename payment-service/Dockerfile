FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /app
COPY pom.xml .

COPY ./market-app/pom.xml /app/market-app

COPY ./payment-service /app/payment-service

WORKDIR /app/payment-service
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/payment-service/target/*.jar app.jar
EXPOSE 8090

ENTRYPOINT ["java", "-jar", "app.jar"]