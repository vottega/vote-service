FROM gradle:8.0-jdk17 AS builder
WORKDIR /home/gradle/project

# 소스 복사 및 빌드
COPY --chown=gradle:gradle . .
RUN gradle clean bootJar --no-daemon

# 2) 런타임 스테이지
FROM eclipse-temurin:17-jre-jammy
VOLUME /tmp
WORKDIR /app

# 빌더에서 만든 JAR만 복사
COPY --from=builder /home/gradle/project/build/libs/*.jar app.jar

# 컨테이너 내 포트
EXPOSE 8080

# JVM 옵션(필요시)
ENV JAVA_OPTS="-Xms256m -Xmx512m"

ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]