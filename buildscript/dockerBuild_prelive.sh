#!/bin/bash
cd ..
#./gradlew clean
./gradlew bootJar -Pprofile=prelive
docker rmi www.sooopo.com/sopo_prelive/parcel:0.0.48
docker build -f ./src/main/resources-prelive/Dockerfile . -t www.sooopo.com/sopo_prelive/parcel:0.0.48
docker push www.sooopo.com/sopo_prelive/parcel:0.0.48
exit 0
