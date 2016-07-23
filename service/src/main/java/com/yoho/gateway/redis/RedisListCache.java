package com.yoho.gateway.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yoho.core.redis.YHListOperations;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.gateway.helper.CacheKeyEnum;
import com.yoho.gateway.helper.CacheKeyHelper;

import javax.annotation.Resource;


/**
 * List类型的redis操作
 *
 * @author Created by dengxinfei on 16/3/13.
 */
@Component
public class RedisListCache {

	static Logger logger = LoggerFactory.getLogger(RedisListCache.class);

	@Resource(name = "yhRedisTemplate")
	YHRedisTemplate<String, String> yHRedisTemplate;

	@Resource(name = "yhListOperations")
	YHListOperations<String, String> yhListOperations;

	/**
	 * redis range 操作
	 * 
	 * @param cacheEnum
	 *            缓存开关的枚举
	 * @param obj
	 *            缓存key的后缀
	 * @param clazz
	 *            对象类型
	 * @param start
	 *            开始索引
	 * @param end
	 *            结束索引
	 * @return
	 */
	public <T> List<T> range(CacheKeyEnum cacheEnum, Object obj, Class<T> clazz, long start, long end) {
		logger.debug("Enter range redis list. cacheEnum is {}, obj is {}, clazz is {}", cacheEnum, obj, clazz);
		String cacheKey = CacheKeyHelper.getCacheKeyAndCheck(cacheEnum, obj);
		// 如果获取的key为空，则说明缓存开关是关闭的
		if (StringUtils.isEmpty(cacheKey)) {
			return null;
		}
		try {
			List<String> strList = yhListOperations.range(cacheKey, start, end);
			logger.info("range redis value. value size {}", strList == null ? 0 : strList.size());
			if (CollectionUtils.isEmpty(strList)) {
				return null;
			}
			// 遍历，组装对象
			List<T> list = new ArrayList<T>();
			for (String str : strList) {
				list.add(CacheKeyHelper.string2Value(str, clazz));
			}
			return list;
		} catch (Exception e) {
			logger.warn("get redis value operation failed. key is {}", cacheKey, e);
		}
		return null;
	}

	/**
	 * redis range 操作
	 * 
	 * @param cacheEnum
	 *            缓存开关的枚举
	 * @param obj
	 *            缓存key的后缀
	 * @param clazz
	 *            对象类型
	 * @param start
	 *            开始索引
	 * @param end
	 *            结束索引
	 * @return
	 */
	public <T> void rightPushAll(CacheKeyEnum cacheEnum, Object obj, Collection<T> values, long timeout, TimeUnit unit) {
		logger.debug("Enter rightPushAll redis list. cacheEnum is {}, obj is {}, value is {}, timeout is {}, unit is {}", cacheEnum, obj, values, timeout, unit);
		// 如果是空列表，直接返回
		if (CollectionUtils.isEmpty(values)) {
			return;
		}

		String cacheKey = CacheKeyHelper.getCacheKeyAndCheck(cacheEnum, obj);
		// 如果获取的key为空，则说明缓存开关是关闭的
		if (StringUtils.isEmpty(cacheKey)) {
			return;
		}
		try {
			Collection<String> strValues = new ArrayList<String>();
			for (T t : values) {
				String strValue = CacheKeyHelper.value2String(t);
				if (StringUtils.isEmpty(strValue)) {
					continue;
				}
				strValues.add(strValue);
			}
			yhListOperations.rightPushAll(cacheKey, strValues);
			yHRedisTemplate.longExpire(cacheKey, timeout, unit);
		} catch (Exception e) {
			logger.warn("rightPushAll redis list operation failed. key is {}", cacheKey, e);
		}
	}

}
