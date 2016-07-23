package com.yoho.gateway.redis;

import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by caoyan.
 */

public class BigDataValueOperations<K,V> implements ValueOperations<K, V> {

    @Resource(name="bigDataRedisTemplate")
    private ValueOperations<K, V> valueOperations;

    @Override
    public void set(K key, V value) {
        valueOperations.set(key,value);
    }

    /**
     * Set {@code key} to hold the string {@code value} until {@code timeout}.
     *
     * @param key
     * @param value
     * @param timeout
     * @param unit
     */
    @Override
    public void set(K key, V value, long timeout, TimeUnit unit) {
        valueOperations.set(key, value,  timeout,  unit);
    }

    @Override
    public Boolean setIfAbsent(K key, V value) {
        return valueOperations.setIfAbsent(key,value);
    }

    @Override
    public void multiSet(Map<? extends K, ? extends V> m) {
        valueOperations.multiSet(m);
    }

    @Override
    public Boolean multiSetIfAbsent(Map<? extends K, ? extends V> m) {
        return valueOperations.multiSetIfAbsent(m);
    }

    @Override
    public V get(Object key) {
        return valueOperations.get(key);
    }

    @Override
    public V getAndSet(K key, V value) {
        return valueOperations.getAndSet(key,value);
    }

    @Override
    public List<V> multiGet(Collection<K> keys) {
        return valueOperations.multiGet(keys);
    }

    @Override
    public Long increment(K key, long delta) {
        return valueOperations.increment(key,delta);
    }

    @Override
    public Double increment(K key, double delta) {
        return valueOperations.increment(key, delta);
    }

    @Override
    public Integer append(K key, String value) {
        return null;
    }

    @Override
    public String get(K key, long start, long end) {
        return null;
    }

    @Override
    public void set(K key, V value, long offset) {
        valueOperations.set(key,  value,  offset);
    }

    @Override
    public Long size(K key) {
        return valueOperations.size(key);
    }

    /**
     *
     * @return
     */
    @Override
    public RedisOperations<K, V> getOperations() {
        return null;
    }

    /**
     * @param key
     * @param offset
     * @param value
     * @return
     * @since 1.5
     */
    @Override
    public Boolean setBit(K key, long offset, boolean value) {
        return null;
    }

    /**
     * @param key
     * @param offset
     * @return
     * @since 1.5
     */
    @Override
    public Boolean getBit(K key, long offset) {
        return null;
    }
}
