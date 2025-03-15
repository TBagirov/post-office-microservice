FROM openjdk:21
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
RUN cp build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
