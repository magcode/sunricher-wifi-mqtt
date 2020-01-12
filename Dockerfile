FROM maven:3.6.3-jdk-14 as build
COPY src /usr/src/app/src/src
COPY pom.xml /usr/src/app/src/
WORKDIR /usr/src/app/src
RUN mvn clean install

FROM openjdk:14-alpine
WORKDIR /usr/app
COPY --from=build /usr/src/app/src/target/sunricher-mqtt-*-jar-with-dependencies.jar sunricher-mqtt.jar
RUN touch sunricher.properties
VOLUME ["/usr/app"]
ENTRYPOINT ["java", "-jar", "sunricher-mqtt.jar"]
