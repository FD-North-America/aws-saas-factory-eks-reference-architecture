FROM 486225280130.dkr.ecr.us-west-1.amazonaws.com/maven:3.6.3-amazoncorretto-11 as BUILD

COPY . /usr/src/payment-service

COPY settings.xml /root/.m2/settings.xml

ARG token
ENV CODEARTIFACT_AUTH_TOKEN $token

RUN mvn -Dmaven.repo.local=/root/.m2 --batch-mode -f /usr/src/payment-service/pom.xml clean package

FROM 486225280130.dkr.ecr.us-west-1.amazonaws.com/openjdk:11-jdk-slim

EXPOSE 80

COPY --from=BUILD /usr/src/payment-service/target /opt/target

WORKDIR /opt/target

CMD ["java", "-jar", "payment-service.war"]
