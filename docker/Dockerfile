FROM openjdk:17

WORKDIR /app

ARG APP_DIR=*.jar

COPY ${APP_DIR} app.jar

ENTRYPOINT ["java","-Duser.timezone=Europe/Moscow","-jar","app.jar"]