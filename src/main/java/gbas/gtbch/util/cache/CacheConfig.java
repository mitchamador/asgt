package gbas.gtbch.util.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import gbas.gtbch.util.cache.impl.CaffeineImpl;
import gbas.tvk.util.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import static gbas.gtbch.util.cache.AppCacheManager.CACHE_DBPAYMENT_QUERY;

@Configuration
public class CacheConfig {

    /**
     * query cache for {@link gbas.tvk.payment.db.DbPayment} based on caffeine
     * @return
     */
    @Bean
    public Cache<String, Vector> dbPaymentQueryCache() {
        com.github.benmanes.caffeine.cache.Cache<String, Optional<Vector>> cache = Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .initialCapacity(128)
                .maximumSize(2048)
                .recordStats()
                .build();
        return new CaffeineImpl<>(CACHE_DBPAYMENT_QUERY, cache);
    }
}
