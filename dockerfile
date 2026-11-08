# ==========================================
# Stage 1: Build the Java application
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

RUN mkdir -p extracted && \
    cd extracted && \
    jar -xf ../build/libs/*.jar


# Generate a minimal custom JRE using jlink
RUN jlink \
    --add-modules java.base,java.logging,java.naming,java.sql,java.xml,java.management,java.instrument,jdk.unsupported,java.desktop,java.security.jgss \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /opt/custom-jre


# ==========================================
# Stage 2: Final Runtime Image
# ==========================================
FROM debian:bookworm-slim
LABEL author="Gerrits Marc"

RUN groupadd -r spring && useradd -r -g spring spring

# Copy custom minimal JRE
COPY --from=builder /opt/custom-jre /opt/custom-jre

ENV JAVA_HOME=/opt/custom-jre
ENV PATH="$JAVA_HOME/bin:$PATH"

WORKDIR /application

# Copy extracted BOOT-INF structure directly into WORKDIR
COPY --chown=spring:spring --from=builder /extracted-app/BOOT-INF ./BOOT-INF
COPY --chown=spring:spring --from=builder /extracted-app/META-INF ./META-INF
COPY --chown=spring:spring --from=builder /extracted-app/org ./org

ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-XX:TieredStopAtLevel=1 \
               -Dspring.threads.virtual.enabled=true \
               -Dspring.main.lazy-initialization=true \
               -Dspring.data.jpa.repositories.bootstrap-mode=deferred"

EXPOSE 8080 5005
USER spring:spring

# Run via classpath pointing directly to the Application class
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -cp BOOT-INF/classes:BOOT-INF/lib/* com.msal.angular.msal_angular_sample.webservice.Application"]