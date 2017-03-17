#!/bin/bash

docker rm -f PerformanceJMS 2> /dev/null

docker run -it -v "$PWD/../PerformanceJMS/target":/opt/performance --net dockerperformance --ip 172.18.0.10 -h "PerformanceJMS" --name "PerformanceJMS" -p 8080:8080 performance/jms
