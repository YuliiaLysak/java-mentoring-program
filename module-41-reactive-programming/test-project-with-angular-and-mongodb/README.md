##How to build Angular app (folder 'reservation-app'):

###Initial setup

1. Install npm using homebrew

```brew update```
```brew install node```  (will also install npm)

2. Check versions

```node -v```
```npm -v```

3. Install Angular CLI

```npm install -g @angular/cli```

4. Change to working directory

```cd ${HOME}/IdeaProjects/java-mentoring-program/module-41-reactive-programming/test-project-with-angular-and-mongodb```

5. Generate Angular project

```ng new reservation-app```

6. Change to app directory

```cd reservation-app```

7. Start the app

```ng serve```


###Generate Angular service class

```ng generate service reservation```

###Run tests

```ng test```