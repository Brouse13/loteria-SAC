FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

ARG VERSION
COPY target/lottery-client-${VERSION}.jar client.jar

ENTRYPOINT ["sh", "-c", "java -jar client.jar ${JAR_ARGS:-}"]