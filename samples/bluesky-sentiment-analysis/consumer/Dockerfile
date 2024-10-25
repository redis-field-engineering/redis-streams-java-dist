FROM openjdk:17-jdk-slim

WORKDIR /app
COPY . /app

#RUN ./gradlew clean build
#COPY 'consumer/consumer/build/libs/consumer*.jar' '/app/consumer.jar'

COPY './consumer/consumer/build/libs/consumer*.jar' '/app/consumer.jar'
CMD ["java", "-Xmx2g", "-jar", "consumer.jar"]