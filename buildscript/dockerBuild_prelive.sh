#!/bin/bash
cd ..
#./gradlew clean
./gradlew bootJar -Pprofile=prelive
docker rmi www.sooopo.com/sopo_prelive/parcel:0.0.29
docker build -f ./src/main/resources-prelive/Dockerfile . -t www.sooopo.com/sopo_prelive/parcel:0.0.29
docker push www.sooopo.com/sopo_prelive/parcel:0.0.29
exit 0
