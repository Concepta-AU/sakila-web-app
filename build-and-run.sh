#!/usr/bin/env bash

set -e
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

docker build -t sakila-web-app "$SCRIPT_DIR/docker"

docker stop sakila-web-app 2> /dev/null || true
docker rm sakila-web-app 2> /dev/null || true

docker run --name sakila-web-app \
           -e POSTGRES_PASSWORD=not_secure \
           -p 15432:5432 \
           -d \
           sakila-web-app

echo "Application running, you should be able to connect to the database on port 15432, using postgres/not_secure as credentials."
