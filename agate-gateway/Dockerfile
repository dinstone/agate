FROM openjdk:8-alpine
ARG JAR_FILE
VOLUME /agate/gateway/logs
ADD target/${JAR_FILE} /agate/gateway/gateway-fat.jar
WORKDIR /agate/gateway/
CMD ["java", "-jar", "gateway-fat.jar"]