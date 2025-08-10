FROM openjdk:11-jdk-slim
WORKDIR /app
COPY server/build/libs/stream_craft_server.jar app.jar
ENV PORT=8080
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
