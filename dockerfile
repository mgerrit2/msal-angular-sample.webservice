# Stage 1: Build the Java application (if using multi-stageS
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /application

# 1. Copy ALL build-essential configuration files
COPY gradlew .
COPY gradle gradle
COPY build.gradle* .
COPY settings.gradle* .
COPY *.properties .

RUN cat gradlew | tr -d '\r' > gradlew.linux && \
    mv gradlew.linux gradlew && \
    chmod +x gradlew

# 3. Copy source code
COPY src src

# 4. Compile fat JAR using Gradle caching
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew clean bootJar --no-daemon --no-configuration-cache

# Simply copy the built jar to app.jar
RUN cp build/libs/*.jar app.jar


# ==========================================
# Stage 2: Final Light Runtime Image (Ultra-Slim)
# ==========================================
FROM eclipse-temurin:25-jre-alpine
LABEL author="Gerrits Marc"

# Security: Non-root user
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /application

# Copy compiled JAR from Stage 1 (builder) into Stage 2
COPY --chown=spring:spring --from=builder /application/app.jar ./app.jar

# Java 25 High-Performance Flags:
# - TieredStopAtLevel=1: Fastest JIT startup for containers
# - Virtual Threads: Enabled for Java 25 performance
# - CDS: Uses the generated class data archive if present
ENV JAVA_OPTS="-XX:TieredStopAtLevel=1 \
               -Dspring.threads.virtual.enabled=true \
               -Dspring.main.lazy-initialization=true \
               -Dspring.data.jpa.repositories.bootstrap-mode=deferred"

# Expose Spring Boot app port (8080) and Remote Debugger (5005)
EXPOSE 8080 5005

USER spring:spring

# Launch Spring Boot using JarLauncher with exec for proper signal handling
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar app.jar"]