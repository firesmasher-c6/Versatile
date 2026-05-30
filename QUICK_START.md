# Versatile Server - Quick Start Guide

## 🚀 Quick Setup (5 Minutes)

### Prerequisites
- **Java**: OpenJDK 25 or later
- **Maven**: Apache Maven 3.9.16 or later
- **System**: Windows, macOS, or Linux

### Step 1: Clone/Setup
```bash
cd /path/to/versatile
```

### Step 2: Build
```bash
mvn clean package -DskipTests
```

**Expected Output**:
```
[INFO] BUILD SUCCESS
[INFO] Total time: 45.123 s
[INFO] Finished at: 2026-05-30T11:42:10Z
[INFO] ────────────────────────────────────────
```

### Step 3: Run
```bash
java -jar target/versatile-core.jar
```

**Expected Console Output**:
```
╔═══════════════════════════════════════════════════════════════╗
║          VERSATILE SERVER - High Performance Core             ║
║         Paper API Compatible Minecraft Server (Java 25)       ║
╚═══════════════════════════════════════════════════════════════╝

═══════════════════════════════════════════════════════════════
Initializing Versatile Server...
═══════════════════════════════════════════════════════════════

[1/4] Loading configurations...
[INFO] Configuration file found: versatile.yml
[INFO] Configuration file found: versatile-global.yml
[INFO] Configuration file found: versatile-world-defaults.yml
[INFO] Configuration file found: plugin-api.yml
[INFO] ✓ Server configuration loaded successfully
[INFO] ✓ Global configuration loaded successfully
[INFO] ✓ World defaults configuration loaded successfully
[INFO] ✓ Plugin API configuration loaded successfully
[INFO] Configuration Summary:
[INFO]   Performance - Virtual Thread Pool: 256
[INFO]   Tick Rates - Main: 20 tps
[INFO]   Tick Rates - Redstone: 20 tps
[INFO]   World Ticking - Chunk Load Radius: 12
[INFO]   Logging Level: INFO

[2/4] Initializing Paper API bridge...
[INFO] ✓ Versatile Server Bridge initialized (Paper API compatibility layer)
[INFO] Server name: Versatile
[INFO] Server version: 1.0-SNAPSHOT
[INFO] Bukkit API version: 1.21

[3/4] Starting optimized tick loop...
[INFO] ✓ Tick loop started (Main TPS: 20)

[4/4] Finalizing startup...
═══════════════════════════════════════════════════════════════
✓ Versatile Server is now ONLINE and accepting connections!
═══════════════════════════════════════════════════════════════
```

### Step 4: Shutdown
Press `Ctrl+C` to gracefully shut down the server.

```
═══════════════════════════════════════════════════════════════
Shutting down Versatile Server...
═══════════════════════════════════════════════════════════════
[INFO] Stopping tick loop...
[INFO] Saving world data...
[INFO] Unloading plugins...
[INFO] ✓ Versatile Server shutdown complete
```

---

## 📁 Configuration Files

After first run, configuration files are created in the server directory:

```
.
├── versatile.yml                      # Main server config
└── config/
    ├── versatile-global.yml          # Engine performance tuning
    ├── versatile-world-defaults.yml  # World template
    └── plugin-api.yml                # Plugin system config
```

### Edit Configuration

1. **Stop the server** (if running)
2. **Edit YAML files** in the config directory
3. **Restart server** for changes to take effect

Example: Change tick rate in `config/versatile-global.yml`
```yaml
tick-rates:
  main: 10            # Change to 10 TPS (slower ticking)
  redstone: 10
  piston: 10
  block-updates: 10
```

---

## 🔧 Development Workflow

### IDE Setup (IntelliJ IDEA)

1. **File → Open** → Select Versatile project root
2. **Trust Project** when prompted
3. **File → Project Structure → SDK** → Ensure Java 25+ selected
4. **Maven** panel on right → Reload projects
5. Ready to develop!

### Running Tests
```bash
mvn test
```

### Building JAR
```bash
mvn clean package
```

### Running with Debug Logging
```bash
java -jar target/versatile-core.jar
# Set logger to FINE in config:
# logging:
#   level: "FINE"
```

---

## 📋 Common Development Tasks

### Add New Configuration Setting

1. **Edit YAML file** (e.g., `config/versatile-global.yml`)
   ```yaml
   new-section:
     new-setting: "value"
   ```

2. **Create Record class** (e.g., `src/main/java/io/versatile/config/NewSectionConfig.java`)
   ```java
   public record NewSectionConfig(String newSetting) {
       public static NewSectionConfig fromMap(Map<String, Object> map) {
           return new NewSectionConfig(
               (String) map.getOrDefault("new-setting", "default")
           );
       }
   }
   ```

3. **Add to GlobalConfig**
   ```java
   public record GlobalConfig(
       // ... existing fields ...
       NewSectionConfig newSection
   )
   ```

4. **Update ConfigManager.loadGlobalConfiguration()**
   ```java
   Map<String, Object> newMap = (Map<String, Object>) 
       map.getOrDefault("new-section", Map.of());
   NewSectionConfig newConfig = NewSectionConfig.fromMap(newMap);
   ```

---

### Implement Stub Method in ServerBridge

1. **Find TODO method** in `ServerBridge.java`
2. **Replace exception** with actual implementation
3. **Connect to engine system** (e.g., TickLoop, World manager)
4. **Test with mock plugin**

