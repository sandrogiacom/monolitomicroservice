FROM java:8-jdk

MAINTAINER Vanei
LABEL description="Performance RMI" version="8"

EXPOSE 8280

WORKDIR /opt/performance
CMD ["java", "-jar", "PerformanceRMI-swarm.jar", "-Dswarm.http.port=8280"]
