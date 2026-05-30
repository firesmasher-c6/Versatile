package io.versatile;

import io.versatile.config.ConfigManager;
import io.versatile.config.GlobalConfig;
import io.versatile.engine.tick.TickLoop;
import java.util.logging.Logger;

/**
 * Main entry point for the Versatile server.
 * Handles configuration loading and engine bootstrap.
 */
public class VersatileServer {
    private static final Logger logger = Logger.getLogger(VersatileServer.class.getName());
    private static VersatileServer instance;
    
    private final GlobalConfig config;
    private final TickLoop tickLoop;

    public VersatileServer() {
        instance = this;
        logger.info("Initializing Versatile Server...");

        // 1. Load Configurations
        this.config = ConfigManager.getInstance().loadGlobalConfig();
        logger.info("Configuration loaded.");

        // 2. Initialize Engine Components
        this.tickLoop = new TickLoop(config.tickRates());

        // 3. Start the Engine
        this.tickLoop.start();
        logger.info("Versatile Server is now running.");
    }

    public static void main(String[] args) {
        VersatileServer server = new VersatileServer();

        // PTERODACTYL STATUS FIX: This string triggers the panel to switch to "Online"
        System.out.println("Done! For help, type \"help\"");

        // Register a shutdown hook to handle Pterodactyl stop signals
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown signal received. Stopping engine...");
            server.getTickLoop().stop();
        }));

        // Keep the process alive by blocking the main thread
        try {
            while (true) {
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.info("Main thread interrupted. Exiting.");
        }
    }

    public static VersatileServer getInstance() {
        return instance;
    }

    public GlobalConfig getConfig() {
        return config;
    }

    public TickLoop getTickLoop() {
        return tickLoop;
    }

}