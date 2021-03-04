package com.develotters.appinfo;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

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

    public static final Set<AppInfoComponent> ALL = Arrays.stream(AppInfoComponent.values()).collect(toSet());
}
