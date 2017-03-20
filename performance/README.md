# Testes de performance
Convertendo uma aplicação monolítica JEE para Microserviços

## Pre-requisitos:
- Java 8
- Docker 17.03 ou superior
- Docker compose 1.11.2 ou superior

### Dois testes est&atilde;o disponive&iacute;s:
- Um usu&aacute;rio, um &uacute;nico servidor de cada servi&ccedil;o
    - Para executar, inicie o docker-compose usando o arquivo `docker/docker-compose.yml`
    - Rode o script `TimeMeasurementMono.jmx` com o JMeter 3.1
- 10 usu&aacute;rios, dois servidores para cada servi&ccedil;o
    - Para executar, inicie o docker-compose usando o arquivo `docker/docker-compose-balanced.yml`
    - Rode o script `TimeMeasurementBalanced.jmx` com o JMeter 3.1
