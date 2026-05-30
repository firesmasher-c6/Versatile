package io.versatile.config;

/**
 * Immutable configuration record for logging.
 */
public record LoggingConfig(
    String level,
    String file,
    String maxFileSize
) {
    public static LoggingConfig fromMap(java.util.Map<String, Object> map) {
        return new LoggingConfig(
            (String) map.getOrDefault("level", "INFO"),
            (String) map.getOrDefault("file", "logs/versatile.log"),
            (String) map.getOrDefault("max-file-size", "10MB")
        );
    }
}
