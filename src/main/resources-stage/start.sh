#!/bin/bash
NUMBER_OF_ATTEMPTS=1000

function start(){
  echo $! > fluentd.pid
  fluentd -c fluentd.conf -d fluentd.pid
  exec java -Duser.timezone=KST \
            -Dspring.profiles.active=stage \
            -Dotel.traces.exporter=otlp \
            -Dotel.metrics.exporter=none \
            -Dotel.exporter.otlp.endpoint=http://data-prepper:21890 \
            -Dotel.resource.attributes="service.name=parcel-service" \
            -Dotel.javaagent.debug=false \
            -javaagent:/usr/app/opentelemetry-javaagent.jar \
            -jar ./sopo-parcel-stage-0.0.50.jar \
            --spring.config.location=/usr/app/config/application.yml
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