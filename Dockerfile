# 1. 빌드 스테이지
FROM gradle:7.6.4-jdk17 AS builder
WORKDIR /app

# Gradle 설정 파일 복사
COPY build.gradle settings.gradle gradlew /app/
COPY gradle /app/gradle

# 전체 소스 복사 후 빌드
COPY . /app
RUN ./gradlew :mokakbob-api:clean :mokakbob-api:bootJar --no-daemon


# 2. 실행 스테이지
FROM eclipse-temurin:17-jdk
WORKDIR /app

RUN apt-get update && apt-get install -y tzdata \
    && ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime \
    && echo "Asia/Seoul" > /etc/timezone \
    && rm -rf /var/lib/apt/lists/*

COPY --from=builder /app/mokakbob-api/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]

EXPOSE 8080
