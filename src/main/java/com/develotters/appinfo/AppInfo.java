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
        return Collections.unmodifiableMap(
            collectProperties().entrySet().stream()
                .filter(entry -> Arrays.stream(keys).anyMatch(key -> entry.getKey().toLowerCase().contains(key.toLowerCase())))
                .collect(ENTRY_COLLECTOR)
        );
    }

    public String prettyPrint() {
        return prettyPrint(getProperties());
    }

    public String prettyPrint(String... keys) {
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
            addSystemProperties(props);
        }
        if (components.contains(ENVIRONMENT_VARIABLES)) {
            props.putAll(System.getenv());
        }
        if (components.contains(RUNTIME)) {
            addRuntimeInfo(props);
        }
        if (components.contains(CLASS_LOADING)) {
            addClassLoadingInfo(props);
        }
        if (components.contains(COMPILATION)) {
            addCompilationInfo(props);
        }
        if (components.contains(GARBAGE_COLLECTOR)) {
            addGcInfo(props);
        }
        if (components.contains(MEMORY)) {
            addMemoryInfo(props);
        }
        if (components.contains(OPERATING_SYSTEM)) {
            addOsInfo(props);
        }
        if (components.contains(THREAD)) {
            addThreadInfo(props);
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
        props.put("runtime.availableProcessors", runtime.availableProcessors());
        props.put("runtime.freeMemory", runtime.freeMemory());
        props.put("runtime.maxMemory", runtime.maxMemory());
        props.put("runtime.totalMemory", runtime.totalMemory());

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        props.put("runtime.classPath", runtimeMXBean.getClassPath());
        props.put("runtime.inputArguments", runtimeMXBean.getInputArguments());
        props.put("runtime.libraryPath", runtimeMXBean.getLibraryPath());
        props.put("runtime.managementSpecVersion", runtimeMXBean.getManagementSpecVersion());
        props.put("runtime.name", runtimeMXBean.getName());
        props.put("runtime.specName", runtimeMXBean.getSpecName());
        props.put("runtime.specVendor", runtimeMXBean.getSpecVendor());
        props.put("runtime.specVersion", runtimeMXBean.getSpecVersion());
        props.put("runtime.startTime", runtimeMXBean.getStartTime());
        props.put("runtime.uptime", runtimeMXBean.getUptime());
        props.put("runtime.vmName", runtimeMXBean.getVmName());
        props.put("runtime.vmVendor", runtimeMXBean.getVmVendor());
        props.put("runtime.vmVersion", runtimeMXBean.getVmVersion());
    }

    private void addClassLoadingInfo(Map<Object, Object> props) {
        ClassLoadingMXBean classLoadingMXBean = ManagementFactory.getClassLoadingMXBean();
        props.put("classLoading.loadedClassCount", classLoadingMXBean.getLoadedClassCount());
        props.put("classLoading.totalLoadedClassCount", classLoadingMXBean.getTotalLoadedClassCount());
        props.put("classLoading.unloadedClassCount", classLoadingMXBean.getUnloadedClassCount());
    }

    private void addCompilationInfo(Map<Object, Object> props) {
        CompilationMXBean compilationMXBean = ManagementFactory.getCompilationMXBean();
        props.put("compilation.name", compilationMXBean.getName());
        props.put("compilation.totalCompilationTime", compilationMXBean.getTotalCompilationTime());
    }

    private void addGcInfo(Map<Object, Object> props) {
        List<GarbageCollectorMXBean> gcMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        for (int i = 0; i < gcMXBeans.size(); i++) {
            GarbageCollectorMXBean gcMXBean = gcMXBeans.get(i);
            props.put("gc." + i + ".name", gcMXBean.getName());
            props.put("gc." + i + ".objectName", gcMXBean.getObjectName());
            props.put("gc." + i + ".collectionCount", gcMXBean.getCollectionCount());
            props.put("gc." + i + ".collectionTime", gcMXBean.getCollectionTime());
            props.put("gc." + i + ".memoryPoolNames", Arrays.toString(gcMXBean.getMemoryPoolNames()));
        }
    }

    private void addMemoryInfo(Map<Object, Object> props) {
        List<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
        for (int i = 0; i < memoryManagerMXBeans.size(); i++) {
            MemoryManagerMXBean memoryManagerMXBean = memoryManagerMXBeans.get(0);
            props.put("memoryManager." + i + ".name", memoryManagerMXBean.getName());
            props.put("memoryManager." + i + ".objectName", memoryManagerMXBean.getObjectName());
            props.put("memoryManager." + i + ".memoryPoolNames", Arrays.toString(memoryManagerMXBean.getMemoryPoolNames()));
        }

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        props.put("memory.heapMemoryUsage.init", memoryMXBean.getHeapMemoryUsage().getInit());
        props.put("memory.heapMemoryUsage.used", memoryMXBean.getHeapMemoryUsage().getUsed());
        props.put("memory.heapMemoryUsage.committed", memoryMXBean.getHeapMemoryUsage().getCommitted());
        props.put("memory.heapMemoryUsage.max", memoryMXBean.getHeapMemoryUsage().getMax());
        props.put("memory.nonHeapMemoryUsage.init", memoryMXBean.getNonHeapMemoryUsage().getInit());
        props.put("memory.nonHeapMemoryUsage.used", memoryMXBean.getNonHeapMemoryUsage().getUsed());
        props.put("memory.nonHeapMemoryUsage.committed", memoryMXBean.getNonHeapMemoryUsage().getCommitted());
        props.put("memory.nonHeapMemoryUsage.max", memoryMXBean.getNonHeapMemoryUsage().getMax());
        props.put("memory.objectPendingFinalizationCount", memoryMXBean.getObjectPendingFinalizationCount());

        List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
        for (int i = 0; i < memoryPoolMXBeans.size(); i++) {
            MemoryPoolMXBean memoryPoolMXBean = memoryPoolMXBeans.get(i);
            props.put("memoryPool." + i + ".name", memoryPoolMXBean.getName());
            props.put("memoryPool." + i + ".type", memoryPoolMXBean.getType());
            props.put("memoryPool." + i + ".collectionUsage", memoryPoolMXBean.getCollectionUsage());
            props.put("memoryPool." + i + ".memoryManagerNames", Arrays.toString(memoryPoolMXBean.getMemoryManagerNames()));
            props.put("memoryPool." + i + ".peakUsage", memoryPoolMXBean.getPeakUsage());
            props.put("memoryPool." + i + ".usage", memoryPoolMXBean.getUsage());

            boolean collectionUsageThresholdSupported = memoryPoolMXBean.isCollectionUsageThresholdSupported();
            props.put("memoryPool." + i + ".collectionUsageThresholdSupported", collectionUsageThresholdSupported);
            if (collectionUsageThresholdSupported) {
                props.put("memoryPool." + i + ".collectionUsageThreshold", memoryPoolMXBean.getCollectionUsageThreshold());
                props.put("memoryPool." + i + ".collectionUsageThresholdCount", memoryPoolMXBean.getCollectionUsageThresholdCount());
                props.put("memoryPool." + i + ".collectionUsageThresholdExceeded", memoryPoolMXBean.isCollectionUsageThresholdExceeded());
            }

            boolean usageThresholdSupported = memoryPoolMXBean.isUsageThresholdSupported();
            props.put("memoryPool." + i + ".usageThresholdSupported", usageThresholdSupported);
            if (usageThresholdSupported) {
                props.put("memoryPool." + i + ".usageThreshold", memoryPoolMXBean.getUsageThreshold());
                props.put("memoryPool." + i + ".usageThresholdCount", memoryPoolMXBean.getUsageThresholdCount());
                props.put("memoryPool." + i + ".usageThresholdExceeded", memoryPoolMXBean.isUsageThresholdExceeded());
            }
        }
    }

    private void addOsInfo(Map<Object, Object> props) {
        OperatingSystemMXBean osMXBean = ManagementFactory.getOperatingSystemMXBean();
        props.put("os.arch", osMXBean.getArch());
        props.put("os.availableProcessors", osMXBean.getAvailableProcessors());
        props.put("os.name", osMXBean.getName());
        props.put("os.systemLoadAverage", osMXBean.getSystemLoadAverage());
        props.put("os.version", osMXBean.getVersion());
    }

    private void addThreadInfo(Map<Object, Object> props) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        long[] threadIds = threadMXBean.getAllThreadIds();
        props.put("thread.threadIds", threadIds);
        props.put("thread.deadlockedThreads", threadMXBean.findDeadlockedThreads());
        props.put("thread.monitorDeadlockedThreads", threadMXBean.findMonitorDeadlockedThreads());
        props.put("thread.daemonThreadCount", threadMXBean.getDaemonThreadCount());
        props.put("thread.peakThreadCount", threadMXBean.getPeakThreadCount());
        props.put("thread.threadCount", threadMXBean.getThreadCount());
        props.put("thread.totalStartedThreadCount", threadMXBean.getTotalStartedThreadCount());

        for (long threadId : threadIds) {
            props.put("thread." + threadId + ".cpuTime", threadMXBean.getThreadCpuTime(threadId));
            props.put("thread." + threadId + ".userTime", threadMXBean.getThreadUserTime(threadId));
            props.put("thread." + threadId + ".info", threadMXBean.getThreadInfo(threadId));
        }

        props.put("thread.isCurrentThreadCpuTimeSupported", threadMXBean.isCurrentThreadCpuTimeSupported());
        props.put("thread.isSynchronizerUsageSupported", threadMXBean.isSynchronizerUsageSupported());
        props.put("thread.isObjectMonitorUsageSupported", threadMXBean.isObjectMonitorUsageSupported());
        props.put("thread.isThreadContentionMonitoringSupported", threadMXBean.isThreadContentionMonitoringSupported());
        props.put("thread.isThreadContentionMonitoringEnabled", threadMXBean.isThreadContentionMonitoringEnabled());
        props.put("thread.isThreadCpuTimeSupported", threadMXBean.isThreadCpuTimeSupported());
        props.put("thread.isThreadCpuTimeEnabled", threadMXBean.isThreadCpuTimeEnabled());
    }

    private static final BinaryOperator<String> MERGE_FUNCTION =
            (key ,value) -> { throw new IllegalStateException(format("Duplicate key: %s", key)); };

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
