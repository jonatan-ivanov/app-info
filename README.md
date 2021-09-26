# app-info

Example project to extract information from a running JVM.

## Execute

You can run the app using your favorite IDE or the command line, here are a few examples:
- `java -jar app-info.jar` (all the information that the app provides)
- `java -jar app-info.jar java os` (specify the details you need)
- `java -jar app-info.jar --server` (server mode, call it with `curl localhost:8080`)
- `java -jar app-info.jar os --server` (filtering works in server mode too)

## Tesing output in different environments

You can execute the application and save the output using any Docker image that can execute Java apps. You can run your own tests using [`test.sh`](https://github.com/jonatan-ivanov/app-info/blob/main/test.sh). In order to specify the images you want to test modify the `IMAGES` variable. If you also want to specify the information you want to get, specify the `APP_INFO_ARGS` variable.
