# 1. Build Stage
FROM gradle:8.14.2-jdk17 AS build
WORKDIR /app

# 캐싱을 위해 설정 파일들을 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 컨슈머, 배치, API 등 모든 모듈의 build.gradle 복사 (캐시 효율화)
COPY mokakbob-api/build.gradle mokakbob-api/
COPY mokakbob-batch/build.gradle mokakbob-batch/
COPY mokakbob-consumer/build.gradle mokakbob-consumer/
COPY mokakbob-core/build.gradle mokakbob-core/
COPY mokakbob-redis/build.gradle mokakbob-redis/

# 의존성 먼저 다운로드 (캐시)
RUN ./gradlew dependencies --no-daemon || true

# 전체 소스 코드 복사
COPY . .

# 빌드 시 아규먼트로 모듈명을 받음 (기본값은 mokakbob-api)
ARG MODULE_NAME=mokakbob-api
RUN ./gradlew :${MODULE_NAME}:bootJar --no-daemon

# 2. Run Stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# 타임존 설정 (Ubuntu/Debian 기반)
RUN apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

# 빌드된 jar 파일 복사
ARG MODULE_NAME=mokakbob-api
COPY --from=build /app/${MODULE_NAME}/build/libs/*.jar app.jar

# JVM 옵션 환경변수
ENV JAVA_OPTS="-Xms512m -Xmx1024m"

# 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dspring.profiles.active=prod -jar app.jar"]
