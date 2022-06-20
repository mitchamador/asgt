package gbas.gtbch.util.cache;

import gbas.gtbch.util.cache.annotation.CacheClear;
import gbas.tvk.util.cache.Cache;
import gbas.tvk.util.cache.CacheStatistics;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Application cache manager
 */
@Aspect
@Component
public class AppCacheManager {

    private static class CacheEntity<K, V> {
        /**
         * {@link Cache}
         */
        private final Cache<K, V> cache;

        /**
         * enable flag for cache
         */
        private boolean enabled = true;

        private CacheEntity(Cache<K, V> cache) {
            this.cache = cache;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public Cache<K, V> getCache() {
            return cache;
        }
    }

    /**
     * {@link gbas.tvk.payment.db.DbPayment} query cache's name
     */
    public final static String CACHE_DBPAYMENT_QUERY = "tp";

    /**
     * caches' map
     */
    private final Map<String, CacheEntity> cacheMap;

    /**
     * global cache enable flag
     */
    private boolean enabled = true;

    /**
     * Get {@link gbas.tvk.payment.db.DbPayment} query cache
     * @return
     */
    public Cache<String, Vector> getDbPaymentQueryCache() {
        return getCache(CACHE_DBPAYMENT_QUERY);
    }

    private Cache getCache(String name) {
        CacheEntity cacheEntity = cacheMap.get(name);
        if (enabled && cacheEntity != null && cacheEntity.isEnabled()) {
            return cacheEntity.getCache();
        }
        return null;
    }

    public AppCacheManager(@Autowired(required = false) Cache<String, Vector> dbPaymentQueryCache) {
        cacheMap = new HashMap<>();
        cacheMap.put(CACHE_DBPAYMENT_QUERY, new CacheEntity(dbPaymentQueryCache));
    }

    /**
     * clear cache
     * @param name cache's name
     */
    private void clearCache(String name) {
        CacheEntity cacheEntity = cacheMap.get(name);
        if (cacheEntity != null && cacheEntity.getCache() != null) {
            cacheEntity.getCache().clear();
        }
    }

    /**
     * Clear TP caches
     */
    public void clearTpCaches() {
        clearCache(CACHE_DBPAYMENT_QUERY);
    }

    /**
     * Clear caches
     * @param caches
     */
    public void clearCaches(String[] caches) {
        if (caches != null && caches.length > 0) {
            for (String cache : caches) {
                switch (cache) {
                    case CACHE_DBPAYMENT_QUERY :
                        clearTpCaches();
                        break;
                }
            }
        } else {
            // clear all caches
            for (CacheEntity cacheEntity : cacheMap.values()) {
                if (cacheEntity != null && cacheEntity.getCache() != null) {
                    cacheEntity.getCache().clear();
                }
            }
        }

    }

    /**
     * Clear caches for method with {@link CacheClear} annotation
     * @param joinPoint
     * @param cacheClear
     * @return
     * @throws Throwable
     */
    @Around("@annotation(cacheClear)")
    public Object clearCaches(ProceedingJoinPoint joinPoint, CacheClear cacheClear) throws Throwable {
        Object result = joinPoint.proceed();

        clearCaches(cacheClear.value());

        return result;
    }

    /**
     * get {@link CacheStatistics} for all available caches
     * @return
     */
    public List<CacheStatistics> getCacheStatistics() {
        List<CacheStatistics> cacheStatisticsList = new ArrayList<>();
        for (CacheEntity cacheEntity : cacheMap.values()) {
            if (cacheEntity != null && cacheEntity.getCache() != null) {
                cacheStatisticsList.add(cacheEntity.getCache().getStats());
            }
        }
        return cacheStatisticsList;
    }
    /**
     * get {@link CacheStatistics} for cache
     * @param name cache's name
     * @return
     */
    public CacheStatistics getCacheStatistics(String name) {
        CacheEntity cacheEntity = cacheMap.get(name);
        if (cacheEntity != null && cacheEntity.getCache() != null) {
            return cacheEntity.getCache().getStats();
        }
        return null;
    }

    /**
     * get global state of cache enabled flag
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * set global state of cache enabled flag
     * @param enabled enabled flag
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * get state of enabled flag for cache
     * @param name cache's name
     * @return
     */
    public boolean isEnabled(String name) {
        CacheEntity cacheEntity = cacheMap.get(name);
        if (cacheEntity != null) {
            return cacheEntity.isEnabled();
        }
        return false;
    }

    /**
     * set state of enabled flag for cache
     * @param name cache's name
     * @param enabled enabled flag
     */
    public void setEnabled(String name, boolean enabled) {
        CacheEntity cacheEntity = cacheMap.get(name);
        if (cacheEntity != null) {
            cacheEntity.setEnabled(enabled);
            if (!enabled) {
                cacheEntity.getCache().clear();
            }
        }
    }
}
