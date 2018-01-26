FROM anapsix/alpine-java:8
MAINTAINER Jan Galinski <jan.galinski@holisticon.de>

ARG JAR_FILE
ADD target/${JAR_FILE} /app.jar

RUN bash -c 'touch /app.jar'

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
