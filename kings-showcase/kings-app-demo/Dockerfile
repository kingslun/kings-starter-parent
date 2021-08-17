FROM java:8
COPY target/app.jar /root/startup/app.jar
WORKDIR /root/startup/
EXPOSE 9090
ENTRYPOINT exec java ${JVM_OPTS:- -Xdebug -Xms124m -Xmx124m} -jar /root/startup/app.jar