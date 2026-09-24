# Fase 1: Scarica Maven e compila il progetto partendo dal codice sorgente
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Fase 2: Crea l'immagine finale leggera con Java 17 per avviare il file compilato
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/pellegrinaggi-1.0.0.jar pellegrinaggi-1.0.0.jar

# Comunica a PandaStack la porta corretta
EXPOSE 3000

# Avvia l'applicazione forzando Spring Boot sulla porta 3000
ENTRYPOINT ["java", "-jar", "pellegrinaggi-1.0.0.jar", "--server.port=3000"]