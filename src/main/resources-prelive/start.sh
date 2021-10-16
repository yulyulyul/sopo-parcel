#!/bin/bash
NUMBER_OF_ATTEMPTS=1000

function start(){
  echo $! > fluentd.pid
  fluentd -c fluentd.conf -d fluentd.pid
  exec java -Duser.timezone=KST \
            -Dspring.profiles.active=prelive \
            -Dotel.traces.exporter=otlp \
            -Dotel.metrics.exporter=otlp \
            -Dotel.exporter.otlp.metrics.endpoint=http://yulnas.synology.me:21890 \
            -Dotel.exporter.otlp.endpoint=http://yulnas.synology.me:21890 \
            -Dotel.resource.attributes="service.name=parcel-service" \
            -Dotel.javaagent.debug=false \
            -javaagent:/usr/app/opentelemetry-javaagent-all.jar \
            -jar ./sopo-parcel-prelive-0.0.4.jar \
            --spring.config.location=/usr/app/config/bootstrap.yml
}

echo "Wait.. Until config server is up"
for((i=1;i<=$NUMBER_OF_ATTEMPTS;i++))
do
   echo "trying >> [$i/$NUMBER_OF_ATTEMPTS]"

    config_status=$(curl -s -X GET http://config:8888/actuator/health | jq -r '.status')
   if [[ "UP" == "$config_status" ]]; then
     echo "config server is up!! start, parcel-service"
     start
     break
   else
     sleep 1
   fi
done