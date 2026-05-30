# Versatile Server Architecture Overview

## 🎯 Project Vision

**Versatile** is a high-performance Minecraft server implementation built from scratch using **Java 25 Virtual Threads**. It natively supports the **Paper API**, enabling compatibility with Spigot/Bukkit plugins while providing a modern, decoupled architecture for maximum performance and maintainability.

---

## 📦 Directory Structure

```
Versatile/
├── pom.xml                              # Maven configuration with Java 25 target
├── src/
│   ├── main/
│   │   ├── java/io/versatile/
│   │   │   ├── VersatileServer.java    # Main entry point & orchestration
│   │   │   ├── config/
│   │   │   │   ├── ConfigManager.java              # YAML config loader
│   │   │   │   ├── GlobalConfig.java               # Global config record
│   │   │   │   ├── PerformanceConfig.java          # Performance tuning record
│   │   │   │   ├── TickRateConfig.java             # Tick rate configuration
│   │   │   │   ├── WorldTickingConfig.java         # World ticking settings
│   │   │   │   ├── LoggingConfig.java              # Logging configuration
│   │   │   │   └── VersatileConfig.java            # Holder for all configs
│   │   │   ├── engine/
│   │   │   │   ├── tick/
│   │   │   │   │   └── TickLoop.java               # Optimized tick loop with Virtual Threads
│   │   │   │   └── world/
│   │   │   │       └── (World management - TBD)
│   │   │   └── paper/
│   │   │       └── ServerBridge.java               # Paper API implementation
│   │   └── resources/
│   │       ├── versatile.yml                       # Main server config
│   │       └── config/
│   │           ├── versatile-global.yml            # Global engine config
│   │           ├── versatile-world-defaults.yml    # World template config
│   │           └── plugin-api.yml                  # Plugin API settings
│   └── test/                           # Unit tests (TBD)
└── target/                             # Build output
```

---

## 🏗️ Architecture Layers

### 1. **Configuration Layer** (`config/`)

Responsibilities:
- Load YAML files from disk or extract from resources
- Parse configurations into type-safe Record objects
- Provide thread-safe access to configuration data

Key Classes:
- **ConfigManager**: Main configuration orchestrator
  - Loads all YAML files on startup
  - Extracts default configs from resources if missing
  - Validates configuration integrity
  
- **Configuration Records** (Java 25 Records):
  - `GlobalConfig`: Root configuration container
  - `PerformanceConfig`: Virtual Thread pool settings
  - `TickRateConfig`: Game tick rates (main, redstone, piston, block updates)
  - `WorldTickingConfig`: World-specific ticking parameters
  - `LoggingConfig`: Logger configuration

### 2. **Engine Layer** (`engine/`)

Responsibilities:
- Core game logic and tick processing
- Optimized tick loop using Virtual Threads
- World and entity management (future)

Key Components:
- **TickLoop** (`engine/tick/TickLoop.java`):
  - Runs in a dedicated Virtual Thread
  - Implements nanosecond-precision tick timing
  - Supports configurable tick rates for different game systems
  - Phase-based execution for redstone, pistons, block updates
  - Synchronization primitive for tick-based logic
  - ≈20 TPS by default (configurable)

### 3. **Paper API Layer** (`paper/`)

Responsibilities:
- Bridge between Versatile engine and Paper/Bukkit plugins
- Implement Bukkit/Paper Server interface
- Maintain API compatibility while keeping engine decoupled

Key Classes:
- **ServerBridge** (`paper/ServerBridge.java`):
  - Implements `org.bukkit.Server` interface
  - Provides stubs for:
    - World management
    - Player management
    - Plugin management
    - Scheduler (leverages Virtual Threads)
  - Allows plugins to hook into Versatile without tight coupling

### 4. **Main Application** (`VersatileServer.java`)

Orchestrates:
1. Configuration loading
2. Paper API bridge initialization
3. Tick loop startup
4. Graceful shutdown with resource cleanup

---

## 🚀 Java 25 Features & Modern Idioms

### Virtual Threads
```java
// TickLoop uses Virtual Threads for high-throughput IO:
Thread.ofVirtual()
    .name("Versatile-TickLoop")
    .start(this);
```

### Records (Immutable Data Classes)
```java
// Type-safe, immutable configuration objects:
public record PerformanceConfig(
    int virtualThreadPoolSize,
    boolean useVirtualThreads
) { }
```

### Pattern Matching (Future)
- Prepared for Java 25 pattern matching in `ServerBridge`

---

## ⚙️ Configuration System

### YAML Configuration Files

#### 1. **versatile.yml** (Server Entry Point)
- Server name, MOTD, port, max players
- Includes references to config files

