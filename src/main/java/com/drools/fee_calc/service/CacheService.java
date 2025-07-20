package com.drools.fee_calc.service;

public interface CacheService {
    Boolean put(String key, Object value);

    Object get(String key);
}
