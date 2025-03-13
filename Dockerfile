FROM openjdk:17
RUN mkdir -p /usr/local/newrelic

ARG JAR_FILE=build/libs/*.jar
ARG PROFILE=dev

COPY ${JAR_FILE} /app.jar
COPY src/main/resources/application-${PROFILE}.yml application.yml

ADD newrelic/newrelic.jar /usr/local/newrelic/newrelic.jar
ADD newrelic/newrelic.yml /usr/local/newrelic/newrelic.yml

ENTRYPOINT ["java","-javaagent:/usr/local/newrelic/newrelic.jar", "-jar","app.jar"]
