# Build Stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -pl payment-provider -am -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
RUN chmod +x /opentelemetry-javaagent.jar
COPY --from=build /app/payment-provider/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-jar", "app.jar"]
