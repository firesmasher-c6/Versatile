package io.versatile.config;

import java.util.Map;

/**
 * Immutable configuration record for the global configuration.
 */
public record GlobalConfig(
    PerformanceConfig performance,
    TickRateConfig tickRates,
    WorldTickingConfig worldTicking,
    LoggingConfig logging
) {
    /**
     * Constructs a GlobalConfig from a YAML-parsed map.
     */
    @SuppressWarnings("unchecked")
    public static GlobalConfig fromMap(Map<String, Object> map) {
        Map<String, Object> perfMap = (Map<String, Object>) map.getOrDefault("performance", Map.of());
        Map<String, Object> tickMap = (Map<String, Object>) map.getOrDefault("tick-rates", Map.of());
        Map<String, Object> worldMap = (Map<String, Object>) map.getOrDefault("world-ticking", Map.of());
        Map<String, Object> logMap = (Map<String, Object>) map.getOrDefault("logging", Map.of());

        return new GlobalConfig(
            PerformanceConfig.fromMap(perfMap),
            TickRateConfig.fromMap(tickMap),
            WorldTickingConfig.fromMap(worldMap),
            LoggingConfig.fromMap(logMap)
        );
    }
}
