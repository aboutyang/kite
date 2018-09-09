package com.aboutyang.modules.sys.form;

import java.io.Serializable;

/**
 * @author aboutyang
 */
public class CacheForm implements Serializable {

    private String id;
    private String cacheName;
    private String cacheKey;
    private Object cacheValue;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public void setCacheKey(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public Object getCacheValue() {
        return cacheValue;
    }

    public void setCacheValue(Object cacheValue) {
        this.cacheValue = cacheValue;
    }
}
