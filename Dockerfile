# --- Build stage ---
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew clean build -x test --no-daemon

# --- Runtime stage ---
FROM eclipse-temurin:21-jdk-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

WORKDIR /app
COPY --from=build /app/build/libs/streamix-users-account-*.jar app.jar
RUN chown appuser:appgroup app.jar

USER appuser
EXPOSE 8110
ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]