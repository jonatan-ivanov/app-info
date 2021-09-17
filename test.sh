#! /bin/bash

IMAGES=(
    'openjdk:8-jdk-alpine'
    'adoptopenjdk:8-jre-hotspot'
    'adoptopenjdk:8-jre-openj9'
)

OUTPUT_DIR='test-out'
mkdir -p "$OUTPUT_DIR"

for DOCKER_IMAGE in "${IMAGES[@]}"; do
    echo "Testing $DOCKER_IMAGE"

    export DOCKER_IMAGE
    envsubst < 'Dockerfile.tmpl' > Dockerfile

    docker build -t app-info .
    docker run --rm app-info > "$OUTPUT_DIR/$DOCKER_IMAGE"
    docker rmi app-info:latest
    rm Dockerfile
done
