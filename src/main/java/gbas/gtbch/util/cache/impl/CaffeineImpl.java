package gbas.gtbch.util.cache.impl;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import gbas.tvk.util.cache.Cache;
import gbas.tvk.util.cache.CacheElement;
import gbas.tvk.util.cache.CacheStatistics;

import java.util.Optional;

/**
 * Caffeine implementation of {@link Cache}
 * @param <K>
 * @param <V>
 */
public class CaffeineImpl<K, V> implements Cache<K, V> {

    private final com.github.benmanes.caffeine.cache.Cache<K, Optional<V>> caffeineCache;
    private final String name;

    public CaffeineImpl(String name, com.github.benmanes.caffeine.cache.Cache<K, Optional<V>> caffeineCache) {
        this.name = name;
        this.caffeineCache = caffeineCache;
    }

    @Override
    public boolean put(K key, V value) {
        caffeineCache.put(key, Optional.ofNullable(value));
        return true;
    }

    @Override
    public V get(K key) {
        Optional<V> value = caffeineCache.getIfPresent(key);
        if (value != null) {
            return value.orElse(null);
        }
        return null;
    }

    @Override
    public boolean contains(K key) {
        return caffeineCache.getIfPresent(key) != null;
    }

    @Override
    public boolean putCacheElement(CacheElement<K, V> cacheElement) {
        if (cacheElement != null) {
            caffeineCache.put(cacheElement.getKey(), Optional.ofNullable(cacheElement.getValue()));
        }
        return false;
    }

    @Override
    public CacheElement<K, V> getCacheElement(K key) {
        Optional<V> value = caffeineCache.getIfPresent(key);
        if (value != null) {
            return new CacheElement<>(key, value.orElse(null));
        }
        return null;
    }

    @Override
    public int size() {
        return (int) caffeineCache.estimatedSize();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void clear() {
        caffeineCache.invalidateAll();
    }

    @Override
    public CacheStatistics getStats() {
        CacheStats cacheStats = caffeineCache.stats();

        CacheStatistics cacheStatistics = new CacheStatistics(name);
        cacheStatistics.setRequestCount(cacheStats.requestCount());
        cacheStatistics.setHitCount(cacheStats.hitCount());
        cacheStatistics.setMissCount(cacheStats.missCount());
        cacheStatistics.setEvictedCount(cacheStats.evictionCount());
        cacheStatistics.setSize(caffeineCache.estimatedSize());

        return cacheStatistics;
    }
}
