package com.yoho.gateway.helper;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.netflix.config.DynamicBooleanProperty;
import com.netflix.config.DynamicPropertyFactory;

/**
 * 缓存所有的KEY的管理 格式: 所有的KEY均由 yh:users:XXXX 开头
 *
 * @author Created by dengxinfei on 2016/3/13.
 */
public class CacheKeyHelper {

	static Logger log = LoggerFactory.getLogger(CacheKeyHelper.class);

	/**
	 * 根据枚举，获取该枚举对应的缓存key
	 * 获取之前需要判断缓存总开关和该缓存对应的开关
	 * 开关配置在cachesSwitch.properties中
	 * @param cacheKey
	 * @param obj
	 * @return
	 */
	public static String getCacheKeyAndCheck(CacheKeyEnum cacheKey, Object obj) {
		//获取总开关
		DynamicBooleanProperty totalOnoff = DynamicPropertyFactory.getInstance().getBooleanProperty("redis.total.key.switch", false);
		log.info("getCacheKeyAndCheck. totalOnoff is {}, cacheKey is {}, obj is {}", totalOnoff.get(), cacheKey, obj);
		if (!totalOnoff.get()) {
			return null;
		}

		//如果开关的key为空，则用缓存key代替
		String switchKey = cacheKey.getSwitchKey();
		if (StringUtils.isEmpty(switchKey)) {
			switchKey = cacheKey.getCacheKey();
		}
		//获取该缓存对应的开关
		DynamicBooleanProperty onoff = DynamicPropertyFactory.getInstance().getBooleanProperty(switchKey, false);
		if (!onoff.get()) {
			return null;
		}
		log.debug("getCacheKeyAndCheck key is {}", cacheKey.getCacheKey());
		return cacheKey.getCacheKey() + obj;
	}
	
	/**
	 * 序列化value值
	 * @param value
	 * @return
	 */
	public static <T> String value2String(T value) {
		String v = null;
		if (value == null) {
			return null;
		}
		if (value instanceof String) {
			v = (String) value;
		} else {
			v = JSON.toJSONString(value);
		}
		return v;
	}
	
	/**
	 * 反序列化value值
	 * @param value
	 * @param clazz
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T string2Value(String value, Class<T> clazz) {
		if (StringUtils.isEmpty(value)) {
			return null;
		}
		if (clazz.getName().equalsIgnoreCase("java.lang.String")) {
			return (T) value;
		}
		return (T) JSON.parseObject(value, clazz);
	}

}
