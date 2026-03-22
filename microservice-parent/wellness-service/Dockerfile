#------
# Build Stage
#------

FROM gradle:8-jdk21 AS builder

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN ./gradlew build -x test

#------
# Package stage
#------

#FROM openjdk:21-jdk
FROM eclipse-temurin:21-jdk-jammy

RUN mkdir /app

COPY --from=builder /home/gradle/src/build/libs/*.jar /app/wellness-service.jar

ENV POSTGRES_USER=admin \
    POSTGRES_PWD=admin

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "/app/wellness-service.jar"]