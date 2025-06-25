FROM docker.io/maven:latest as builder
ADD pom.xml .
RUN mvn dependency:go-offline
COPY . .
RUN mvn clean package

FROM docker.io/eclipse-temurin:17
VOLUME /tmp
RUN mkdir -p /app/
RUN mkdir -p /app/logs/
COPY --from=builder /target/soccernow-1.0.jar app/app.jar
EXPOSE 8080 8084
ENTRYPOINT ["java","-jar", "/app/app.jar"]