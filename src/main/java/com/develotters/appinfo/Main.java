package com.develotters.appinfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.develotters.appinfo.http.SimpleHttpServer;

/**
 * @author Jonatan Ivanov
 */
public class Main {
    private static final AppInfo APP_INFO = new AppInfo();

    public static void main(String[] args) throws IOException {
        List<String> arguments = Arrays.stream(args).collect(Collectors.toList());
        if (arguments.contains("--server")) {
            arguments.remove("--server");
            new SimpleHttpServer(() -> getInfo(arguments)).start();
        }
        else {
            System.out.println(getInfo(arguments));
        }
    }

    private static String getInfo(List<String> keys) {
        if (keys.isEmpty()) {
            return APP_INFO.prettyPrint();
        }
        else {
            return APP_INFO.prettyPrint(keys);
        }
    }
}
