###How to run all application in one container in Docker using docker-compose.yaml

1) Build every app using command:
```mvn clean package docker:build```

    **NB:** In order to use docker:build pom.xml should contain 'docker-maven-plugin'


2) Open terminal/cmd in the folder with docker-compose.yaml:
```cd ../module-24-microservices/main-task-v2/docker-compose.yaml```



3) Run command:
```docker-compose up``` or ```docker-compose up --build```