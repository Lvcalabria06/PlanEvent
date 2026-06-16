# Build: Maven compila o backend e roda o build do frontend (Vite → static no JAR).
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY domain/pom.xml domain/
COPY application/pom.xml application/
COPY infrastructure/pom.xml infrastructure/
COPY presentation-frontend/pom.xml presentation-frontend/
COPY presentation-backend/pom.xml presentation-backend/

RUN mvn -B -pl presentation-backend -am dependency:go-offline -Dmaven.test.skip=true

COPY domain/ domain/
COPY application/ application/
COPY infrastructure/ infrastructure/
COPY presentation-frontend/ presentation-frontend/
COPY presentation-backend/ presentation-backend/

RUN mvn -B -pl presentation-backend -am package -Dmaven.test.skip=true

# Runtime: JRE enxuto servindo API + frontend estático na porta 3000.
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /app/presentation-backend/target/PlanEvent-presentation-backend-*.jar app.jar

EXPOSE 3000

ENV DB_URL=jdbc:postgresql://planevent-db:5432/planevent \
    DB_USERNAME=planevent \
    DB_PASSWORD=planevent

ENTRYPOINT ["java", "-jar", "app.jar"]
