FROM openjdk:8-alpine
ARG JAR_FILE
VOLUME /agate/manager/logs
VOLUME /agate/manager/data
ADD target/${JAR_FILE} /agate/manager/manager-fat.jar
WORKDIR /agate/manager/
CMD ["java", "-jar", "manager-fat.jar"]