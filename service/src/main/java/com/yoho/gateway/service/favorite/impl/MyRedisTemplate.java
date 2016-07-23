package com.yoho.gateway.service.favorite.impl;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

/**
 *  spring redis 设置超长时间expire，会调用twemproxy不支持的time命令，导致报错。 
 *
 * Created by chunhua.zhang@yoho.cn on 2016/1/30.
 */
public class MyRedisTemplate<K,V> extends  RedisTemplate<K, V> {


    public Boolean longExpire(K key, final long timeout, final TimeUnit unit) {

        try {
            final byte[] rawKey = String.valueOf(key).getBytes("UTF-8");

            return (Boolean) this.execute(new RedisCallback() {
                public Boolean doInRedis(RedisConnection connection) {
                    return connection.expire(rawKey, TimeoutUtils.toSeconds(timeout, unit));
                }
            }, true);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }
}


