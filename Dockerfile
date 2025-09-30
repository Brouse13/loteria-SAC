FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy jars
COPY lottery-client/target/lottery-client-1.0.jar .
COPY lottery-seller/target/lottery-seller-1.0.jar .
#COPY lottery-server/target/lottery-server-1.0.jar .

# Copy entrypoint
COPY docker/entrypoint.sh .
RUN chmod u+x entrypoint.sh

ENV SERVER_TYPE=client
ENTRYPOINT ["./entrypoint.sh"]

