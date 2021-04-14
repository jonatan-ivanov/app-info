APP_NAME=app-info

pack-build:
	pack build '$(APP_NAME)' \
		--env 'BP_GRADLE_BUILD_ARGUMENTS=clean assemble --no-daemon' \
		--env 'BP_JVM_VERSION=16'

docker-build:
	docker build -t '$(APP_NAME)' .

docker-run:
	docker run --rm '$(APP_NAME):latest'
