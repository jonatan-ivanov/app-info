package com.develotters.appinfo;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.develotters.appinfo.AppInfoComponent.*;
import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

/**
 * @author Jonatan Ivanov
 */
public class AppInfo {
    private final Set<AppInfoComponent> components;

    public AppInfo() {
        this(ALL);
    }

    public AppInfo(AppInfoComponent... components) {
        this(Arrays.asList(components));
    }

    public AppInfo(Iterable<AppInfoComponent> components) {
        this.components = StreamSupport.stream(components.spliterator(), false).collect(toSet());
    }

    public Map<String, String> getProperties() {
        return Collections.unmodifiableMap(collectProperties());
    }

    public Map<String, String> getProperties(String... keys) {
        return getProperties(Arrays.asList(keys));
    }

    public Map<String, String> getProperties(List<String> keys) {
        return Collections.unmodifiableMap(
            collectProperties().entrySet().stream()
                .filter(entry -> keys.stream().anyMatch(key -> entry.getKey().toLowerCase().contains(key.toLowerCase())))
                .collect(ENTRY_COLLECTOR)
        );
    }

    public String prettyPrint() {
        return prettyPrint(getProperties());
    }

    public String prettyPrint(String... keys) {
        return prettyPrint(Arrays.asList(keys));
    }

    public String prettyPrint(List<String> keys) {
        return prettyPrint(getProperties(keys));
    }

    private String prettyPrint(Map<String, String> properties) {
        return properties.entrySet().stream()
            .map(entry -> format("%s: %s", entry.getKey(), entry.getValue()))
            .collect(joining("\n"));
    }

    private Map<String, String> collectProperties() {
        Map<Object, Object> props = new TreeMap<>();

        if (components.contains(SYSTEM_PROPERTIES)) {
            safelyRun(() -> addSystemProperties(props));
        }
        if (components.contains(ENVIRONMENT_VARIABLES)) {
            safelyRun(() -> props.putAll(System.getenv()));
        }
        if (components.contains(RUNTIME)) {
            safelyRun(() -> addRuntimeInfo(props));
        }
        if (components.contains(CLASS_LOADING)) {
            safelyRun(() -> addClassLoadingInfo(props));
        }
        if (components.contains(COMPILATION)) {
            safelyRun(() -> addCompilationInfo(props));
        }
        if (components.contains(GARBAGE_COLLECTOR)) {
            safelyRun(() -> addGcInfo(props));
        }
        if (components.contains(MEMORY)) {
            safelyRun(() -> addMemoryInfo(props));
        }
        if (components.contains(OPERATING_SYSTEM)) {
            safelyRun(() -> addOsInfo(props));
        }
        if (components.contains(THREAD)) {
            safelyRun(() -> addThreadInfo(props));
        }

        return props.entrySet().stream().collect(ENTRY_TO_STRING_COLLECTOR);
    }

    private void addSystemProperties(Map<Object, Object> props) {
        props.putAll(System.getProperties());
        props.put("system.nanoTime", System.nanoTime());
        props.put("system.currentTimeMillis", System.currentTimeMillis());
        props.put("system.date", new Date());
        props.put("system.instant", Instant.now());
    }

    private void addRuntimeInfo(Map<Object, Object> props) {
        Runtime runtime = Runtime.getRuntime();
        props.put("runtime.availableProcessors", safelyGet(runtime::availableProcessors));
        props.put("runtime.freeMemory", safelyGet(runtime::freeMemory));
        props.put("runtime.maxMemory", safelyGet(runtime::maxMemory));
        props.put("runtime.totalMemory", safelyGet(runtime::totalMemory));

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        props.put("runtime.classPath", safelyGet(runtimeMXBean::getClassPath));
        props.put("runtime.inputArguments", safelyGet(runtimeMXBean::getInputArguments));
        props.put("runtime.libraryPath", safelyGet(runtimeMXBean::getLibraryPath));
        props.put("runtime.managementSpecVersion", safelyGet(runtimeMXBean::getManagementSpecVersion));
        props.put("runtime.name", safelyGet(runtimeMXBean::getName));
        props.put("runtime.specName", safelyGet(runtimeMXBean::getSpecName));
        props.put("runtime.specVendor", safelyGet(runtimeMXBean::getSpecVendor));
        props.put("runtime.specVersion", safelyGet(runtimeMXBean::getSpecVersion));
        props.put("runtime.startTime", safelyGet(runtimeMXBean::getStartTime));
        props.put("runtime.uptime", safelyGet(runtimeMXBean::getUptime));
        props.put("runtime.vmName", safelyGet(runtimeMXBean::getVmName));
        props.put("runtime.vmVendor", safelyGet(runtimeMXBean::getVmVendor));
        props.put("runtime.vmVersion", safelyGet(runtimeMXBean::getVmVersion));
    }

    private void addClassLoadingInfo(Map<Object, Object> props) {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        props.put("classLoading.loadedClassCount", safelyGet(classLoadingMXBean::getLoadedClassCount));
        props.put("classLoading.totalLoadedClassCount", safelyGet(classLoadingMXBean::getTotalLoadedClassCount));
        props.put("classLoading.unloadedClassCount", safelyGet(classLoadingMXBean::getUnloadedClassCount));
    }

