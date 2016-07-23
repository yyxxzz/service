package com.yoho.gateway.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yoho.gateway.redis.RedisListCache;
import com.yoho.gateway.redis.RedisValueCache;



/**
 * Redis缓存操作
 *
 *@author  Created by xinfei on 16/3/13.
 */
@Component
public class CacheFactory {

    @Autowired
    private RedisListCache redisListCache;

    @Autowired
    private RedisValueCache redisValueCache;


    /**
     * 获取Value类型的REDIS操作权限
     *
     * @return ValueOperations
     */
    public RedisValueCache getRedisValueCache(){
        return redisValueCache;
    }

    /**
     * 获取List结构的REDIS操作权限
     *
     * @return ListOperations
     */
    public RedisListCache getRedisListCache(){
        return redisListCache;
    }

}
