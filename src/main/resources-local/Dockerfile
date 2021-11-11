FROM www.sooopo.com/base_image/debian_java11_fluentd:1.0.3

USER root

ARG JAR_FILE=sopo-parcel-stage-0.0.20.jar
ENV JAR_FILE_PATH=/usr/app/$JAR_FILE

COPY ./sopo-firebase-adminsdk.json /usr/app
COPY ./src/main/resources-stage/start.sh /usr/app
COPY ./src/main/resources/fluentd.conf /usr/app
COPY ./src/main/resources/bootstrap.yml /usr/app/config
COPY build/libs/${JAR_FILE} /usr/app

RUN fluent-gem install fluent-plugin-kafka

WORKDIR /usr/app

EXPOSE 40150

RUN chmod 744 ./start.sh
ENV PROFILE=stage

ENTRYPOINT ["/bin/bash","./start.sh"]