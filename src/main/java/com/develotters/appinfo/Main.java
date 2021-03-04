package com.develotters.appinfo;

/**
 * @author Jonatan Ivanov
 */
public class Main {
    public static void main(String[] args) {
        AppInfo appInfo = new AppInfo();
        if (args.length != 0) {
            System.out.println(appInfo.prettyPrint(args));
        }
        else {
            System.out.println(appInfo.prettyPrint());
        }
    }
}
