package com.aboutyang.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * @author aboutyang
 */
@Configuration
@EnableAsync
@EnableCaching
public class CacheConfig {

    public static final String PERM_CACHE = "permCache";
    public static final String USER_CACHE = "userCache";
    public static final String TOKEN_CACHE = "tokenCache";
    public static final String DEPT_CACHE = "deptCache";
    public static final String MENU_CACHE = "menuCache";

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("permCache", "userCache", "tokenCache", "deptCache", "menuCache");
    }

}
