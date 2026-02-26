FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

ARG VERSION
COPY target/lottery-server-${VERSION}.jar server.jar

ENTRYPOINT ["sh", "-c", "java -jar  server.jar ${JAR_ARGS:-}"]