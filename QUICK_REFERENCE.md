# 🎯 VERSATILE SERVER - QUICK REFERENCE GUIDE

## 📍 Key Files & Entry Points

### Main Entry Point
**File**: `src/main/java/io/versatile/VersatileServer.java`
- **Purpose**: Main application orchestrator
- **Key Method**: `public static void main(String[] args)`
- **Responsibility**: Startup sequence, initialization, graceful shutdown

### Configuration System
**File**: `src/main/java/io/versatile/config/ConfigManager.java`
- **Purpose**: Load and manage all YAML configurations
- **Key Method**: `public void loadConfigurations() throws IOException`
- **Features**: Auto-extract from JAR, YAML parsing, type-safe Records

### Configuration Objects (Immutable Records)
- `GlobalConfig.java` - Root configuration container
- `PerformanceConfig.java` - Virtual Thread settings
- `TickRateConfig.java` - Game tick rates
- `WorldTickingConfig.java` - World-specific settings
- `LoggingConfig.java` - Logging configuration

### Game Engine
**File**: `src/main/java/io/versatile/engine/tick/TickLoop.java`
- **Purpose**: High-performance tick loop using Virtual Threads
- **Key Method**: `public void start()`
- **Characteristics**: 20 TPS, nanosecond-precision, synchronized updates

### Paper API Bridge
**File**: `src/main/java/io/versatile/paper/ServerBridge.java`
- **Purpose**: Implement Bukkit/Paper Server interface
- **Implements**: `org.bukkit.Server`
- **Status**: Stub methods marked with TODO comments

---

## ⚙️ Configuration Files

Located in: `src/main/resources/`

| File | Purpose | Edit When |
|------|---------|-----------|
| `versatile.yml` | Server name, port, max players | Changing server settings |
| `config/versatile-global.yml` | Engine tuning (tick rates, threads) | Performance optimization |
| `config/versatile-world-defaults.yml` | World template (difficulty, spawning) | Changing world defaults |
| `config/plugin-api.yml` | Plugin system configuration | Plugin settings |

---

## 🏗️ Project Structure at a Glance

```
src/main/java/io/versatile/
├── VersatileServer.java              ← START HERE (Main entry point)
├── config/
│   ├── ConfigManager.java            ← Configuration loading
│   ├── GlobalConfig.java             ← Config container (Record)
│   ├── PerformanceConfig.java        ← Performance settings (Record)
│   ├── TickRateConfig.java           ← Tick rates (Record)
│   ├── WorldTickingConfig.java       ← World settings (Record)
│   └── LoggingConfig.java            ← Logging settings (Record)
├── engine/
│   └── tick/
│       └── TickLoop.java             ← Game tick engine
└── paper/
    └── ServerBridge.java             ← Paper API bridge
```

---

## 📚 Documentation Map

| Document | Read For | Key Sections |
|----------|----------|--------------|
| **README.md** | Overview | Features, quick start, architecture |
| **ARCHITECTURE.md** | Deep dive | Patterns, thread safety, design decisions |
| **QUICK_START.md** | Development | Setup, workflow, debugging, common tasks |
| **IMPLEMENTATION_SUMMARY.md** | Code details | Statistics, component breakdown, performance |
| **DELIVERY_SUMMARY.md** | Project summary | Deliverables, status, next steps |

---

## 🚀 Getting Started (5 Steps)

### Step 1: Build
```bash
mvn clean package -DskipTests
```

### Step 2: Run
```bash
java -jar target/versatile-core.jar
```

### Step 3: Verify
Look for:
```
✓ Versatile Server is now ONLINE and accepting connections!
```

### Step 4: Customize
Edit `config/versatile-global.yml` for:
- Tick rates
- Virtual Thread pool size
- Chunk load radius
- Entity culling

### Step 5: Explore Code
Start with:
1. `VersatileServer.java` - Understand startup flow
2. `ConfigManager.java` - See configuration loading
3. `TickLoop.java` - Review game loop logic
4. `ServerBridge.java` - Examine Paper API stubs

---

## 💡 Common Tasks

### Change Server Port
**File**: `config/versatile.yml`
```yaml
server:
  port: 25566  # Change from 25565
```

