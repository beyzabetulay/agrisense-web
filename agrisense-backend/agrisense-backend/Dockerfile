# Multi-stage build: Maven build + JVM runtime
FROM docker.io/library/maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /build
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Download dependencies first (cached layer)
RUN mvn dependency:go-offline -B 2>/dev/null || true

# Copy source and build
COPY src src
RUN mvn package -DskipTests -B

# --- Runtime stage ---
FROM docker.io/library/eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy the Quarkus fast-jar output
COPY --from=builder /build/target/quarkus-app/lib/ /app/lib/
COPY --from=builder /build/target/quarkus-app/*.jar /app/
COPY --from=builder /build/target/quarkus-app/app/ /app/app/
COPY --from=builder /build/target/quarkus-app/quarkus/ /app/quarkus/

EXPOSE 8081

ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Dquarkus.http.port=8081"

CMD ["java", "-jar", "/app/quarkus-run.jar"]
