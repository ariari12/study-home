FROM openjdk:17-jdk-alpine AS build
WORKDIR /app
COPY . .
# -x test 를 추가하는 이유는 테스트단계에서는 환경변수가 전달되지않아서 jdbc 에러가 뜬다
RUN ./gradlew build -x test

# Runtime stage
FROM openjdk:17-jdk-alpine AS runtime
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/server.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/server.jar"]