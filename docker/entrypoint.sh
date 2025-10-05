#!/bin/sh
set -e

case "$SERVER_TYPE" in
  dns)
      echo "Starting Lottery Client..."
      exec java -jar lottery-dns-1.1.0.jar $JAR_ARGS
      ;;
  client)
    echo $JAR_ARGS
    echo "Starting Lottery Client..."
    exec java -jar lottery-client-1.1.0.jar $JAR_ARGS
    ;;
  seller)
    echo "Starting Lottery Seller..."
    exec java -jar lottery-seller-1.1.0.jar $JAR_ARGS
    ;;
  server)
    echo "Starting Lottery Server..."
    exec java -jar lottery-server-1.1.0.jar $JAR_ARGS
    ;;
  *)
    echo "Error: APP_TARGET must be one of: dns | client | seller | server"
    exit 1
    ;;
esac
