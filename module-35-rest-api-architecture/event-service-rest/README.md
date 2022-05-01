#Dockerize Event Service:


##Build jar
```mvn clean package```

##Build Docker image
```docker build -t event-service .```
###To see images:
```docker images```

##Run Docker container from image
```docker run --name event-service-app -p8080:8080 -d event-service```

###To see containers:
```docker ps -a```
###To see logs:
```docker logs event-service-app```
###To inspect image:
```docker inspect event-service-app```