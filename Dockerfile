# Stage 1: Build the Spring Boot application using Maven
FROM maven:3.8.5-openjdk-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Create the final image with CentOS and OpenJDK
FROM centos:7
LABEL maintainer="Your Name <your.email@example.com>"

# Install OpenJDK 17
RUN yum install -y java-21-openjdk-headless
RUN yum clean all

# Install OpenJDK 21
RUN dnf install java-21-openjdk

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose the port your Spring Boot application listens on (default is 8080)
EXPOSE 8080

# Define the command to run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
