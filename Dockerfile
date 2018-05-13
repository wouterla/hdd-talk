FROM zenika/alpine-kotlin:latest

RUN mkdir -p /opt/app/log
RUN mkdir -p /var/log/app
COPY build/libs/webshop-shadow.jar /opt/app/
COPY logging.properties /opt/app/

VOLUME /var/log/

CMD java -Djava.util.logging.config.file=/opt/app/logging.properties -jar /opt/app/webshop-shadow.jar
