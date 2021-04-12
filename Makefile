APP_NAME=app-info

pack-build:
	pack build '$(APP_NAME)' \
		--env 'BP_GRADLE_BUILD_ARGUMENTS=--no-daemon assemble' \
		--env 'BP_GRADLE_BUILT_ARTIFACT=build/libs/$(APP_NAME).jar' \
		--env 'BP_JVM_VERSION=16'

docker-build:
	docker build -t '$(APP_NAME)' .

docker-run:
	docker run --rm '$(APP_NAME):latest'
