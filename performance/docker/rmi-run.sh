#!/bin/bash

docker rm -f PerformanceRMI 2> /dev/null

docker run -it -v "$PWD/../PerformanceRMI/target":/opt/performance --net dockerperformance --ip 172.18.0.8 --name "PerformanceRMI" -p 8280:8280 --link PerformanceServer performance/rmi
