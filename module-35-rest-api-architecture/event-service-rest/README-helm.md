#Helm charts:

###Install helm
```brew install helm```

###Check version
```helm version```


###REPOSITORY
####Add repo
```helm repo add <repo-name> <repo-url>```
####Update repos
```helm repo update```
####Get all repos
```helm repo list```

###CHARTS
####Create folder with default files and repos
```helm create <charts-folder-name>```

####Install (init) helm (first build image)
```helm install <charts-folder-name> .```
```helm install <charts-folder-name> -n <namespace-name>```

####Preview new changes to helm
```helm template <charts-folder-name> .```

####Upgrade helm with new changes
```helm upgrade <charts-folder-name> .```

####View history of helm upgrades
```helm history <charts-folder-name>```

####Rollback helm to previous revision
```helm rollback <charts-folder-name>```
####Rollback helm to specific revision
```helm rollback <charts-folder-name> <revision-number>```

####Get all helms
```helm list```
```helm list -n <namespace-name>```

####Delete helm
```helm delete <charts-folder-name>```
```helm delete <charts-folder-name> -n <namespace-name>```
