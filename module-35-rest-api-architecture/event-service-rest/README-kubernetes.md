#Kubernetes for App:

###Check version
```kubectl version```

###Check contexts
```kubectl config get-contexts```


###DEPLOYMENT, SERVICE, NODE
####Check nodes
```kubectl get nodes```

####Create deployment/service (first build docker image)
```kubectl create -f events-deployment.yml```<br>
```kubectl create -f events-service.yml```<br><br>
or<br><br>
```kubectl apply -f events-deployment.yml```<br>
```kubectl apply -f events-service.yml```

or standalone manifest:<br>
```kubectl create -f events.yml```

####Get info about deployments/services/pods
```kubectl get deployment```<br>
```kubectl get svc```<br>
```kubectl get pods```<br>
```kubectl get svc,deployment,pods```

####Get info about pods logs
```kubectl logs <pod name>```

####Delete deployment/service
```kubectl delete deployment <deployment name>```

or using file name

```kubectl delete -f events-deployment.yml```<br>
```kubectl delete -f events-service.yml```<br>
```kubectl delete -f events.yml```


###NAMESPACE
####Create namespace
```kubectl create ns <namespace-name>```
####Delete namespace
```kubectl delete ns <namespace-name>```
####Get all namespaces
```kubectl get ns```


###CONFIG MAP
####Get all config maps
```kubectl get cm```
####Get info about specific config map
```kubectl describe cm <configmap-name>```

###SECRETS
####Get all secrets
```kubectl get secrets```
####Get info about specific secret
```kubectl describe secret <secret-name>```
