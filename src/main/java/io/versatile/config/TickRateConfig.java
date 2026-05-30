package io.versatile.config;

import java.util.Map;

/**
 * Immutable configuration record for tick rates.
 */
public record TickRateConfig(
    int main,
    int redstone,
    int piston,
    int blockUpdates
) {
    public static TickRateConfig fromMap(Map<String, Object> map) {
        return new TickRateConfig(
            (Integer) map.getOrDefault("main", 20),
            (Integer) map.getOrDefault("redstone", 20),
            (Integer) map.getOrDefault("piston", 20),
            (Integer) map.getOrDefault("block-updates", 20)
        );
    }
}
