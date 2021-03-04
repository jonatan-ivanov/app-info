package com.develotters.appinfo;

/**
 * @author Jonatan Ivanov
 */
public enum AppInfoComponent {
    SYSTEM_PROPERTIES,
    ENVIRONMENT_VARIABLES,
    RUNTIME,
    CLASS_LOADING,
    COMPILATION,
    GARBAGE_COLLECTOR,
    MEMORY,
    OPERATING_SYSTEM,
    THREAD;

    public static final AppInfoComponent[] ALL = AppInfoComponent.values();
}
