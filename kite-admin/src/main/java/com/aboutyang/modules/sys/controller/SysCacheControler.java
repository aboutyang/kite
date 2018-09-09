package com.aboutyang.modules.sys.controller;

import com.aboutyang.common.utils.PageUtils;
import com.aboutyang.common.utils.R;
import com.aboutyang.config.CacheConfig;
import com.aboutyang.modules.sys.entity.SysConfigEntity;
import com.aboutyang.modules.sys.form.CacheForm;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author aboutyang
 */
@RestController
@RequestMapping("/sys/cache")
public class SysCacheControler {

    @Autowired
    private CacheManager cacheManager;

    /**
     * 所有配置列表
     */
    @RequestMapping("/cacheNames")
    public R cacheNames(@RequestParam Map<String, Object> params) {
        return R.ok().put("cacheNames", cacheManager.getCacheNames());
    }

    @RequestMapping("/{cacheName}")
    public R cacheKeys(@PathVariable("cacheName") String cacheName) {
        List allCacheKeys = cacheManager.getCache(cacheName).get(CacheConfig.ALL_CACHE_KEY, List.class);
        return R.ok().put("allCacheKeys", allCacheKeys);
    }

    @RequestMapping("/list")
    public R list(@RequestParam("cacheName") String cacheName, @RequestParam(value = "cacheKey", required = false) String cacheKey) {
        List<String> allCacheKeys = cacheManager.getCache(cacheName).get(CacheConfig.ALL_CACHE_KEY, List.class);
        if(allCacheKeys == null){
            return R.ok().put("cacheList", Lists.newArrayList());
        }
        List<CacheForm> cacheList = allCacheKeys.stream().filter(ck -> {
            if (StringUtils.isBlank(cacheKey)) {
                return true;
            } else {
                return ck.contains(cacheKey);
            }
        }).limit(50).map(key -> {
            CacheForm cacheForm = new CacheForm();
            cacheForm.setCacheName(cacheName);
            cacheForm.setCacheKey(String.valueOf(key));
            cacheForm.setId(cacheName + "_" + cacheForm.getCacheKey());
            return cacheForm;
        }).collect(Collectors.toList());
        return R.ok().put("cacheList", cacheList);
    }

    @RequestMapping("/{cacheName}/{cacheKey}")
    public R info(@PathVariable("cacheName") String cacheName, @PathVariable("cacheKey") String cacheKey) {
        CacheForm cacheForm = new CacheForm();
        cacheForm.setCacheName(cacheName);
        cacheForm.setCacheKey(cacheKey);
        cacheForm.setId(cacheName + "_" + cacheForm.getCacheKey());
        cacheForm.setCacheValue(cacheManager.getCache(cacheName).get(cacheKey).get());
        return R.ok().put("cacheInfo", cacheForm);
    }

    @RequestMapping("/delete/{cacheName}")
    public R deleteCache(@PathVariable("cacheName") String cacheName) {
        cacheManager.getCache(cacheName).clear();
        return R.ok();
    }

    @RequestMapping("/delete/{cacheName}/{cacheKey}")
    public R deleteCacheKey(@PathVariable("cacheName") String cacheName, @PathVariable("cacheKey") String cacheKey) {
        cacheManager.getCache(cacheName).evict(cacheKey);
        return R.ok();
    }

}
