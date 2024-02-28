FROM openjdk:8-alpine
ARG JAR_FILE
ENV APP_NAME=agate-admin
ENV ACTIVE_PROFILE="release"
VOLUME /agate-admin/logs
VOLUME /agate-admin/data
VOLUME /agate-admin/config
ADD target/${JAR_FILE} /agate-admin/admin-fat.jar
WORKDIR /agate-admin/
ENTRYPOINT java -cp /agate-admin/config -Dspring.profiles.active=$ACTIVE_PROFILE -jar admin-fat.jar