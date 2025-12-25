FROM openjdk:17

WORKDIR /app

COPY target/team-sphere-1.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