    private void addCompilationInfo(Map<Object, Object> props) {
        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        props.put("compilation.name", safelyGet(compilationMXBean::getName));
        props.put("compilation.totalCompilationTime", safelyGet(compilationMXBean::getTotalCompilationTime));
    }

    private void addGcInfo(Map<Object, Object> props) {
        List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (int i = 0; i < gcMXBeans.size(); i++) {
            GarbageCollectorMXBean gcMXBean = gcMXBeans.get(i);
            props.put("gc." + i + ".name", safelyGet(gcMXBean::getName));
            props.put("gc." + i + ".objectName", safelyGet(gcMXBean::getObjectName));
            props.put("gc." + i + ".collectionCount", safelyGet(gcMXBean::getCollectionCount));
            props.put("gc." + i + ".collectionTime", safelyGet(gcMXBean::getCollectionTime));
            props.put("gc." + i + ".memoryPoolNames", Arrays.toString(safelyGet(gcMXBean::getMemoryPoolNames)));
        }
    }

    private void addMemoryInfo(Map<Object, Object> props) {
        List<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
        for (int i = 0; i < memoryManagerMXBeans.size(); i++) {
            MemoryManagerMXBean memoryManagerMXBean = memoryManagerMXBeans.get(i);
            props.put("memoryManager." + i + ".name", safelyGet(memoryManagerMXBean::getName));
            props.put("memoryManager." + i + ".objectName", safelyGet(memoryManagerMXBean::getObjectName));
            props.put("memoryManager." + i + ".memoryPoolNames", Arrays.toString(safelyGet(memoryManagerMXBean::getMemoryPoolNames)));
        }

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        props.put("memory.heapMemoryUsage.init", safelyGet(() -> memoryMXBean.getHeapMemoryUsage().getInit()));
        props.put("memory.heapMemoryUsage.used", safelyGet(() -> memoryMXBean.getHeapMemoryUsage().getUsed()));
        props.put("memory.heapMemoryUsage.committed", safelyGet(() -> memoryMXBean.getHeapMemoryUsage().getCommitted()));
        props.put("memory.heapMemoryUsage.max", safelyGet(() -> memoryMXBean.getHeapMemoryUsage().getMax()));
        props.put("memory.nonHeapMemoryUsage.init", safelyGet(() -> memoryMXBean.getNonHeapMemoryUsage().getInit()));
        props.put("memory.nonHeapMemoryUsage.used", safelyGet(() -> memoryMXBean.getNonHeapMemoryUsage().getUsed()));
        props.put("memory.nonHeapMemoryUsage.committed", safelyGet(() -> memoryMXBean.getNonHeapMemoryUsage().getCommitted()));
        props.put("memory.nonHeapMemoryUsage.max", safelyGet(() -> memoryMXBean.getNonHeapMemoryUsage().getMax()));

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        for (int i = 0; i < memoryPoolMXBeans.size(); i++) {
            MemoryPoolMXBean memoryPoolMXBean = memoryPoolMXBeans.get(i);
            props.put("memoryPool." + i + ".name", safelyGet(memoryPoolMXBean::getName));
            props.put("memoryPool." + i + ".type", safelyGet(memoryPoolMXBean::getType));
            props.put("memoryPool." + i + ".collectionUsage", safelyGet(memoryPoolMXBean::getCollectionUsage));
            props.put("memoryPool." + i + ".memoryManagerNames", Arrays.toString(safelyGet(memoryPoolMXBean::getMemoryManagerNames)));
            props.put("memoryPool." + i + ".peakUsage", safelyGet(memoryPoolMXBean::getPeakUsage));
            props.put("memoryPool." + i + ".usage", safelyGet(memoryPoolMXBean::getUsage));

            boolean collectionUsageThresholdSupported = memoryPoolMXBean.isCollectionUsageThresholdSupported();
            props.put("memoryPool." + i + ".collectionUsageThresholdSupported", collectionUsageThresholdSupported);
            if (collectionUsageThresholdSupported) {
                props.put("memoryPool." + i + ".collectionUsageThreshold", safelyGet(memoryPoolMXBean::getCollectionUsageThreshold));
                props.put("memoryPool." + i + ".collectionUsageThresholdCount", safelyGet(memoryPoolMXBean::getCollectionUsageThresholdCount));
                props.put("memoryPool." + i + ".collectionUsageThresholdExceeded", safelyGet(memoryPoolMXBean::isCollectionUsageThresholdExceeded));
            }

            boolean usageThresholdSupported = memoryPoolMXBean.isUsageThresholdSupported();
            props.put("memoryPool." + i + ".usageThresholdSupported", usageThresholdSupported);
            if (usageThresholdSupported) {
                props.put("memoryPool." + i + ".usageThreshold", safelyGet(memoryPoolMXBean::getUsageThreshold));
                props.put("memoryPool." + i + ".usageThresholdCount", safelyGet(memoryPoolMXBean::getUsageThresholdCount));
                props.put("memoryPool." + i + ".usageThresholdExceeded", safelyGet(memoryPoolMXBean::isUsageThresholdExceeded));
            }
        }
    }

