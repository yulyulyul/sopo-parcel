FROM www.sooopo.com/base_image/debian_java11_fluentd:1.0.5

USER root

ARG JAR_FILE=sopo-parcel-prelive-0.0.58.jar
ENV JAR_FILE_PATH=/usr/app/$JAR_FILE

COPY ./src/main/resources-prelive/start.sh /usr/app
COPY ./src/main/resources-prelive/fluentd.conf /usr/app
COPY ./src/main/resources/application.yml /usr/app/config
COPY build/libs/${JAR_FILE} /usr/app
ADD ./opentelemetry/opentelemetry-javaagent.jar /usr/app

RUN fluent-gem install fluent-plugin-kafka

WORKDIR /usr/app

EXPOSE 40150

RUN chmod 744 ./start.sh
ENV PROFILE=prelive

ENTRYPOINT ["/bin/bash","./start.sh"]