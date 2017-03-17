FROM haproxy:1.7

MAINTAINER Vanei
LABEL description="Servidor HA Proxy" version="1.7"

EXPOSE 8080 61616

COPY haproxy.cfg /usr/local/etc/haproxy/haproxy.cfg
RUN mkdir -p /var/lib/haproxy
