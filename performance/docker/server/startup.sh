#!/usr/bin/env bash

/wait-for-it.sh -h PerformanceMysql -p 3306 -t 90
java -jar /opt/performance/PerformanceServer-swarm.jar
