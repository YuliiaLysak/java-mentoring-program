#Dockerize Recipes App:

##Change application.yaml
Replace spring.datasource.url in application.yaml to use postgres docker image/container

####Before:
```url: jdbc:postgresql://localhost:5432/mentoring_recipes_db```
####After:
```url: jdbc:postgresql://local-postgres:5432/mentoring_recipes_db```

##Build images

|Without docker-maven-plugin in pom.xml| With docker-maven-plugin in pom.xml |
|:---:|:---:|
|Build jar: ```mvn clean package```| Build jar and Docker image:```mvn clean package docker:build``` |
|Build Docker image: ```docker build -t recipes .```||

####To see images:
```docker images```

##Run Docker container from image
<span style="color:red">Replace ${DATASOURCE_USERNAME} and ${DATASOURCE_PASSWORD} environment variable with values</span>.

```docker run --env DATASOURCE_USERNAME=${DATASOURCE_USERNAME} --env DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD} --name recipes-app -p8080:8080 --link local-postgres:postgres -d recipes```

####To see containers:
```docker ps -a```
####To see logs:
```docker logs recipes-app```
####To inspect image:
```docker inspect recipes-app```