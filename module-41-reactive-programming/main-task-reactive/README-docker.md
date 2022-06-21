#Docker:

##Change application.yaml (or use spring profile via application-docker.yml)
Replace spring.r2dbc.url and spring.flyway.url in application.yaml to use postgres docker image/container

####<span style="color:red">BEFORE:</span>
**spring.r2dbc.url:**
```r2dbc:postgresql://localhost:5432/sport_db```<br>
**spring.flyway.url:**
```jdbc:postgresql://localhost:5432/sport_db```
####<span style="color:green">AFTER:</span>
**spring.r2dbc.url:**
```r2dbc:postgresql://local-postgres:5432/sport_db```<br>
**spring.flyway.url:**
```jdbc:postgresql://local-postgres:5432/sport_db```

##Build images

|Without docker-maven-plugin in pom.xml| With docker-maven-plugin in pom.xml |
|:---:|:---:|
|Build jar: ```mvn clean package```| Build jar and Docker image:```mvn clean package docker:build``` |
|Build Docker image: ```docker build -t <image-name> .```||

##Run Docker container from image
###1. Run
<span style="color:red">Replace ${DATASOURCE_USERNAME} and ${DATASOURCE_PASSWORD} environment variable with values</span><br>
```docker run --env SPRING_PROFILES_ACTIVE=docker --env DATASOURCE_USERNAME=${DATASOURCE_USERNAME} --env DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD} --env POSTGRES_HOST=local-postgres --name sport-container -p8080:8080 --link local-postgres:postgres sport-image```

###2. Run with memory settings (first rebuild image using another docker file)
**Build jar:** ```mvn clean package```<br>
**Build docker image:** ```docker build -f ExecDockerfile -t sport-memory-settings .```<br>
**Run docker container:**
<span style="color:red">(first replace ${DATASOURCE_USERNAME} and ${DATASOURCE_PASSWORD} environment variable with values)</span><br>
```docker run --env SPRING_PROFILES_ACTIVE=docker --env JAVA_OPTS='-Xmx3g -Xms3g' --env DATASOURCE_USERNAME=${DATASOURCE_USERNAME} --env DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD} --env POSTGRES_HOST=local-postgres --name sport-container-memory-settings -p8080:8080 --link local-postgres:postgres sport-memory-settings```

###3. Run with volume (external application.yml)
https://docs.docker.com/storage/bind-mounts/#start-a-container-with-a-bind-mount <br>
```docker run --env SPRING_PROFILES_ACTIVE=docker --env DATASOURCE_USERNAME=${DATASOURCE_USERNAME} --env DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD} --name sport-container -p8080:8080 --link local-postgres:postgres -v "$(pwd)"/volume-sport-app:/config sport-image```

##Info about Docker images and containers
####To see images:
```docker images```
####To see containers:
```docker ps -a```
####To see logs:
```docker logs <container-name>```
####To inspect image:
```docker inspect <container-name>```
####To stop container:
```docker stop <container-name>```
####To remove container:
```docker rm <container-name>```

