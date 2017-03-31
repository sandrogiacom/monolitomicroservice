#!/usr/bin/env bash

mkdir -p /opt/session
cp /opt/server/SessionServerCache.war /opt/jboss/wildfly/standalone/deployments/SessionServerCache.war

/wait-for-it.sh -h sessionmysql -p 3306 -t 90
/opt/jboss/wildfly/bin/standalone.sh -b 0.0.0.0 -bmanagement 0.0.0.0  -Djboss.node.name=servercache
