# 1. Build Stage
FROM gradle:8.14.2-jdk17 AS build
WORKDIR /app

# [Optimization] 캐싱을 위해 환경 및 설정 파일들을 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 모든 모듈의 build.gradle 복사 (의존성 레이어 캐싱)
COPY mokakbob-api/build.gradle mokakbob-api/
COPY mokakbob-batch/build.gradle mokakbob-batch/
COPY mokakbob-consumer/build.gradle mokakbob-consumer/
COPY mokakbob-core/build.gradle mokakbob-core/
COPY mokakbob-redis/build.gradle mokakbob-redis/

# [Optimization] 의존성만 먼저 다운로드하여 레이어 캐시 생성
RUN ./gradlew dependencies --no-daemon || true

# 전체 소스 코드 복사 (이후의 수정은 컴파일 단계만 수행됨)
COPY . .

# 빌드 시 아규먼트로 모듈명을 받음
ARG MODULE_NAME=mokakbob-api
RUN ./gradlew :${MODULE_NAME}:bootJar --no-daemon

# 2. Run Stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# 타임존 설정 및 필수 패키지 설치
RUN apt-get update && apt-get install -y tzdata && \
    ln -sf /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

# [Optimization] 실행 가능 Jar만 복사 (plain 제외)
ARG MODULE_NAME=mokakbob-api
COPY --from=build /app/${MODULE_NAME}/build/libs/*-SNAPSHOT.jar app.jar

# JVM 옵션은 docker-compose에서 주입받도록 설정 (ENV JAVA_OPTS 제거)
# 기본값은 빈 값으로 설정
ENV JAVA_OPTS=""

# 실행
# 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
