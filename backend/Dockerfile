# Etape 1: Build Maven
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests || mvn clean package -DskipTests

# Etape 2: Execution
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Hugging Face Spaces écoute sur le port 7860
EXPOSE 7860
CMD ["java", "-Dserver.port=7860", "-jar", "app.jar"]