APP_NAME=app-info

pack-build:
	pack build '$(APP_NAME)' --env BP_JVM_VERSION=17

pack-native-build:
	pack build '$(APP_NAME)' --env BP_NATIVE_IMAGE=true

docker-run:
	docker run --rm --publish 8080:8080 '$(APP_NAME):latest' --server
