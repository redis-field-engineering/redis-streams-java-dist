FROM openjdk:17-jdk-slim

WORKDIR /app
COPY . /app


#RUN ./gradlew clean build
#COPY 'api/bsky-sentiment-api/build/libs/api*.jar' '/app/api.jar'
COPY './api/bsky-sentiment-api/build/libs/bsky-sentiment-api*.jar' '/app/bsky-sentiment-api.jar'
CMD ["java", "-Dspring.profiles.active=docker", "-jar", "bsky-sentiment-api.jar"]