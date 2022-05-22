#!/bin/bash
cd ..
#./gradlew clean
./gradlew bootJar -Pprofile=prelive
docker rmi www.sooopo.com/sopo_prelive/parcel:0.0.51
docker build --platform linux/x86_64 -f ./src/main/resources-prelive/Dockerfile . -t www.sooopo.com/sopo_prelive/parcel:0.0.51
docker push www.sooopo.com/sopo_prelive/parcel:0.0.51
exit 0
