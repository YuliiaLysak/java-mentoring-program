# Base java:17
FROM openjdk:17

# create and use user
RUN useradd -ms /bin/bash newuser
USER newuser
WORKDIR /home/newuser

# Add jar to container
ADD target/main-task-reactive-1.0-SNAPSHOT.jar //

EXPOSE 8080

# Entry in json format
ENTRYPOINT ["java", "-jar", "/main-task-reactive-1.0-SNAPSHOT.jar"]
