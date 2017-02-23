FROM java:8-jdk

MAINTAINER Vanei
LABEL description="Performance Rest" version="8"

EXPOSE 8180

WORKDIR /opt/performance
CMD ["java", "-jar", "PerformanceRest-swarm.jar", "-Dswarm.http.port=8180"]
