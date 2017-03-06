#!/bin/bash

docker rm -f PerformanceMysql 2> /dev/null

#docker run --net dockerperformance --ip 172.18.0.5 -d --name "PerformanceMysql" -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=teste performance/mysql
docker run --net dockerperformance --ip 172.18.0.5 -h "PerformanceMysql" -it --rm --name "PerformanceMysql" -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=teste performance/mysql
#docker run -p 33306:3306 --net dockerperformance --ip 172.18.0.5 -it --rm --name "PerformanceMysql" -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=teste performance/mysql
