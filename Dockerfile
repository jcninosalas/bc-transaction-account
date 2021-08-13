FROM adoptopenjdk/openjdk11

RUN adduser --system --group spring
USER spring:spring
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} bc-transactions-service-0.0.1.jar
ENTRYPOINT ["java", "-jar", "/bc-transactions-service-0.0.1.jar"]