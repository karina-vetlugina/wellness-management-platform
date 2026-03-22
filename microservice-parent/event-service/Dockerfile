## -----
## Build Stage
## -----
#
#FROM gradle:8-jdk21 AS builder
#
#COPY --chown=gradle:gradle . /home/gradle/src
#
#WORKDIR /home/gradle/src
#
#RUN ./gradlew build -x test
#
## -----
## Package Stage
## -----
#
##FROM openjdk:21-jdk
#FROM eclipse-temurin:21-jdk-jammy
#RUN mkdir /app
#
#COPY --from=builder /home/gradle/src/build/libs/*.jar /app/event-service.jar
#
#EXPOSE 8082
#
#ENTRYPOINT ["java", "-jar", "/app/event-service.jar"]

# -----
# Build Stage
# -----

FROM gradle:8-jdk21 AS builder

# Set the working directory to where the root project will be copied
WORKDIR /home/gradle/src

# This COPY assumes the context is the project root (i.e., you will change docker-compose.yml)
COPY --chown=gradle:gradle . .

# *** UPDATED: Run the build command specifically for the event-service module ***
# Running from the root directory (/home/gradle/src) ensures access to the :shared-schema dependency.
RUN ./gradlew :event-service:build -x test

# -----
# Package Stage
# -----

FROM eclipse-temurin:21-jdk-jammy
RUN mkdir /app

# *** UPDATED: Get the JAR from the module's build folder ***
COPY --from=builder /home/gradle/src/event-service/build/libs/*.jar /app/event-service.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/event-service.jar"]