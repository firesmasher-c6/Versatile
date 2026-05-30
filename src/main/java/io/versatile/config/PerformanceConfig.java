package io.versatile.config;

import java.util.Map;

/**
 * Immutable configuration record for performance tuning.
 */
public record PerformanceConfig(
    int virtualThreadPoolSize,
    boolean useVirtualThreads
) {
    public static PerformanceConfig fromMap(Map<String, Object> map) {
        return new PerformanceConfig(
            (Integer) map.getOrDefault("virtual-thread-pool-size", 256),
            (Boolean) map.getOrDefault("use-virtual-threads", true)
        );
    }
}
