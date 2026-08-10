# Build Stage
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -pl inventory-service -am -DskipTests

# Run Stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
ADD https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest/download/opentelemetry-javaagent.jar /opentelemetry-javaagent.jar
RUN chmod +x /opentelemetry-javaagent.jar
COPY --from=build /app/inventory-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-javaagent:/opentelemetry-javaagent.jar", "-jar", "app.jar"]
