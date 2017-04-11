FROM jboss/wildfly:10.1.0.Final

MAINTAINER Vanei
LABEL description="Servidor Session JWT" version="8"

RUN /opt/jboss/wildfly/bin/add-user.sh admin totvs@123 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -g user,admin ejb test --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user,admin admin totvs@123 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user ejb test --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user user1 user1 --silent

RUN mkdir -p /opt/jboss/wildfly/modules/com/mysql/main
RUN mkdir -p /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/auth/main

COPY startup.sh /startup.sh
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
COPY mysql-module.xml /opt/jboss/wildfly/modules/com/mysql/main/module.xml
COPY mysql-connector-java-5.1.26.jar /opt/jboss/wildfly/modules/com/mysql/main/mysql-connector-java-5.1.26.jar
COPY auth-module.xml /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/auth/main/module.xml
COPY CommonLoginModule-1.0.0-SNAPSHOT.jar /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/auth/main/CommonLoginModule.jar
COPY jjwt-0.7.0.jar /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/auth/main/jjwt.jar

WORKDIR /opt/session
ENTRYPOINT /startup.sh
