# Build the Angular client
FROM node:18 AS buildang

WORKDIR /src

# Copy Angular source
COPY client/package*.json ./
COPY client/angular.json .
COPY client/tsconfig*.json ./
COPY client/src src

# Install dependencies and build
RUN npm ci
RUN npm install -g @angular/cli
RUN ng build 

FROM maven:3.9.9-eclipse-temurin-23 AS buildjava

WORKDIR /src

COPY server/mvnw .
COPY server/pom.xml .
COPY server/src src
COPY server/.mvn .mvn

## note application name
COPY --from=buildang /src/dist/client/* src/main/resources/static

# make mvnw executable
RUN chmod a+x mvnw
# produce target/server-0.0.1-SNAPSHOT.jar
RUN ./mvnw package -Dtest.skip=true 

# Deployment container
FROM eclipse-temurin:23-jre

WORKDIR /app

COPY --from=buildjava /src/target/server-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your application runs on

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--port=3000"]
