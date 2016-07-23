package com.yoho.gateway.controller.order.cache;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.gateway.utils.constants.CacheKeyConstants;

/**
 * Created by wangshijie on 16/4/21.
 */
@Component
public class UserOrderCache {
	private final static Logger logger = LoggerFactory.getLogger(UserOrderCache.class);
	// 缓存时间
	public final static long EXPIRE_TIME = 60;

	@Resource(name = "yhRedisTemplate")
	private YHRedisTemplate<String, String> redisTemplate;

	@Autowired
	private YHValueOperations<String, String> valueOperations;

	/**
	 * 根据key取缓存
	 * 
	 * @param uid
	 */
	public String getValueFromRedis(String key) {
		logger.info("Redis. get value in userorder. key is {}", key);
		try {
			if (redisTemplate.hasKey(key)) {
				return valueOperations.get(key);
			}
		} catch (Exception e) {
			logger.warn("getValueFromRedis error: key is {}, error message is {}", key, e.getMessage());
		}
		return null;
	}

	/**
	 * 设置缓存。
	 * 
	 * @param uid
	 */
	public void setValueToRedis(String key, String value, long expire, TimeUnit timeUnit) {
		try {
			valueOperations.setIfAbsent(key, value);
			redisTemplate.longExpire(key, expire, timeUnit);
		} catch (Exception e) {
			logger.warn("setValueToRedis error: key is {}, value is {}, expire is {}, error message is {}", key, value, expire, e.getMessage());
		}
	}

	/**
	 * 根据uid清除待支付、待发货、已发货的缓存。
	 * 
	 * @param uid
	 */
	public void clearOrderCountCache(Integer uid) {
		if (uid == null) {
			logger.info("clearOrderCountCache failed because uid is null .");
			return;
		}
		// 待支付数量redis缓存key
		String waitpayNum = CacheKeyConstants.YHGW_WAITPAYNUM_PRE + uid;
		// 待发货数量redis缓存key
		String waitcargoNum = CacheKeyConstants.YHGW_WAITCARGONUM_PRE + uid;
		// 已发货数量redis缓存key
		String sendcargoNum = CacheKeyConstants.YHGW_SENDCARGONUM_PRE + uid;

		clearCache(uid, waitpayNum, waitcargoNum, sendcargoNum);
	}

	/**
	 * 根据uid清除退换货总数的缓存。
	 * 
	 * @param uid
	 */
	public void clearRefundOrderCountCache(Integer uid) {
		if (uid == null) {
			logger.info("clearRefundOrderCountCache failed because uid is null .");
			return;
		}
		String refundExchangeNum = CacheKeyConstants.YHGW_REFUNDEXCHANGENUM_PRE + uid;
		clearCache(uid, refundExchangeNum);
	}

	/**
	 * 根据keys来清除缓存
	 * 
	 * @param key
	 */
	private void clearCache(Integer uid, String... key) {
		if (ArrayUtils.isEmpty(key)) {
			return;
		}
		try {
			redisTemplate.delete(Arrays.asList(key));
			logger.info("ClearCache success for user {} keys {}", uid, Arrays.asList(key));
		} catch (Exception ex) {
			logger.info("ClearCache fail for user {} keys {}", uid, Arrays.asList(key));
		}
	}

}
