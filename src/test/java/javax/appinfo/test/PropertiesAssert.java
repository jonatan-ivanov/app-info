/*
 * Copyright 2016 Expedia, Inc. All rights reserved. EXPEDIA PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.appinfo.test;

import javax.appinfo.AppInfo;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jonatan Ivanov
 */
public class PropertiesAssert {
    public static void assertPropertyExists(String key, AppInfo appInfo) {
        assertThat(appInfo.getProperties()).containsKeys(key);
        assertThat(appInfo.getProperties(key)).containsKeys(key);

        assertPropertyExists(key, appInfo.prettyPrint());
        assertPropertyExists(key, appInfo.prettyPrint(key));
    }

    public static void assertPropertyExists(String key, String content) {
        assertThat(content).contains(key + ": ");
    }

    public static void assertPropertyDoesNotExist(String key, String content) {
        assertThat(content).doesNotContain(key + ": ");
    }
}
