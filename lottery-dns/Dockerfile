FROM amazoncorretto:17-alpine-jdk AS client

WORKDIR /app

ARG VERSION
COPY target/lottery-dns-${VERSION}.jar dns.jar

ENTRYPOINT ["sh", "-c", "java -jar dns.jar ${JAR_ARGS:-}"]