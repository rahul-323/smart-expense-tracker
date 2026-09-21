# ─────────────── STAGE 1: BUILD ───────────────
# Use Maven + Java 21 to compile the app
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first and download dependencies
# (This layer is cached — deps only re-download if pom.xml changes)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ─────────────── STAGE 2: RUN ───────────────
# Use a lightweight JRE-only image (smaller, faster)
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy ONLY the built JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080
EXPOSE 8080

# JVM memory tuning
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Run the app
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
