APP_NAME=app-info

pack-build:
	pack build '$(APP_NAME)' \
		--env 'BP_GRADLE_BUILT_ARTIFACT=build/libs/$(APP_NAME).jar' \
		--env 'BP_JVM_VERSION=15'
