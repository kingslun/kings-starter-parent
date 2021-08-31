FROM java:8
COPY target/app.jar /usr/src/myapp/app.jar
COPY replay /usr/src/myapp/replay
WORKDIR /usr/src/myapp
EXPOSE 8080
ENTRYPOINT exec java ${JVM_OPTS:- -Xdebug -Xms124m -Xmx124m} -jar /usr/src/myapp/app.jar