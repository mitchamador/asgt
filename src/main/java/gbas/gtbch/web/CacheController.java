package gbas.gtbch.web;

import gbas.gtbch.util.cache.AppCacheManager;
import gbas.tvk.util.cache.CacheStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/api/cache")
public class CacheController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    private final AppCacheManager cacheManager;

    public CacheController(AppCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    /**
     * get {@link CacheStatistics} for all available caches
     * @return
     */
    @RequestMapping(value = "/stats", method = RequestMethod.GET)
    public ResponseEntity<List<CacheStatistics>> getCacheStatistics() {
        return ResponseEntity.ok(cacheManager.getCacheStatistics());
    }

    /**
     * get {@link CacheStatistics} for cache
     * @param name cache's name
     * @return
     */
    @RequestMapping(value = "/stats/{name}", method = RequestMethod.GET)
    public ResponseEntity<CacheStatistics> getCacheStatistics(@PathVariable String name) {
        return ResponseEntity.ok(cacheManager.getCacheStatistics(name));
    }
}