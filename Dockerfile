# 1. OpenJDK 17 기반 이미지 사용
FROM openjdk:17-jdk-slim

# 2. JAR 파일을 컨테이너 내부에 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 3. 컨테이너 실행 시 JAR 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 4. Spring Boot 서버 포트 (예: 8080) 설정
EXPOSE 8080