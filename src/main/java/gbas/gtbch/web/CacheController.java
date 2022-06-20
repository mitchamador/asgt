package gbas.gtbch.web;

import gbas.gtbch.util.cache.AppCacheManager;
import gbas.gtbch.web.response.Response;
import gbas.gtbch.web.response.ResponseValue;
import gbas.tvk.util.cache.CacheStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * clear all available caches
     * @return
     */
    @RequestMapping(value = "/clear", method = RequestMethod.GET)
    public ResponseEntity<Response> clearCache() {
        cacheManager.clearCaches(null);
        return ResponseEntity.ok(new Response("clear all caches"));
    }

    /**
     * clear cache
     * @param name cache's name
     * @return
     */
    @RequestMapping(value = "/clear/{name}", method = RequestMethod.GET)
    public ResponseEntity<Response> clearCache(@PathVariable String name) {
        cacheManager.clearCaches(new String[] {name});
        return ResponseEntity.ok(new Response(String.format("clear cache '%s'", name)));
    }

    /**
     * get/set global state of cache
     * @return
     */
    @RequestMapping(value = "/state", method = RequestMethod.GET)
    public ResponseEntity<ResponseValue<Boolean>> globalState(@RequestParam(required = false) Boolean enabled) {
        if (enabled == null) {
            return ResponseEntity.ok(new ResponseValue<>(cacheManager.isEnabled(), "get state for global cache"));
        }
        cacheManager.setEnabled(enabled);
        return ResponseEntity.ok(new ResponseValue<>(enabled, "set state for global cache"));
    }

    /**
     * get/set state of concrete cache
     * @param name cache's name
     * @return
     */
    @RequestMapping(value = "/state/{name}", method = RequestMethod.GET)
    public ResponseEntity<ResponseValue<Boolean>> cacheState(@PathVariable String name, @RequestParam(required = false) Boolean enabled) {
        if (enabled == null) {
            return ResponseEntity.ok(new ResponseValue<>(cacheManager.isEnabled(name), String.format("get state for cache '%s'", name)));
        }
        cacheManager.setEnabled(name, enabled);
        return ResponseEntity.ok(new ResponseValue<>(enabled, String.format("set state for cache '%s'", name)));
    }



}