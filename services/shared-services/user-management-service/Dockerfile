FROM 486225280130.dkr.ecr.us-east-1.amazonaws.com/maven:3.6.3-amazoncorretto-11 as BUILD

#ADD m2.tar.gz /root

COPY . /usr/src/user-management-service
RUN mvn -Dmaven.repo.local=/root/m2 --batch-mode -f /usr/src/user-management-service/pom.xml clean package

FROM 486225280130.dkr.ecr.us-east-1.amazonaws.com/openjdk:11-jdk-slim
EXPOSE 80
COPY --from=BUILD /usr/src/user-management-service/target /opt/target
WORKDIR /opt/target

CMD ["java", "-jar", "user-management-service.war"]

