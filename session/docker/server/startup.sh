#!/usr/bin/env bash

/wait-for-it.sh -h sessionmysql -p 3306 -t 90
java -jar /opt/session/SessionServer-swarm.jar -Dswarm.bind.address=0.0.0.0
