package io.versatile.config;

import org.yaml.snakeyaml.Yaml;
import java.io.*;
import java.nio.file.*;
import java.util.Map;
import java.util.logging.Logger;

public class ConfigManager {
    private static final Logger logger = Logger.getLogger(ConfigManager.class.getName());
    private static ConfigManager instance;
    private final Yaml yaml = new Yaml();

    public static ConfigManager getInstance() {
        if (instance == null) instance = new ConfigManager();
        return instance;
    }

    /**
     * Loads the versatile-global.yml and returns a GlobalConfig record.
     */
    public GlobalConfig loadGlobalConfig() {
        File configFile = new File("versatile-global.yml");

        // 1. If file doesn't exist, extract from resources
        if (!configFile.exists()) {
            logger.info("Configuration file not found. Extracting defaults...");
            saveDefaultConfig("config/versatile-global.yml", configFile);
        }

        // 2. Parse YAML
        try (InputStream inputStream = new FileInputStream(configFile)) {
            Map<String, Object> data = yaml.load(inputStream);
            return GlobalConfig.fromMap(data);
        } catch (IOException e) {
            logger.severe("Failed to load configuration: " + e.getMessage());
            throw new RuntimeException("Could not load global configuration", e);
        }
    }

    private void saveDefaultConfig(String resourcePath, File destination) {
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) throw new FileNotFoundException("Resource not found: " + resourcePath);
            Files.copy(in, destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            logger.severe("Could not save default config: " + e.getMessage());
        }
    }
}