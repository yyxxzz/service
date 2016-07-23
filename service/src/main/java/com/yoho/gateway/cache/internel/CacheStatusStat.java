package com.yoho.gateway.cache.internel;

import com.yoho.error.event.GatewayCacheEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Component;

/**
 * cache统计. 上报事件
 * Created by chunhua.zhang@yoho.cn on 2015/11/13.
 */
@Component
public class CacheStatusStat implements ApplicationEventPublisherAware {


    private ApplicationEventPublisher publisher;

    /**
     * 报告cache信息
     *
     * @param methodName
     * @param key
     * @param status
     */
    public void report(String methodName, String key, Status status) {
        publisher.publishEvent(new GatewayCacheEvent(methodName, key, status.name(), null));
    }


    /**
     * 报告cache信息
     *
     * @param methodName
     * @param key
     * @param status
     */
    public void report(String methodName, String key, Status status, Exception e) {
        publisher.publishEvent(new GatewayCacheEvent(methodName, key, status.name(), e));
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }


    public enum Status {

        //一级缓存命中
        Level1_Hit,

        //一级缓存异常
        Level1_exception,

        //一级缓存miss，并且调用服务成功
        Level1_miss,

        //二级缓存命中
        Level2_hit,

        //二级缓存miss
        Level2_miss

    }


}
