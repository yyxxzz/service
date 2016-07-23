package com.yoho.gateway.cache;

import com.yoho.core.cache.CacheClient;
import com.yoho.core.cache.impl.MemcachedClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Memecache 客户端
 * Created by chunhua.zhang@yoho.cn on 2015/11/27.
 */
@Component
public class MemecacheClientHolder {

    private static final Logger logger = LoggerFactory.getLogger(MemecacheClientHolder.class);


    //一级缓存默认时间：30分钟
    @Value("${cache.servers.gateway.level1.address:null}")
    private String level1CacheAddress[];
    @Value("${cache.servers.gateway.level1.expire:1800}")
    private int level1Expire;


    //二级缓存默认时间：12小时: 12*60*60
    @Value("${cache.servers.gateway.level2.address:null}")
    private String level2CacheAddress[];
    @Value("${cache.servers.gateway.level2.expire:43200}")
    private int level2Expire;


    private CacheClient level1Cache;
    private CacheClient level2Cache;

    @PostConstruct
    public void initCache() {
        try {
            if (level1CacheAddress != null) {
                MemcachedClientFactory clientFactory = new MemcachedClientFactory(level1CacheAddress);
                level1Cache = clientFactory.getObject();

                logger.info("init level1 cache at {} success.", String.join(",", level1CacheAddress));
            } else {
                logger.info("Level1 cache is not configured");
            }
            if (level2CacheAddress != null) {
                MemcachedClientFactory clientFactory = new MemcachedClientFactory(level2CacheAddress);
                level2Cache = clientFactory.getObject();

                logger.info("init level2 cache at {} success.", String.join(",", level2CacheAddress));
            } else {
                logger.info("Level2 cache is not configured");
            }
        } catch (Exception e) {
            logger.warn("can not init any memcache: {}", e.getMessage());
        }

    }


    public CacheClient getLevel2Cache() {
        return level2Cache;
    }

    public CacheClient getLevel1Cache() {
        return level1Cache;
    }

    public int getLevel1Expire() {
        return level1Expire;
    }

    public int getLevel2Expire() {
        return level2Expire;
    }

}
