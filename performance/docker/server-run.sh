#!/bin/bash

docker rm -f PerformanceServer 2> /dev/null

#docker run -d -v "$PWD/../PerformanceServer/target":/opt/performance --net dockerperformance --ip 172.18.0.6 --name "PerformanceServer" -p 8080:8080 --link PerformanceMysql performance/server
docker run -it -v "$PWD/../PerformanceServer/target":/opt/performance --net dockerperformance --ip 172.18.0.6 --name "PerformanceServer" -p 8080:8080 -p 4447:4447 --link PerformanceMysql performance/server
#docker run --rm -it -v "$PWD/../target":/opt/performance --net dockerperformance --ip 172.18.0.6 --name "PerformanceServer" --link PerformanceMysql performance/server bash
