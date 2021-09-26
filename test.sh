#! /bin/bash

# JMX is not supported on GraalVM right now so you either:
# - Disable everything that is related to JMX: in Main.java AppInfo appInfo = new AppInfo(SYSTEM_PROPERTIES);
# - Disable native-image: comment out the last few lines of this file

./gradlew clean assemble

IMAGES=(
    'adoptopenjdk:8-jre-hotspot'
    'adoptopenjdk:8-jre-openj9'
    'adoptopenjdk:11-jre-hotspot'
    'adoptopenjdk:11-jre-openj9'
    'adoptopenjdk:16-jre-hotspot' # TODO: 17
    'adoptopenjdk:16-jre-openj9'  # TODO: 17

    'eclipse-temurin:8-jre'
    'eclipse-temurin:11-jre'
    'eclipse-temurin:17-jdk'

    'amazoncorretto:8'
    'amazoncorretto:8-alpine'
    'amazoncorretto:11'
    'amazoncorretto:11-alpine'
    'amazoncorretto:17'
    'amazoncorretto:17-alpine'

    'bellsoft/liberica-openjre-debian:8'
    'bellsoft/liberica-openjre-alpine-musl:8'
    'bellsoft/liberica-openjre-debian:11'
    'bellsoft/liberica-openjre-alpine-musl:11'
    'bellsoft/liberica-openjre-debian:17'
    'bellsoft/liberica-openjre-alpine-musl:17'

    'openjdk:8-jre-slim'
    # 8 alpine was discontinued
    'openjdk:11-jre-slim'
    # 11 alpine is not available
    'openjdk:17-slim'
    'openjdk:17-alpine'
    'openjdk:18-slim'
    'openjdk:18-alpine'

    'sapmachine:11'
    'sapmachine:17'

    'azul/zulu-openjdk:8'
    'azul/zulu-openjdk:11'
    'azul/zulu-openjdk:17'
    'azul/zulu-openjdk-alpine:8-jre'
    'azul/zulu-openjdk-alpine:11-jre'
    'azul/zulu-openjdk-alpine:17-jre'
)

OUTPUT_DIR='test-out'
APP_INFO_ARGS='java'
mkdir -p "$OUTPUT_DIR"

for DOCKER_IMAGE in "${IMAGES[@]}"; do
    echo "Testing $DOCKER_IMAGE"

    export DOCKER_IMAGE
    envsubst < 'Dockerfile.tmpl' > Dockerfile

    docker pull "$DOCKER_IMAGE"
    docker build -t app-info .
    docker run --rm app-info "$APP_INFO_ARGS" > "$OUTPUT_DIR/$(echo "$DOCKER_IMAGE" | tr '/' '_')"
    docker rmi app-info:latest
    rm Dockerfile
done

pack build app-info --env BP_NATIVE_IMAGE=true --path build/libs/app-info.jar
docker run --rm app-info "$APP_INFO_ARGS" > "$OUTPUT_DIR/paketo-buildpacks_native-image"
docker rmi app-info:latest
