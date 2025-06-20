
# 🐟 WhiskCache

A pure Java, zero-dependency, in-memory cache library built from scratch — designed for learning and extensibility.

> ✅ Features TTL expiration, pluggable eviction strategies, persistent warm booting, and full test coverage.

---

## 🎯 Goals

- Understand how real cache systems like Redis or Caffeine work internally
- Build a modular, extensible, and performant cache engine in Java
- Learn core software design principles: Strategy, Configuration, Serialization, Concurrency

---

## 🚀 Features

- ✅ In-memory key-value store
- ✅ Per-entry TTL expiration (with background cleanup thread)
- ✅ Pluggable eviction policies: **LRU**, **LFU**, **FIFO**, **Random**, **NoOp**
- ✅ Graceful shutdown + warm boot via **file-based persistence**
- ✅ Thread-safe (`ConcurrentHashMap`)
- ✅ Lightweight configuration system (`CacheConfig`)
- ✅ API utility methods: `putIfAbsent`, `getOrDefault`, `containsKey`, etc.
- ✅ Cache statistics: **hits**, **misses**, **evictions**
- ✅ 100% pure Java — no external libraries

---

## 📦 Usage

### ✅ 1. Create a Cache Config

```java
CacheConfig<String, String> config = new CacheConfig<>(
    100, // max size
    5,   // cleanup interval
    TimeUnit.SECONDS,
    30000, // default TTL in ms
    new FilePersistenceProvider<>("cache-store.bin") // optional persistence
);
```

### ✅ 2. Choose an Eviction Policy

```java
EvictionPolicy<String> policy = new LFUEvictionPolicy<>();
```

### ✅ 3. Instantiate the Cache

```java
InMemoryCache<String, String> cache = new InMemoryCache<>(config, policy);
```

### ✅ 4. Use the Cache

```java
cache.put("session:user1", "abc123", 10000); // with TTL
cache.putIfAbsent("static:key", "value");
String val = cache.getOrDefault("session:user1", "default");
boolean exists = cache.containsKey("session:user1");
cache.evict("session:user1");
```

### ✅ 5. Shutdown Gracefully

```java
cache.shutdown(); // saves cache to disk if persistence is enabled
```

---

## 🧪 Testing

This project uses **JUnit 5** with full test coverage via **JaCoCo**.

```bash
# Run all tests
mvn clean verify

# View coverage report
open target/site/jacoco/index.html
```

> Includes unit tests for: eviction policies, TTL expiration, persistence, concurrency stress, and utility methods.

---

## 📚 Educational Highlights

- Strategy pattern: plug-and-play eviction
- Thread-safe design with `ConcurrentHashMap`
- Background scheduling with daemon thread
- Serializable persistence with warm boot
- TTL-based logic and cleanup
- Clean separation of concerns (`Cache`, `EvictionPolicy`, `PersistenceProvider`)

---

## 🛠 TODO (Advanced Learning Ideas)

- [ ] Add `computeIfAbsent()` loading cache
- [ ] Expose TCP/HTTP interface (like a mini Redis)
- [ ] Add Prometheus metrics exporter
- [ ] Implement custom serialization layer (e.g., binary)
- [ ] Add clustered cache nodes + replication

---

## 📖 License

MIT — use this for learning, modify as you like. Contributions welcome!

---

## 🙌 Credits

This project was built purely for **learning and exploration** — to understand what makes systems like Redis, Guava Cache, and Caffeine tick from the inside out.
