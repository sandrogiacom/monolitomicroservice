#!/usr/bin/env bash

/wait-for-it.sh -h PerformanceServer -p 8080 -t 90
java -jar /opt/performance/PerformanceRMI-swarm.jar -Dswarm.http.port=8080