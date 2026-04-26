package com.itcast.utils;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.itcast.constant.RedisConstants.*;

/**
 * @Description CacheClient
 * @Author kight-tom
 * @Date 2026-04-26  23:22
 */
@Component
@Slf4j
public class CacheClient {

    private StringRedisTemplate stringRedisTemplate;

    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 设置缓存
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void set(String key , Object  value , Long time , TimeUnit unit){
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }

    /**
     * 设置逻辑过期
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void setWihLogicalExpire(String key , Object  value , Long time , TimeUnit unit){
        // 设置逻辑过期
        RedisData redisData = new RedisData();
        redisData.setData(value);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        // 写入Redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }

    /**
     * 缓存空值解决缓存穿透
     * @param keyPrefix
     * @param id
     * @param type
     * @param dbFallback
     * @param time
     * @param unit
     * @param <R>
     * @param <ID>
     * @return
     */
    public <R, ID> R queryWithPassThrough(String keyPrefix ,ID id , Class<R> type , Function<ID, R> dbFallback , Long time , TimeUnit unit){

        String key = keyPrefix + id;
        String json = stringRedisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(json)){
            return JSONUtil.toBean(json, type);
        }

        if(json != null){
            return null;
        }

        R r = dbFallback.apply(id);

        if (r == null){
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }

        this.set(key, r, time, unit);

        return r;
    }

    /**
     * 逻辑过期解决缓存击穿
     * @param keyPrefix
     * @param id
     * @param type
     * @param dbFallback
     * @param time
     * @param unit
     * @param
     * @param <ID>
     * @return
     */
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    public <R , ID> R queryWithLogicalExpire(String keyPrefix ,ID id , Class<R> type , Function<ID, R> dbFallback , Long time , TimeUnit unit){
        String key = CACHE_SHOP_KEY + id;
        //1.从redis中查询
        String json = stringRedisTemplate.opsForValue().get(key);
        //2.判断是否存在
        if (StrUtil.isBlank(json)){
            return null;
        }
        //3.命中，先把json反序列化为对象
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        R r = JSONUtil.toBean((JSONObject) redisData.getData(), type);
        LocalDateTime expireTime = redisData.getExpireTime();

        //4.判断是否过期
        if(expireTime.isAfter(LocalDateTime.now())){
            //5.未过期，返回店铺信息
            return r;
        }

        //6.已过期，需要缓存重建
        //7.获取互斥锁
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = tryLock(lockKey);

        //8.判断是否获取锁成功
        if(isLock){
            //9.成功，开启独立线程，实现缓存重建
            //submit是线程池的方法，submit是异步执行，返回的是一个future对象
            CACHE_REBUILD_EXECUTOR.submit(() -> {
                try{
                    //重建缓存
                    //1.查询数据库
                    R r1 = dbFallback.apply(id);
                    //2.写入Redis
                    this.setWihLogicalExpire(key, r1, time, unit);
                }catch (Exception e){
                    throw new RuntimeException(e);
                }finally {
                    unLock(lockKey);
                }
            });
        }
        //10.返回过期的店铺信息
        return r;
    }

    /**
     * 尝试获取锁
     * @param key
     * @return
     */
    private boolean tryLock(String key){
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1" , LOCK_SHOP_TTL, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }

    /**
     * 释放锁
     * @param key
     */
    private void unLock(String key){
        stringRedisTemplate.delete(key);
    }

}
