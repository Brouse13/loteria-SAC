#!/bin/sh
set -e

case "$SERVER_TYPE" in
  client)
    echo "Starting Lottery Client..."
    exec java -jar lottery-client-1.0.jar "$@"
    ;;
  seller)
    echo "Starting Lottery Seller..."
    exec java -jar lottery-seller-1.0.jar "$@"
    ;;
  server)
    echo "Starting Lottery Server..."
    exec java -jar lottery-server-1.0.jar "$@"
    ;;
  *)
    echo "Error: APP_TARGET must be one of: client | seller | server"
    exit 1
    ;;
esac
