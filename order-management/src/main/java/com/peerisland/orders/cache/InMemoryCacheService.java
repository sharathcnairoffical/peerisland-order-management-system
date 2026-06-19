package com.peerisland.orders.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

// In-memory Cache

@Slf4j
@Component
public class InMemoryCacheService {

    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public void put(String key, Object value, long ttl, TimeUnit timeUnit) {
        long expiresAt = Instant.now().toEpochMilli() + timeUnit.toMillis(ttl);
        cache.put(key, new CacheEntry(value, expiresAt));
    }

    public <T> Optional<T> get(String key, Class<T> type) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return Optional.empty();
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return Optional.empty();
        }
        if (!type.isInstance(entry.value())) {
            log.warn("Cache entry type mismatch | key: {} | expectedType: {}", key, type.getSimpleName());
            cache.remove(key);
            return Optional.empty();
        }
        return Optional.of(type.cast(entry.value()));
    }

    public boolean contains(String key) {
        return get(key, Object.class).isPresent();
    }

    public void evict(String key) {
        cache.remove(key);
    }

    @Scheduled(fixedDelayString = "${app.cache.cleanup-interval-ms:600000}")
    public void evictExpiredEntries() {
        cache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }

    private record CacheEntry(Object value, long expiresAt) {
        private boolean isExpired() {
            return Instant.now().toEpochMilli() > expiresAt;
        }
    }
}
