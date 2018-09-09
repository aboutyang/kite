package com.aboutyang.config;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.ClassUtils;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.List;
import java.util.concurrent.Callable;

import static com.aboutyang.config.CacheConfig.ALL_CACHE_KEY;

/**
 * @author aboutyang
 */
@Configuration
@EnableAsync
@EnableCaching
public class CacheConfig {

    public static final String ALL_CACHE_KEY = "allCacheKey";

    public static final String PERM_CACHE = "permCache";
    public static final String USER_CACHE = "userCache";
    public static final String TOKEN_CACHE = "tokenCache";
    public static final String DEPT_CACHE = "deptCache";
    public static final String MENU_CACHE = "menuCache";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("permCache", "userCache", "tokenCache", "deptCache", "menuCache"){
            @Override
            protected Cache createConcurrentMapCache(String name) {
                Cache cache = super.createConcurrentMapCache(name);
                return new CacheWrapper(cache);
            }
        };
    }

}

class CacheWrapper implements Cache {

    private Cache cache;

    public CacheWrapper(Cache cache) {
        this.cache = cache;
    }

    @Override
    public String getName() {
        return cache.getName();
    }

    @Override
    public Object getNativeCache() {
        return cache.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {
        return cache.get(String.valueOf(key));
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        return cache.get(String.valueOf(key), type);
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        return cache.get(String.valueOf(key), valueLoader);
    }

    @Override
    public void put(Object key, Object value) {
        String keyStr = String.valueOf(key);
        cacheKey(keyStr);
        cache.put(keyStr, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        String keyStr = String.valueOf(key);
        cacheKey(keyStr);
        return cache.putIfAbsent(keyStr, value);
    }

    @Override
    public void evict(Object key) {
        String keyStr = String.valueOf(key);
        removeCacheKey(keyStr);
        cache.evict(keyStr);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    public void cacheKey(Object key){
        List allCacheKey = cache.get(ALL_CACHE_KEY, List.class);
        if(allCacheKey == null){
            allCacheKey = Lists.newArrayList();
        }
        allCacheKey.add(key);
        cache.put(ALL_CACHE_KEY, allCacheKey);
    }

    public void removeCacheKey(Object key){
        List allCacheKey = cache.get(ALL_CACHE_KEY, List.class);
        if(allCacheKey != null){
            allCacheKey.remove(key);
        }
        cache.put(ALL_CACHE_KEY, allCacheKey);
    }

    public void removeAllCacheKey(){
        cache.evict(ALL_CACHE_KEY);
    }

}