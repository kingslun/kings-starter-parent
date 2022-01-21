FROM openjdk:latest
COPY target/app.jar /root/startup/app.jar
WORKDIR /root/startup/
EXPOSE 8080
ENTRYPOINT exec java ${JVM_OPTS:- -Xdebug -Xms124m -Xmx124m} -jar /root/startup/app.jar