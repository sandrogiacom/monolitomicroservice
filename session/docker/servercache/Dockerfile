FROM jboss/wildfly:10.1.0.Final

MAINTAINER Vanei
LABEL description="Servidor Session" version="8"

RUN /opt/jboss/wildfly/bin/add-user.sh admin totvs@123 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user,admin admin totvs@123 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user user user --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user user01 user01 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user user02 user02 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user user03 user03 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user user04 user04 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user user05 user05 --silent

RUN mkdir -p /opt/jboss/wildfly/modules/com/mysql/main

COPY startup.sh /startup.sh
COPY wait-for-it.sh /wait-for-it.sh
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
COPY mysql-module.xml /opt/jboss/wildfly/modules/com/mysql/main/module.xml
COPY mysql-connector-java-5.1.26.jar /opt/jboss/wildfly/modules/com/mysql/main/mysql-connector-java-5.1.26.jar
#RUN chmod +x /startup.sh
#RUN chmod +x /wait-for-it.sh

WORKDIR /opt/session
ENTRYPOINT /startup.sh