#### 2. **config/versatile-global.yml** (Engine Tuning)
```yaml
performance:
  virtual-thread-pool-size: 256
  use-virtual-threads: true

tick-rates:
  main: 20           # Main game tick rate (TPS)
  redstone: 20       # Redstone signal propagation
  piston: 20         # Piston extension/retraction
  block-updates: 20  # Random block updates

world-ticking:
  chunk-load-radius: 12
  entity-culling-enabled: true
  max-entities-per-chunk: 16
```

#### 3. **config/versatile-world-defaults.yml** (World Template)
- Default difficulty, game mode, PvP settings
- Environmental spawn limits
- Performance tuning per world

#### 4. **config/plugin-api.yml** (Plugin System)
- Paper API version
- Plugin directory paths
- Async event configuration
- Plugin thread pool size

### Configuration Loading Strategy

```
On Startup:
  1. Check if config files exist in server root
  2. If missing, extract from JAR resources
  3. Parse YAML → Record objects
  4. Validate and log configuration summary
  5. Make available to engine components
```

---

## 🎯 Tick Loop Design

### Execution Model

```
┌─────────────────────────────────────────┐
│       Versatile Tick Loop               │
│     (Virtual Thread, 50ms intervals)    │
└────────────────────┬────────────────────┘
                     │
      ┌──────────────┼──────────────┐
      │              │              │
      ▼              ▼              ▼
  Main Tick    Redstone Tick   Piston Tick
  (1x/cycle)   (every Nth)      (every Nth)
      │              │              │
      └──────────────┼──────────────┘
                     │
                     ▼
            Block Update Tick
            (Random, every Nth)
                     │
                     ▼
            ┌─────────────────┐
            │  Tick Callback  │
            │  (Synchronized) │
            └─────────────────┘
```

### Performance Characteristics

- **Nanosecond-precision timing**: Uses `System.nanoTime()` for accurate tick intervals
- **Non-blocking synchronization**: Virtual Threads yield control, not block
- **Configurable rates**: Each subsystem (redstone, piston) runs at configured rate
- **Memory efficient**: Virtual Threads have minimal memory overhead

---

## 🔌 Paper API Bridge

### Design Principles

1. **Decoupling**: Engine logic independent from API implementation
2. **Gradual Implementation**: Stub methods clearly marked with TODO comments
3. **Plugin Compatibility**: Full Paper/Bukkit interface support
4. **Virtual Thread Integration**: Scheduler leverages Virtual Threads for async tasks

### Key Stub Methods (Priority Order)

**Critical (Tier 1)**:
- World management (getWorld, createWorld, getWorlds)
- Player management (getPlayer, getOnlinePlayers)
- Plugin manager & command handling

**Important (Tier 2)**:
- Event system (async event support with Virtual Threads)
- Task scheduler (async tasks via Virtual Threads)
- Configuration access

**Optional (Tier 3)**:
- Scoreboard API
- Boss bar API
- Block break/place events

---

## 🔄 Startup Sequence

```
1. VersatileServer.main()
   └─ configureLogging() → Set up SLF4J-style logging

2. VersatileServer constructor
   └─ ConfigManager(serverRoot)
   └─ ServerBridge() → Paper API initialization

3. VersatileServer.startup()
   ├─ ConfigManager.loadConfigurations()
   │  └─ Extract defaults from JAR if missing
   │  └─ Parse YAML → Record objects
   │
   ├─ ServerBridge initialization
   │  └─ Log server name, version, API version
   │
   ├─ TickLoop.start()
   │  └─ Launch Virtual Thread for tick loop
   │
   ├─ setupShutdownHook()
   │  └─ Register graceful shutdown handler
   │
   └─ Await indefinitely (while server is running)

4. Graceful Shutdown (on SIGTERM, CTRL+C, etc)
   ├─ Stop tick loop
   ├─ Save world data
   ├─ Unload plugins
   └─ Exit JVM
```

---

## 🛠️ Building & Running

### Build
```bash
mvn clean package
```

Output: `target/versatile-core.jar`

### Run
```bash
java -jar target/versatile-core.jar
```

Optional: Specify custom server root
```bash
java -jar target/versatile-core.jar /path/to/server
```

---

## 📋 Development Roadmap

### Phase 1: Core Foundation ✅
- [x] Configuration management (YAML + Records)
- [x] Paper API bridge (ServerBridge)
- [x] Optimized tick loop (Virtual Threads)
- [x] Main entry point

### Phase 2: Engine Implementation (In Progress)
- [ ] World management system
- [ ] Entity system with physics
- [ ] Chunk loading/unloading
- [ ] Block updates & random ticks

