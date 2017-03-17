#!/usr/bin/env bash

/wait-for-it.sh -h performancemysql -p 3306 -t 90
java -jar /opt/performance/PerformanceServer-swarm.jar -Dswarm.bind.address=0.0.0.0
