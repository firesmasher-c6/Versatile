package io.versatile.config;

import java.util.Map;

/**
 * Immutable configuration record for world ticking parameters.
 */
public record WorldTickingConfig(
    int chunkLoadRadius,
    boolean entityCullingEnabled,
    int maxEntitiesPerChunk
) {
    public static WorldTickingConfig fromMap(Map<String, Object> map) {
        return new WorldTickingConfig(
            (Integer) map.getOrDefault("chunk-load-radius", 12),
            (Boolean) map.getOrDefault("entity-culling-enabled", true),
            (Integer) map.getOrDefault("max-entities-per-chunk", 16)
        );
    }
}
