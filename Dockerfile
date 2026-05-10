# 1단계: build
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app
COPY . .

RUN chmod +x gradlew
RUN ./gradlew clean build -x test

# 2단계: run
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]