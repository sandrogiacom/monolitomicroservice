FROM java:8-jdk

MAINTAINER Vanei
LABEL description="Servidor Performance" version="8"

COPY startup.sh /startup.sh
COPY wait-for-it.sh /wait-for-it.sh
RUN chmod +x /startup.sh
RUN chmod +x /wait-for-it.sh

EXPOSE 8080

WORKDIR /opt/performance
ENTRYPOINT /startup.sh
