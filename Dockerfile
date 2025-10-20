
FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml mvnw* ./
COPY .mvn .mvn
COPY src src
RUN mvn -B -DskipTests package || true

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
