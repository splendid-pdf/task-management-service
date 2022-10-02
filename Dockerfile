FROM openjdk:17-alpine
WORKDIR /app
EXPOSE 8080
COPY /build/libs/*-SNAPSHOT.jar app.jar
ENTRYPOINT ["sh", "-c", "java -jar /app/app.jar"]