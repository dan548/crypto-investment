FROM openjdk:17-alpine

EXPOSE 8080

COPY .build/libs/crypto-investment-0.0.1-SNAPSHOT.jar /usr/app
WORKDIR /usr/app

ENTRYPOINT ["java", "-jar", "crypto-investment-0.0.1-SNAPSHOT.jar"]