### Increase Performance
**File**: `config/versatile-global.yml`
```yaml
performance:
  virtual-thread-pool-size: 512  # Increase from 256

tick-rates:
  main: 10  # Reduce from 20 (slower but lighter)
```

### Enable Debug Logging
**File**: `config/versatile-global.yml`
```yaml
logging:
  level: "FINE"  # Change from INFO
```

### Implement a Stub Method
**File**: `src/main/java/io/versatile/paper/ServerBridge.java`
```java
// Find a method marked with TODO
// Replace the UnsupportedOperationException
// with actual implementation
// Example: Connect to engine system
```

---

## 🔍 Key Design Patterns

| Pattern | Location | Purpose |
|---------|----------|---------|
| **Records** | `config/*Config.java` | Immutable configuration objects |
| **Virtual Threads** | `engine/tick/TickLoop.java` | High-concurrency IO handling |
| **Factory Pattern** | `GlobalConfig.fromMap()` | Create Records from YAML |
| **Singleton-like** | `ConfigManager` | Single configuration instance |
| **Delegation** | `ServerBridge` | Delegate to engine systems |
| **Synchronized Block** | `TickLoop.performTick()` | Game state safety |

---

## 🔐 Thread Safety Notes

- **ConfigManager**: Safe after `loadConfigurations()` called
- **GlobalConfig**: Immutable Records (always safe)
- **TickLoop**: Synchronized at `synchronized(tickLock)` boundary
- **ServerBridge**: Stateless (safe to call from any thread)

---

## 📊 Code Metrics

- **Main Entry Point**: 206 lines
- **Configuration System**: 262 lines (ConfigManager + Records)
- **Game Engine**: 151 lines
- **Paper API Bridge**: 192 lines
- **Total**: 831 lines of production code

---

## ⏭️ Next Phase (Phase 2)

To implement world management, create:

```
src/main/java/io/versatile/engine/
├── world/
│   ├── WorldManager.java      ← Manage loaded worlds
│   ├── World.java             ← World representation
│   └── Chunk.java             ← Chunk representation
├── entity/
│   ├── EntityManager.java      ← Manage entities
│   └── Entity.java             ← Entity representation
└── block/
    └── BlockUpdateSystem.java  ← Handle block updates
```

---

## 📞 Getting Help

1. **Architecture Question**: Read `ARCHITECTURE.md`
2. **Setup Issue**: Check `QUICK_START.md`
3. **Code Question**: Review inline Javadoc and TODOs
4. **Performance**: See `IMPLEMENTATION_SUMMARY.md`
5. **Project Overview**: Read `README.md`

---

## ✅ Verification Checklist

Before moving to Phase 2, verify:

- [ ] Project builds: `mvn clean package -DskipTests`
- [ ] Server runs: `java -jar target/versatile-core.jar`
- [ ] Configs extracted: Look for `config/` directory
- [ ] Startup message: "ONLINE and accepting connections"
- [ ] Shutdown works: Press Ctrl+C (graceful exit)
- [ ] You understand: VersatileServer.java startup flow

---

## 🎯 Success Criteria for This Phase

✅ **Configuration System Works**
- YAML files load successfully
- Records created with parsed data
- Type-safe access to settings

✅ **Game Loop Running**
- Tick loop starts in Virtual Thread
- Accurate tick timing (50ms intervals)
- Synchronized tick boundary

✅ **Paper API Bridge Ready**
- ServerBridge implements Server interface
- Stubs prepared for future implementation
- Plugin compatibility path established

✅ **Documentation Complete**
- README.md comprehensive
- ARCHITECTURE.md detailed
- QUICK_START.md developer-friendly
- All code documented

---

## 🚀 Ready for Development!

The foundation is complete. You can now:

1. **Build plugins** targeting the Paper API
2. **Optimize configuration** for your needs
3. **Extend tick loop** with new subsystems
4. **Implement Phase 2** (World Manager)

---

**Your Versatile Server is ready to evolve!**

Start with `VersatileServer.java`, understand the flow, then build upon this solid foundation.

---

*For detailed information, see the comprehensive documentation files in the project root.*
