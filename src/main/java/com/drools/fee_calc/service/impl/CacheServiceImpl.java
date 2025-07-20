package com.drools.fee_calc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.drools.fee_calc.service.CacheService;

import jakarta.annotation.PostConstruct;

@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    private CacheManager cacheManager;

    private Cache cache;

    @PostConstruct
    public void init() {
        this.cache = cacheManager.getCache("cacheData");
        System.out.println("Initialized cache: " + cache);
    }

    @Override
    public Boolean put(String key, Object value) {
        System.out.println("Putting value in cache with key: " + key);
        if (cache != null) {
            System.out.println("Cache is not null, proceeding to put value.");
            cache.put(key, value);
            return true;
        }
        System.out.println("Cache is null, cannot put value.");
        return false;
    }

    @Override
    public Object get(String key) {
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get(key);
            if (wrapper != null) {
                Object value = wrapper.get();
                System.out.println("Cache HIT with key: " + key + ", value: " + value);
                return value;
            } else {
                System.out.println("Cache MISS with key: " + key);
            }
        }
        return null;
    }

}
