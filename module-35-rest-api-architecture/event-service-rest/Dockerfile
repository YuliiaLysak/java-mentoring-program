FROM openjdk:17
WORKDIR /
ADD target/event-service-rest-1.0-SNAPSHOT.jar //
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/event-service-rest-1.0-SNAPSHOT.jar"]