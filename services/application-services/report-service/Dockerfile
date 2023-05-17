FROM 486225280130.dkr.ecr.us-west-1.amazonaws.com/maven:3.6.3-amazoncorretto-11 as BUILD

COPY . /usr/src/report-service

COPY settings.xml /root/.m2/settings.xml

ARG token
ENV CODEARTIFACT_AUTH_TOKEN $token

RUN mvn -Dmaven.repo.local=/root/.m2 --batch-mode -f /usr/src/report-service/pom.xml clean package

FROM 486225280130.dkr.ecr.us-west-1.amazonaws.com/openjdk:11-jdk-slim

EXPOSE 80

COPY --from=BUILD /usr/src/report-service/target /opt/target

WORKDIR /opt/target

CMD ["java", "-jar", "report-service.war"]