Example:
```java
@Override
public Collection<? extends Player> getOnlinePlayers() {
    // TODO: Implement player lookup from engine
    return Collections.emptyList();
}

// Change to:
@Override
public Collection<? extends Player> getOnlinePlayers() {
    return playerManager.getAllOnlinePlayers();  // Connect to engine
}
```

---

### Add New Tick System

1. **Add tick rate** to `config/versatile-global.yml`
   ```yaml
   tick-rates:
     my-system: 20
   ```

2. **Add to TickRateConfig**
   ```java
   public record TickRateConfig(
       // ... existing ...
       int mySystem
   )
   ```

3. **Add ticker method** to `TickLoop.java`
   ```java
   private void onMySystemTick() {
       mySystemTickCounter++;
       // Implement tick logic here
   }
   ```

4. **Call from performTick()**
   ```java
   if (shouldTickMySystem()) {
       onMySystemTick();
   }
   ```

---

## 🐛 Debugging

### Enable Debug Logging
Edit `config/versatile-global.yml`:
```yaml
logging:
  level: "FINE"           # FINE = debug, INFO = normal, WARNING = errors only
```

### Inspect Configuration
Add this to `VersatileServer.startup()`:
```java
GlobalConfig config = configManager.getGlobalConfig();
System.out.println("Virtual Threads: " + config.performance().useVirtualThreads());
System.out.println("Main TPS: " + config.tickRates().main());
```

### Virtual Thread Debugging
```bash
# Enable Virtual Thread stack traces
java -Djdk.virtualThreadScheduler.parallelism=4 \
     -Djdk.virtualThreadScheduler.maxPoolSize=256 \
     -jar target/versatile-core.jar
```

### Monitor with JFR
```bash
# Start with Java Flight Recorder
java -XX:StartFlightRecording=duration=60s,filename=recording.jfr \
     -jar target/versatile-core.jar

# Analyze with JDK Mission Control
jmc recording.jfr
```

---

## 📚 Key File Locations

| File | Purpose |
|------|---------|
| `VersatileServer.java` | Main entry point - orchestration logic |
| `config/ConfigManager.java` | Configuration loading from YAML |
| `engine/tick/TickLoop.java` | Game tick loop (Virtual Thread-based) |
| `paper/ServerBridge.java` | Paper API implementation stubs |
| `pom.xml` | Maven build configuration |
| `ARCHITECTURE.md` | Detailed architecture documentation |
| `IMPLEMENTATION_SUMMARY.md` | Implementation details & statistics |

---

## 🚀 Performance Tips

1. **Virtual Thread Pool Size**
   - Default: 256 (good for 20 players)
   - Increase for more concurrent players
   - Edit: `config/versatile-global.yml` → `performance.virtual-thread-pool-size`

2. **Tick Rates**
   - Default: 20 TPS (standard Minecraft)
   - Reduce for lower-end machines
   - Edit: `config/versatile-global.yml` → `tick-rates.main`

3. **Entity Culling**
   - Reduce lag from entity updates
   - Default: enabled
   - Edit: `config/versatile-global.yml` → `world-ticking.entity-culling-enabled`

4. **Chunk Radius**
   - Balance between render distance and server load
   - Default: 12 chunks
   - Edit: `config/versatile-global.yml` → `world-ticking.chunk-load-radius`

---

## 🤝 Contributing

### Code Style
- Use **Java 25 idioms** (Records, Virtual Threads)
- **Meaningful variable names**: `virtualThreadPoolSize` not `vtp`
- **Javadoc on public classes/methods**
- **Modern Java patterns**: Avoid null checks, use Optional

### Commit Message Format
```
[Component] Brief description

Longer explanation if needed.

Related to: #123
```

Example:
```
[ConfigManager] Support config reloading

Add support for dynamic config reloading without server restart.
Implements hot-reload for specific config sections.

Related to: #5
```

---

## 🆘 Troubleshooting

| Problem | Solution |
|---------|----------|
| `Java not found` | Install OpenJDK 25+ and add to PATH |
| `mvn not found` | Install Maven 3.9.16+ and add to PATH |
| `Configuration not loading` | Check file permissions, ensure UTF-8 encoding |
| `Tick loop not starting` | Check Virtual Thread availability (Java 25+) |
| `Port already in use` | Change `server.port` in `versatile.yml` |
| `Out of memory` | Increase JVM heap: `java -Xmx2G -jar versatile-core.jar` |

---

## 📖 Documentation Files

- **ARCHITECTURE.md**: Detailed architecture and design decisions
- **IMPLEMENTATION_SUMMARY.md**: Code statistics and file breakdown
- **QUICK_START.md** (this file): Getting started guide
- **README.md** (future): User-facing documentation

---

## ✅ Verification Checklist

After building, verify:

- [ ] No compilation errors: `mvn clean compile`
- [ ] JAR created: `ls -la target/versatile-core.jar`
- [ ] Server starts: `java -jar target/versatile-core.jar`
- [ ] Configs extracted: `ls config/`
- [ ] Shutdown works: `Ctrl+C` completes gracefully

---

## 🎯 What's Next?

1. **Phase 2**: Implement world loading system
2. **Phase 3**: Build plugin loader and event system
3. **Phase 4**: Add network protocol handler (Netty)
4. **Phase 5**: Optimize performance with profiling

---

**Need Help?**
- Check ARCHITECTURE.md for detailed design docs
- Review IMPLEMENTATION_SUMMARY.md for code statistics
- Search existing code for similar implementations
- Check inline TODO comments for planned features

**Happy Developing!** 🚀
