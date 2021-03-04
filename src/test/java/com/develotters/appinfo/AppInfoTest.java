package com.develotters.appinfo;

import org.assertj.core.api.Condition;

import java.util.Map;
import java.util.function.Predicate;
import org.junit.jupiter.api.Test;

import static com.develotters.appinfo.test.PropertiesAssert.assertPropertyExists;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Jonatan Ivanov
 */
public class AppInfoTest {

    @Test
    public void systemPropertiesTest() {
        assertPropertyExists("java.version", new AppInfo(AppInfoComponent.SYSTEM_PROPERTIES));
    }

    @Test
    public void environmentVariablesTest() {
        assertPropertyExists("PATH", new AppInfo(AppInfoComponent.ENVIRONMENT_VARIABLES));
    }

    @Test
    public void runtimePropertiesTest() {
        assertPropertyExists("runtime.specVersion", new AppInfo(AppInfoComponent.RUNTIME));
    }

    @Test
    public void classLoadingPropertiesTest() {
        assertPropertyExists("classLoading.totalLoadedClassCount", new AppInfo(AppInfoComponent.CLASS_LOADING));
    }

    @Test
    public void compilationPropertiesTest() {
        assertPropertyExists("compilation.totalCompilationTime", new AppInfo(AppInfoComponent.COMPILATION));
    }

    @Test
    public void gcPropertiesTest() {
        assertPropertyExists("gc.0.collectionCount", new AppInfo(AppInfoComponent.GARBAGE_COLLECTOR));
    }

    @Test
    public void memoryPropertiesTest() {
        assertPropertyExists("memory.heapMemoryUsage.used", new AppInfo(AppInfoComponent.MEMORY));
    }

    @Test
    public void osPropertiesTest() {
        assertPropertyExists("os.name", new AppInfo(AppInfoComponent.OPERATING_SYSTEM));
    }

    @Test
    public void threadPropertiesTest() {
        assertPropertyExists("thread.threadCount", new AppInfo(AppInfoComponent.THREAD));
    }

    @Test
    public void allPropertiesTest() {
        AppInfo appInfo = new AppInfo(AppInfoComponent.ALL);

        assertPropertyExists("java.version", appInfo);
        assertPropertyExists("PATH", appInfo);
        assertPropertyExists("runtime.specVersion", appInfo);
        assertPropertyExists("classLoading.totalLoadedClassCount", appInfo);
        assertPropertyExists("compilation.totalCompilationTime", appInfo);
        assertPropertyExists("gc.0.collectionCount", appInfo);
        assertPropertyExists("memory.heapMemoryUsage.used", appInfo);
        assertPropertyExists("os.name", appInfo);
        assertPropertyExists("thread.threadCount", appInfo);
    }

    @Test
    public void defaultPropertiesTest() {
        AppInfo appInfo = new AppInfo();

        assertPropertyExists("java.version", appInfo);
        assertPropertyExists("PATH", appInfo);
        assertPropertyExists("runtime.specVersion", appInfo);
        assertPropertyExists("classLoading.totalLoadedClassCount", appInfo);
        assertPropertyExists("compilation.totalCompilationTime", appInfo);
        assertPropertyExists("gc.0.collectionCount", appInfo);
        assertPropertyExists("memory.heapMemoryUsage.used", appInfo);
        assertPropertyExists("os.name", appInfo);
        assertPropertyExists("thread.threadCount", appInfo);
    }

    @Test
    public void filteredPropertiesTest() {
        AppInfo appInfo =  new AppInfo();
        Map<String, String> properties = appInfo.getProperties("os.", "system.");
        String prettyPrint = appInfo.prettyPrint("os.", "system.");

        Predicate<String> osPredicate = s -> s.contains("os.");
        Predicate<String> systemPredicate = s -> s.contains("system.");
        Condition<String> osOrSystemProperty = new Condition<>(osPredicate.or(systemPredicate), "os or system property");

        assertThat(properties.keySet()).are(osOrSystemProperty);
        assertThat(prettyPrint.split(System.getProperty("line.separator"))).are(osOrSystemProperty);
    }
}
