package io.versatile.engine.tick;

import io.versatile.config.TickRateConfig;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Optimized tick loop engine using Java 25 Virtual Threads for high-performance event processing.
 * Manages synchronized tick events for various game systems (main, redstone, piston, block updates).
 */
public class TickLoop implements Runnable {
    private static final Logger logger = Logger.getLogger(TickLoop.class.getName());
    
    private final TickRateConfig tickRates;
    private final AtomicBoolean running = new AtomicBoolean(false);
    private volatile long serverTime = 0L;
    private final Object tickLock = new Object();

    public TickLoop(TickRateConfig tickRates) {
        this.tickRates = tickRates;
    }

    /**
     * Starts the tick loop in a Virtual Thread.
     */
    public void start() {
        if (running.compareAndSet(false, true)) {
            Thread.ofVirtual()
                .name("Versatile-TickLoop")
                .start(this);
            logger.info("✓ Tick loop started (Main TPS: " + tickRates.main() + ")");
        }
    }

    /**
     * Stops the tick loop gracefully.
     */
    public void stop() {
        running.set(false);
        synchronized (tickLock) {
            tickLock.notifyAll();
        }
        logger.info("✓ Tick loop stopped");
    }

    /**
     * Main tick loop execution.
     */
    @Override
    public void run() {
        final long tickIntervalNanos = 1_000_000_000L / tickRates.main();
        long lastTickTime = System.nanoTime();

        while (running.get()) {
            try {
                long currentTime = System.nanoTime();
                long elapsedNanos = currentTime - lastTickTime;

                if (elapsedNanos >= tickIntervalNanos) {
                    performTick();
                    lastTickTime = currentTime;
                } else {
                    // Yield to allow other Virtual Threads to run
                    Thread.yield();
                }
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error during tick execution", e);
            }
        }
    }

    /**
     * Executes a single server tick with all subsystem updates.
     */
    private void performTick() {
        synchronized (tickLock) {
            serverTime++;
            
            // Execute main game logic
            onMainTick();

            // Execute redstone tick logic if enabled
            if (shouldTickRedstone()) {
                onRedstoneTick();
            }

            // Execute piston tick logic if enabled
            if (shouldTickPiston()) {
                onPistonTick();
            }

            // Execute block update tick logic if enabled
            if (shouldTickBlockUpdates()) {
                onBlockUpdateTick();
            }

            tickLock.notifyAll();
        }
    }

    /**
     * Main game tick - handles general server updates.
     */
    private void onMainTick() {
        // Placeholder: Will be connected to world/entity update systems
    }

    /**
     * Redstone tick - handles redstone signal propagation.
     */
    private void onRedstoneTick() {
        // Placeholder: Will be connected to redstone logic system
    }

    /**
     * Piston tick - handles piston extension/retraction.
     */
    private void onPistonTick() {
        // Placeholder: Will be connected to piston logic system
    }

    /**
     * Block update tick - handles random block updates (crops, leaves, etc).
     */
    private void onBlockUpdateTick() {
        // Placeholder: Will be connected to block update system
    }

    /**
     * Determines if redstone should tick this cycle.
     */
    private boolean shouldTickRedstone() {
        return tickRates.main() % tickRates.redstone() == 0;
    }

    /**
     * Determines if piston should tick this cycle.
     */
    private boolean shouldTickPiston() {
        return tickRates.main() % tickRates.piston() == 0;
    }

    /**
     * Determines if block updates should occur this cycle.
     */
    private boolean shouldTickBlockUpdates() {
        return tickRates.main() % tickRates.blockUpdates() == 0;
    }

    /**
     * Returns the current server time in ticks.
     */
    public long getServerTime() {
        return serverTime;
    }

    /**
     * Checks if the tick loop is running.
     */
    public boolean isRunning() {
        return running.get();
    }
}
