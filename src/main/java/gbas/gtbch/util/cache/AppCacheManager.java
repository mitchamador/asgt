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

    /**
     * {@link gbas.tvk.payment.db.DbPayment} query cache's name
     */
    public final static String CACHE_DBPAYMENT_QUERY = "tp";

    private final Map<String, Cache> cacheMap;

    /**
     * Get {@link gbas.tvk.payment.db.DbPayment} query cache
     * @return
     */
    public Cache<String, Vector> getDbPaymentQueryCache() {
        return (Cache<String, Vector>) cacheMap.get(CACHE_DBPAYMENT_QUERY);
    }

    public AppCacheManager(@Autowired(required = false) Cache<String, Vector> dbPaymentQueryCache) {
        cacheMap = new HashMap<>();
        cacheMap.put(CACHE_DBPAYMENT_QUERY, dbPaymentQueryCache);
    }

    /**
     * Clear TP caches
     */
    public void clearTpCaches() {
        if (cacheMap.get(CACHE_DBPAYMENT_QUERY) != null) {
            cacheMap.get(CACHE_DBPAYMENT_QUERY).clear();
        }
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
            clearTpCaches();
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
        for (String name : cacheMap.keySet()) {
            cacheStatisticsList.add(getCacheStatistics(name));
        }
        return cacheStatisticsList;
    }
    /**
     * get {@link CacheStatistics} for cache
     * @param name cache's name
     * @return
     */
    public CacheStatistics getCacheStatistics(String name) {
        if (cacheMap.get(name) != null) {
            return cacheMap.get(name).getStats();
        }
        return null;
    }
}
