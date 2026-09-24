FROM eclipse-temurin:17-jdk
ADD  target/pellegrinaggi-1.0.0.jar pellegrinaggi-1.0.0.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","pellegrinaggi-1.0.0.jar"]