#Dockerize Recipes App:

##Change application.yaml
Replace spring.datasource.url in application.yaml to use postgres docker image/container

####Before:
```url: jdbc:postgresql://localhost:5432/mentoring_recipes_db```
####After:
```url: jdbc:postgresql://local-postgres:5432/mentoring_recipes_db```

##Build jar
```mvn clean package```

##Build Docker image
```docker build -t recipes-image .```
####To see images:
```docker images```

##Run Docker container from image
Replace ${DATASOURCE_USERNAME} and ${DATASOURCE_PASSWORD} environment variable with values

```docker run --env DATASOURCE_USERNAME=${DATASOURCE_USERNAME} --env DATASOURCE_PASSWORD=${DATASOURCE_PASSWORD} --name recipes-app -p8080:8080 --link local-postgres:postgres -d recipes-image```

####To see containers:
```docker ps -a```
####To see logs:
```docker logs recipes-app```
####To inspect image:
```docker inspect recipes-app```