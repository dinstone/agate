FROM openjdk:8-alpine
ARG JAR_FILE
VOLUME /agate/manager/logs
VOLUME /agate/manager/data
VOLUME /agate/manager/application.properties
ADD data/agate.db /agate/manager/data/agate.db
ADD target/${JAR_FILE} /agate/manager/manager-fat.jar
WORKDIR /agate/manager/
ENTRYPOINT java -jar manager-fat.jar