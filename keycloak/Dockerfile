# Stage 1: Build the JAR Provider file
FROM maven:3.8.4-openjdk-17-slim AS builder
# Set the working directory
WORKDIR /app
# Copy the pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline
# Copy the source code
COPY src ./src
# Package the application into a JAR file
RUN mvn package

# Stage 2: Create the final image
FROM bitnami/keycloak:25.0.6

# Copy the custom Hive Theme to the Keycloak themes directory
RUN mkdir -p /opt/bitnami/keycloak/themes
COPY ./themes /opt/bitnami/keycloak/themes

# Copy the JAR files from the build stage
COPY --from=builder /app/target/*.jar /opt/bitnami/keycloak/providers/
