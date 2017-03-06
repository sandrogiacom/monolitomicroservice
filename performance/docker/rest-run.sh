#!/bin/bash

docker rm -f PerformanceRest 2> /dev/null

docker run -it -v "$PWD/../PerformanceRest/target":/opt/performance --net dockerperformance --ip 172.18.0.7 -h "PerformanceRest" --name "PerformanceRest" -p 8180:8180 --link PerformanceServer performance/rest
