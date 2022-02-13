#!/bin/bash
cd ..
#./gradlew clean
./gradlew bootJar -Pprofile=prelive
docker rmi www.sooopo.com/sopo_prelive/parcel:0.0.44
docker build -f ./src/main/resources-prelive/Dockerfile . -t www.sooopo.com/sopo_prelive/parcel:0.0.44
docker push www.sooopo.com/sopo_prelive/parcel:0.0.44
exit 0
