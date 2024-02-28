FROM openjdk:8-alpine
ARG JAR_FILE
ENV CONFIG_FILE="/agate-gateway/config/config.json"
VOLUME /agate-gateway/config
VOLUME /agate-gateway/logs
ADD target/${JAR_FILE} /agate-gateway/gateway-fat.jar
WORKDIR /agate-gateway/
ENTRYPOINT java -Dconfig.file=$CONFIG_FILE -jar gateway-fat.jar