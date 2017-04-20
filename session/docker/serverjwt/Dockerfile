FROM jboss/wildfly:10.1.0.Final

MAINTAINER Vanei
LABEL description="Servidor Session JWT" version="8"

RUN /opt/jboss/wildfly/bin/add-user.sh admin totvs@123 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -g user,admin ejb test --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user,admin admin totvs@123 --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user ejb test --silent
RUN /opt/jboss/wildfly/bin/add-user.sh -a -g user user1 user1 --silent

RUN mkdir -p /opt/jboss/wildfly/modules/com/mysql/main
RUN mkdir -p /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/security/common/main
RUN mkdir -p /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/security/loginmodule/main
RUN mkdir -p /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/security/mechanism/main
RUN mkdir -p /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/extension/custom/main


COPY startup.sh /startup.sh
COPY standalone.xml /opt/jboss/wildfly/standalone/configuration/standalone.xml
COPY modules/com/mysql/main/* /opt/jboss/wildfly/modules/com/mysql/main/
COPY modules/com/teste/monolitomicroservice/security/common/main/* /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/security/common/main/
COPY modules/com/teste/monolitomicroservice/security/loginmodule/main/* /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/security/loginmodule/main/
COPY modules/com/teste/monolitomicroservice/security/mechanism/main/* /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/security/mechanism/main/
COPY modules/com/teste/monolitomicroservice/extension/custom/main/* /opt/jboss/wildfly/modules/com/teste/monolitomicroservice/extension/custom/main/

WORKDIR /opt/session
ENTRYPOINT /startup.sh
