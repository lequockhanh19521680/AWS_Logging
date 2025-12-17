FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
ARG MODULE_NAME
RUN mvn clean package -pl ${MODULE_NAME} -am -DskipTests

FROM eclipse-temurin:21-jre-alpine
ARG MODULE_NAME
WORKDIR /app
COPY --from=build /app/${MODULE_NAME}/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
