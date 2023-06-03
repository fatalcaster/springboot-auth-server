FROM gradle:jdk19-alpine AS BUILD_STAGE
COPY --chown=gradle:gradle . /home/gradle
RUN gradle build || return 1


FROM eclipse-temurin:19-jdk-alpine
ENV ARTIFACT_NAME=auth-provider-0.0.1.jar
ENV APP_HOME=/app
COPY --from=BUILD_STAGE /home/gradle/build/libs/$ARTIFACT_NAME $APP_HOME/
WORKDIR $APP_HOME
ENTRYPOINT exec java -jar ${ARTIFACT_NAME}