# ==========================================
# Stage 1: Build the Java application & extract layers
# ==========================================
FROM eclipse-temurin:25-jdk AS builder
WORKDIR /application

# Copy build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle* .
COPY settings.gradle* .
COPY *.properties .

RUN cat gradlew | tr -d '\r' > gradlew.linux && \
    mv gradlew.linux gradlew && \
    chmod +x gradlew

# Copy source code
COPY src src

# Compile fat JAR
RUN --mount=type=cache,target=/root/.gradle \
    ./gradlew clean bootJar --no-daemon --no-configuration-cache

# Extract layers using layertools and ensure all possible layer folders exist
RUN java -Djarmode=layertools -jar build/libs/*.jar extract --destination extracted && \
    mkdir -p extracted/snapshot-dependencies extracted/spring-boot-loader extracted/dependencies extracted/application

# Generate a minimal custom JRE using jlink (including java.security.jgss for Tomcat security)
RUN jlink \
    --add-modules java.base,java.logging,java.naming,java.sql,java.xml,java.management,java.instrument,jdk.unsupported,java.desktop,java.security.jgss \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /opt/custom-jre


# ==========================================
# Stage 2: Final Ultra-Slim Runtime Image (Debian Slim)
# ==========================================
FROM debian:bookworm-slim
LABEL author="Gerrits Marc"

# Security: Non-root user & minimal runtime environment
RUN groupadd -r spring && useradd -r -g spring spring

# Copy custom minimal JRE from builder stage
COPY --from=builder /opt/custom-jre /opt/custom-jre

ENV JAVA_HOME=/opt/custom-jre
ENV PATH="$JAVA_HOME/bin:$PATH"

WORKDIR /application

# Copy extracted Spring Boot layers safely
COPY --chown=spring:spring --from=builder /application/extracted/dependencies/ ./
COPY --chown=spring:spring --from=builder /application/extracted/spring-boot-loader/ ./
COPY --chown=spring:spring --from=builder /application/extracted/snapshot-dependencies/ ./
COPY --chown=spring:spring --from=builder /application/extracted/application/ ./

ENV SPRING_PROFILES_ACTIVE=render
ENV JAVA_OPTS="-XX:TieredStopAtLevel=1 \
               -Dspring.threads.virtual.enabled=true \
               -Dspring.main.lazy-initialization=true \
               -Dspring.data.jpa.repositories.bootstrap-mode=deferred"

EXPOSE 8080 5005
USER spring:spring

# Run Java application
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]