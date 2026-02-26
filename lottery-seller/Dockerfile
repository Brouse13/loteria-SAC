FROM amazoncorretto:17-alpine-jdk

WORKDIR /app

ARG VERSION
COPY target/lottery-seller-${VERSION}.jar seller.jar

ENTRYPOINT ["sh", "-c", "java -jar seller.jar ${JAR_ARGS:-}"]