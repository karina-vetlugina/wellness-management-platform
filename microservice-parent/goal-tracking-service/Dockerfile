## Build
#FROM gradle:8-jdk21 AS builder
#
#WORKDIR /home/gradle/src
#
#COPY --chown=gradle:gradle . .
#
#RUN ./gradlew clean bootJar -x test
#
## Runtime
#FROM eclipse-temurin:21-jre
#
#WORKDIR /app
#
#COPY --from=builder /home/gradle/src/build/libs/*.jar /app/app.jar
#
#ENV SPRING_PROFILES_ACTIVE=docker
#
#EXPOSE 8087
#
#ENTRYPOINT ["java","-jar","/app/app.jar"]

# Build
FROM gradle:8-jdk21 AS builder

WORKDIR /home/gradle/src

# This COPY now copies the entire monorepo from the root context
COPY --chown=gradle:gradle . .

# *** CHANGE THIS LINE ***
# Run the build specifically for the 'goal-tracking-service' module.
# Running from /home/gradle/src (the root) ensures access to the :shared-schema dependency.
RUN ./gradlew :goal-tracking-service:clean :goal-tracking-service:bootJar -x test

# Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# *** CHANGE THIS LINE ***
# Update the copy path to retrieve the JAR from the module's build directory.
COPY --from=builder /home/gradle/src/goal-tracking-service/build/libs/*.jar /app/app.jar

ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8087

ENTRYPOINT ["java","-jar","/app/app.jar"]