FROM openjdk:17-jdk-alpine AS build
WORKDIR /app
COPY . .
# Docker 컨텍스트가 gradlew 파일이 실행 가능한 권한을 가지도록 Dockerfile에서 명시적으로 설정.
RUN chmod +x ./gradlew
# -x test 를 추가하는 이유는 테스트단계에서는 환경변수가 전달되지않아서 jdbc 에러가 뜬다
RUN ./gradlew build -x test

# Runtime stage
FROM openjdk:17-jdk-alpine AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/server.jar"]