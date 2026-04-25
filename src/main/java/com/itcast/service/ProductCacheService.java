package com.itcast.service;

import com.itcast.entity.Product;
import com.itcast.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductCacheService {

    private final ProductMapper productMapper;

    @Cacheable(cacheNames = "productCache", key = "'allProducts'")
    public List<Product> getProductsWithSpringCache() {
        log.info("SpringCache miss, query product list from database");
        return productMapper.listAll();
    }

    @CacheEvict(cacheNames = "productCache", key = "'allProducts'")
    public void clearSpringCache() {
        log.info("SpringCache evicted: productCache::allProducts");
    }
}
