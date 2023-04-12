FROM openjdk:17
COPY . /app
WORKDIR /app
RUN chmod +x ./gradlew
RUN microdnf install findutils
RUN ./gradlew clean build --no-daemon > /dev/null 2>&1 || true
ENV PORT=80
EXPOSE 80
ENTRYPOINT java -jar build/libs/statmoonbotlistener-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=${ENV_NAME}

