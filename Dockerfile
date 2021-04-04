FROM openjdk:11
ARG VERSION
COPY build/libs/covid-contact-server-${VERSION}.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