### Phase 3: Plugin System
- [ ] Plugin loader
- [ ] Event system
- [ ] Command framework
- [ ] Plugin class isolation

### Phase 4: Network & Players
- [ ] Netty-based network protocol
- [ ] Player connection handling
- [ ] Chunk synchronization
- [ ] Entity position sync

### Phase 5: Performance Optimization
- [ ] Memory profiling & optimization
- [ ] Virtual Thread tuning
- [ ] Chunk pre-generation
- [ ] Entity AI optimization

---

## 🎓 Key Design Decisions

### 1. Java 25 Virtual Threads
**Rationale**: 
- Handle thousands of concurrent connections efficiently
- Simplify async IO code (no callbacks/reactive frameworks)
- Natural fit for Minecraft's player-per-thread model

### 2. Records for Configuration
**Rationale**:
- Immutable, thread-safe config objects
- Built-in equals/hashCode/toString
- Reduced boilerplate
- Aligns with modern Java practices

### 3. Decoupled Engine + API
**Rationale**:
- Paper API is a historical contract, not optimal design
- Engine should evolve independently
- Easy to swap API implementation (e.g., Spigot, Forge)
- Cleaner codebase

### 4. YAML Configuration Extraction
**Rationale**:
- Users can customize without recompiling
- Sensible defaults embedded in JAR
- Easy deployment (single JAR file)

### 5. Tick Loop Synchronization
**Rationale**:
- Game logic runs on deterministic tick boundaries
- Prevents race conditions in world state
- Simple mental model for developers

---

## 📚 Component Interaction Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                        VersatileServer                           │
│                    (Main Orchestrator)                           │
└────────┬─────────────────────────────────────────────────────────┘
         │
    ┌────┴────────────────────────────────────┐
    │                                          │
    ▼                                          ▼
┌─────────────────┐                    ┌──────────────────┐
│ ConfigManager   │                    │  ServerBridge    │
│ (Config Layer)  │                    │  (Paper API)     │
├─────────────────┤                    ├──────────────────┤
│ loadConfigs()   │                    │ getWorld()       │
│ getGlobalConfig │                    │ getPlayer()      │
│ getServerRoot() │                    │ getPluginMgr()   │
└─────────────────┘                    └──────────────────┘
    │                                          │
    │                                          │
    └─────────────────────┬────────────────────┘
                          │
                          ▼
                  ┌───────────────────┐
                  │  TickLoop Engine  │
                  │ (Core Game Logic) │
                  ├───────────────────┤
                  │ performTick()     │
                  │ onMainTick()      │
                  │ onRedstoneTick()  │
                  │ onPistonTick()    │
                  └───────────────────┘
                          │
         ┌────────────────┼────────────────┐
         │                │                │
         ▼                ▼                ▼
    World Manager    Entity System   Chunk Manager
   (Future)         (Future)         (Future)
```

---

## 🔐 Thread Safety

### ConfigManager
- **Thread-safe**: All reads after initialization
- Loads once at startup, then read-only

### TickLoop
- **Synchronized tick boundary**: `synchronized(tickLock)`
- All game state modifications happen within tick
- Virtual Threads yield at tick boundaries

### ServerBridge
- **Stateless**: Returns immutable config data
- Methods are pure or delegate to safe components

---

## 📖 Code Style Guidelines

- **Modern Java 25 idioms**: Records, pattern matching (future), virtual threads
- **Meaningful names**: `virtualThreadPoolSize` not `vtp`
- **Clear documentation**: Javadoc on public classes/methods
- **No premature optimization**: Clarity > Performance (until profiling says otherwise)
- **Single Responsibility**: Each class has one reason to change

---

## 🚀 Next Steps

1. **Build & Verify**: `mvn clean package`
2. **Test Configuration Loading**: Run and verify YAML extraction
3. **Implement World Manager**: Load/unload world folders
4. **Add Entity System**: Core physics & state
5. **Network Protocol**: Netty-based client communication

---

## 📝 Notes for Future Developers

- **Virtual Thread stack traces**: Can be deep; use IDE debugger
- **Tick synchronization is critical**: All mutable state changes must be within `synchronized(tickLock)`
- **Paper API evolution**: Methods marked TODO should be prioritized by plugin demand
- **Performance profiling**: Use `jfr` (Java Flight Recorder) with Virtual Threads
- **Testing strategy**: Unit test Records, integration test ConfigManager, benchmark TickLoop

---

**Architecture Last Updated**: 2026-05-30  
**Target Java Version**: OpenJDK 25  
**Build Tool**: Apache Maven 3.9.16
