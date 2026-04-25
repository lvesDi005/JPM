package com.itcast.controller;

import com.alibaba.fastjson.JSON;
import com.itcast.entity.Product;
import com.itcast.mapper.ProductMapper;
import com.itcast.service.ProductCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin/redis")
@Slf4j
public class RedisController {

    private static final String PRODUCT_NATIVE_KEY = "cache:product:list:native";

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductCacheService productCacheService;
    @Autowired
    private CacheManager cacheManager;

    @PostMapping("/set")
    public Map<String, Object> set(@RequestBody Map<String, String> params) {
        String key = params.get("key");
        String value = params.get("value");
        redisTemplate.opsForValue().set(key, value, 24, TimeUnit.HOURS);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("key", key);
        result.put("value", value);
        return result;
    }

    @GetMapping("/get/{key}")
    public Map<String, Object> get(@PathVariable String key) {
        Object value = redisTemplate.opsForValue().get(key);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("key", key);
        result.put("value", value);
        return result;
    }

    @DeleteMapping("/delete/{key}")
    public Map<String, Object> delete(@PathVariable String key) {
        redisTemplate.delete(key);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("key", key);
        result.put("value", "已删除");
        return result;
    }

    @GetMapping("/product/native")
    public Map<String, Object> productNativeCache() {
        String cacheValue = (String) redisTemplate.opsForValue().get(PRODUCT_NATIVE_KEY);
        List<Product> products;
        String source;

        if (cacheValue != null && !cacheValue.isBlank()) {
            products = JSON.parseArray(cacheValue, Product.class);
            source = "redis-native-cache";
        } else {
            products = productMapper.listAll();
            redisTemplate.opsForValue().set(PRODUCT_NATIVE_KEY, JSON.toJSONString(products), 30, TimeUnit.MINUTES);
            source = "database";
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "Redis 缓存");
        result.put("source", source);
        result.put("count", products.size());
        result.put("data", products);
        return result;
    }

    @DeleteMapping("/product/native/clear")
    public Map<String, Object> clearProductNativeCache() {
        redisTemplate.delete(PRODUCT_NATIVE_KEY);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "Redis 缓存");
        result.put("message", "Redis 原生商品缓存已清理");
        return result;
    }

    @GetMapping("/product/springcache")
    public Map<String, Object> productSpringCache() {
        Cache cache = cacheManager.getCache("productCache");
        Cache.ValueWrapper wrapper = cache == null ? null : cache.get("allProducts");
        boolean cacheHitBeforeCall = wrapper != null;

        List<Product> products = productCacheService.getProductsWithSpringCache();
        if (products == null) {
            products = Collections.emptyList();
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "SpringCache缓存");
        result.put("source", cacheHitBeforeCall ? "spring-cache" : "database");
        result.put("count", products.size());
        result.put("data", products);
        return result;
    }

    @DeleteMapping("/product/springcache/clear")
    public Map<String, Object> clearProductSpringCache() {
        productCacheService.clearSpringCache();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("type", "SpringCache缓存");
        result.put("message", "SpringCache 商品缓存已清理");
        return result;
    }
}