    private void addOsInfo(Map<Object, Object> props) {
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        props.put("os.arch", safelyGet(osMXBean::getArch));
        props.put("os.availableProcessors", safelyGet(osMXBean::getAvailableProcessors));
        props.put("os.name", safelyGet(osMXBean::getName));
        props.put("os.systemLoadAverage", safelyGet(osMXBean::getSystemLoadAverage));
        props.put("os.version", safelyGet(osMXBean::getVersion));
    }

    private void addThreadInfo(Map<Object, Object> props) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        props.put("thread.threadIds", safelyGet(threadMXBean::getAllThreadIds));
        props.put("thread.deadlockedThreads", safelyGet(threadMXBean::findDeadlockedThreads));
        props.put("thread.monitorDeadlockedThreads", safelyGet(threadMXBean::findMonitorDeadlockedThreads));
        props.put("thread.daemonThreadCount", safelyGet(threadMXBean::getDaemonThreadCount));
        props.put("thread.peakThreadCount", safelyGet(threadMXBean::getPeakThreadCount));
        props.put("thread.threadCount", safelyGet(threadMXBean::getThreadCount));
        props.put("thread.totalStartedThreadCount", safelyGet(threadMXBean::getTotalStartedThreadCount));

        safelyRun(() -> addThreadIdInfo(threadMXBean, props));

        props.put("thread.isCurrentThreadCpuTimeSupported", safelyGet(threadMXBean::isCurrentThreadCpuTimeSupported));
        props.put("thread.isSynchronizerUsageSupported", safelyGet(threadMXBean::isSynchronizerUsageSupported));
        props.put("thread.isObjectMonitorUsageSupported", safelyGet(threadMXBean::isObjectMonitorUsageSupported));
        props.put("thread.isThreadContentionMonitoringSupported", safelyGet(threadMXBean::isThreadContentionMonitoringSupported));
        props.put("thread.isThreadContentionMonitoringEnabled", safelyGet(threadMXBean::isThreadContentionMonitoringEnabled));
        props.put("thread.isThreadCpuTimeSupported", safelyGet(threadMXBean::isThreadCpuTimeSupported));
        props.put("thread.isThreadCpuTimeEnabled", safelyGet(threadMXBean::isThreadCpuTimeEnabled));
    }

    private void addThreadIdInfo(ThreadMXBean threadMXBean, Map<Object, Object> props) {
        for (long threadId : threadMXBean.getAllThreadIds()) {
            props.put("thread." + threadId + ".cpuTime", safelyGet(() -> threadMXBean.getThreadCpuTime(threadId)));
            props.put("thread." + threadId + ".userTime", safelyGet(() -> threadMXBean.getThreadUserTime(threadId)));
            props.put("thread." + threadId + ".info", safelyGet(() -> threadMXBean.getThreadInfo(threadId)));
        }
    }

    private <T> T safelyGet(Supplier<T> supplier) {
        try {
            return supplier.get();
        }
        catch (Throwable throwable) {
            // swallow the error :(
            return null;
        }
    }

    private void safelyRun(Runnable runnable) {
        try {
            runnable.run();
        }
        catch (Throwable throwable) {
            // swallow the error :(
        }
    }

    private static final BinaryOperator<String> MERGE_FUNCTION =
            (key ,ignored) -> { throw new IllegalStateException(format("Duplicate key: %s", key)); };

    private static final Collector<Entry<String, String>, ?, Map<String, String>> ENTRY_COLLECTOR =
            Collectors.toMap(
                    Entry::getKey,
                    Entry::getValue,
                    MERGE_FUNCTION,
                    TreeMap::new
            );

    private static final Collector<Entry<?, ?>, ?, Map<String, String>> ENTRY_TO_STRING_COLLECTOR =
            Collectors.toMap(
                    entry -> toString(entry.getKey()),
                    entry -> toString(entry.getValue()),
                    MERGE_FUNCTION,
                    TreeMap::new
            );

    private static String toString(Object object) {
        if (object != null && object.getClass().isArray()) {
            Class<?> clazz = object.getClass();
            if (clazz == byte[].class) return Arrays.toString((byte[]) object);
            else if (clazz == short[].class) return Arrays.toString((short[]) object);
            else if (clazz == int[].class) return Arrays.toString((int[]) object);
            else if (clazz == long[].class) return Arrays.toString((long[]) object);
            else if (clazz == char[].class) return Arrays.toString((char[]) object);
            else if (clazz == float[].class) return Arrays.toString((float[]) object);
            else if (clazz == double[].class) return Arrays.toString((double[]) object);
            else if (clazz == boolean[].class) return Arrays.toString((boolean[]) object);
            else return Arrays.deepToString((Object[]) object);
        }
        else {
            return String.valueOf(object);
        }
    }
}
