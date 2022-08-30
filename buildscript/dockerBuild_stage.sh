#!/bin/bash
cd ..
#./gradlew clean
./gradlew bootJar -Pprofile=stage
docker rmi www.sooopo.com/sopo_stage/parcel:0.0.53
docker build --platform linux/x86_64 -f ./src/main/resources-stage/Dockerfile . -t www.sooopo.com/sopo_stage/parcel:0.0.53

#stack_sopo=$(docker stack ls --format "{{.Name}}")
#
#if [ "$stack_sopo" == "sopo" ]; then
#    cd /parcels/jieyullee/SynologyDrive/sopo/clustering
#    ./finish.sh
#    ./start.sh
#fi

exit 0
