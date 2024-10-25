FROM openjdk:17-jdk-slim

WORKDIR /app
COPY . /app

#RUN ./gradlew clean build
#
#COPY 'producer/producer/build/libs/producer*.jar' '/app/producer.jar'

COPY './producer/producer/build/libs/producer*.jar' '/app/producer.jar'
CMD ["java", "-jar", "producer.jar"]