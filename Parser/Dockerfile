FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

COPY src ./src

CMD ["./mvnw", "spring-boot:run"] 