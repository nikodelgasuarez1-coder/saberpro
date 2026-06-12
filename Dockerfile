# ===== Etapa 1: compilar el .jar =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q -DskipTests clean package

# ===== Etapa 2: imagen final ligera =====
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
# Render inyecta el puerto en la variable PORT
EXPOSE 8089
ENTRYPOINT ["java", "-jar", "app.jar"]
