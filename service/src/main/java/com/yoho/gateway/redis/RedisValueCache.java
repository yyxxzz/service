package com.yoho.gateway.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.yoho.core.redis.YHHashOperations;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.gateway.helper.CacheKeyEnum;
import com.yoho.gateway.helper.CacheKeyHelper;


/**
 * Value类型的Redis操作
 *
 * @author Created by xinfei on 16/3/13.
 */
@Component
public class RedisValueCache {

	private final static Logger logger = LoggerFactory.getLogger("redisOptionLog");

	@Autowired
	YHRedisTemplate<String, String> yHRedisTemplate;

	@Autowired
	YHValueOperations<String, String> yhValueOperations;
	
	@Autowired
	YHHashOperations<String,String,String> hashOperations;

	/**
	 * 根据KEY从REDIS中获取值
	 * 
	 * @param cacheEnum
	 *            对应的缓存key的枚举
	 * @param obj
	 *            对应的key的后缀
	 * @param clazz
	 *            转换的对象类型
	 * @return
	 */
	public <T> T get(CacheKeyEnum cacheEnum, Object obj, Class<T> clazz) {
		logger.debug("Enter get valueOperation redis value. cacheEnum is {}, obj is {}, clazz is {}", cacheEnum, obj, clazz);
		String cacheKey = null;
		try {
			cacheKey = CacheKeyHelper.getCacheKeyAndCheck(cacheEnum, obj);
			// 如果获取的key为空，则说明缓存开关是关闭的
			if (StringUtils.isEmpty(cacheKey)) {
				return null;
			}

			String strValue = yhValueOperations.get(cacheKey);
			logger.info("get redis value operations. value is {}", strValue);
			return CacheKeyHelper.string2Value(strValue, clazz);
		} catch (Exception e) {
			logger.warn("get redis value operation failed. cacheEnum is {}, obj is {}, error msg is {}", cacheEnum, obj, e.getMessage());
		}
		return null;
	}

	/**
	 * 批量get 该方法的key的前缀一定要一样
	 * 
	 * @param cacheEnum
	 *            对应的缓存key的枚举
	 * @param objList
	 *            对应的key的后缀
	 * @param clazz
	 *            转换的对象类型
	 * @return
	 */
	public <T> List<T> multiGet(CacheKeyEnum cacheEnum, Collection<Object> objList, Class<T> clazz) {
		logger.debug("Enter get multiGet redis value. cacheEnum is {}, clazz is {}", cacheEnum, clazz);
		if (CollectionUtils.isEmpty(objList)) {
			return null;
		}
		try {
			String cacheKey = CacheKeyHelper.getCacheKeyAndCheck(cacheEnum, "");
			// 如果获取的key为空，则说明缓存开关是关闭的
			if (StringUtils.isEmpty(cacheKey)) {
				return null;
			}

			List<String> keyList = new ArrayList<String>();
			for (Object obj : objList) {
				keyList.add(cacheKey + obj.toString());
			}
			List<String> valueList = yhValueOperations.multiGet(keyList);
			logger.debug("multiGet get value list size is {}", valueList == null ? 0 : valueList.size());
			if (CollectionUtils.isEmpty(valueList)) {
				return null;
			}
			List<T> list = new ArrayList<T>();
			for (String str : valueList) {
				list.add(CacheKeyHelper.string2Value(str, clazz));
			}
			return list;
		} catch (Exception e) {
			logger.warn("multiGet redis value operation failed. cacheEnum is {}, objList is {}, error msg is {}", cacheEnum, objList, e.getMessage());
		}
		return null;
	}
	
	public <T> Map<String, T> mHashGet(String key, List<String> fields, Class<T> clazz) 
	 {
		try {
			List<String> multiGet = hashOperations.multiGet(key, fields);
			int size = multiGet.size();
			String curItem;
			Map<String, T> result = new HashMap<String, T>(size);
			for (int i = 0; i < size; i++) {
				curItem = multiGet.get(i);
				if(null == curItem){
					result.put(fields.get(i), null);
					logger.debug("cache miss key is:{}", fields.get(i));
				} else {
					//基本类型直接返回
					if(String.class.equals(clazz))
					{
						result.put(fields.get(i), (T)curItem);
					}else
					{
						result.put(fields.get(i), JSON.parseObject(curItem, clazz));
					}
					logger.debug("cache hit key is:{}", fields.get(i));
				}
			}
			return result;

		} catch (Exception e) {
			logger.warn("hashGetfailed!!! key is: {},field is:{}", key,fields, e);
			return null;
		} 
	}
	
	public void mHashSet(String key, Map<String, String> map) 
	{
	   if (MapUtils.isEmpty(map)) {
		  return;
	   }
	
	   try {
		  hashOperations.putAll(key, map);;

	   } catch (Exception e) {
		  logger.warn("mHashGet failed!!! key is: {},field is:{}", key,map, e);
	   } 
	 }
	
	/**
	 * 往hash结构中存值
	 * @param key
	 * @param field
	 * @param value
	 */
	public void hashPut(String key, String field, String value) {
		try {
			hashOperations.put(key, field, value);
		} catch (Exception e) {
			logger.warn("hashPut cache failed!!! key is: {},field is:{}",key, field, e);
		} 
	}
	
	public void expire(String key, long timeout)
	{	
		try
		{	
			yHRedisTemplate.longExpire(key, timeout, TimeUnit.SECONDS);
		}
		catch(Exception e) {
			logger.warn("expire cache failed!!! key is: " + key, e);
		}
	}
	

	/**
	 * 设置值
	 * 
	 * @param cacheEnum
	 *            缓存开关的枚举
	 * @param obj
	 *            key的后缀
	 * @param value
	 *            缓存的值
	 * @param timeout
	 *            超时时间
	 * @param unit
	 *            超时时间单位
	 */
	public <T> void set(CacheKeyEnum cacheEnum, Object obj, T value, long timeout, TimeUnit unit) {
		logger.debug("Enter set valueOperation redis value. cacheEnum is {}, value is {}", cacheEnum, value);
		try {
			String v = CacheKeyHelper.value2String(value);
			String cacheKey = CacheKeyHelper.getCacheKeyAndCheck(cacheEnum, obj);
			// 如果获取的key为空，则说明缓存开关是关闭的
			if (StringUtils.isEmpty(cacheKey)) {
				return;
			}

			yhValueOperations.set(cacheKey, v);
			yHRedisTemplate.longExpire(cacheKey, timeout, unit);
		}catch (Exception e){
			logger.warn("Redis exception. value redis set . cacheEnum is {}, obj is {}, value is {}, error msg is {}", cacheEnum, obj, value, e.getMessage());
		}
	}

	/**
	 * 设置值之前检查key是否存在
	 * 
	 * @param cacheEnum
	 *            缓存开关的枚举
	 * @param obj
	 *            key的后缀
	 * @param value
	 *            缓存的值
	 * @param timeout
	 *            超时时间
	 * @param unit
	 *            超时时间单位
	 * @return
	 */
	public <T> boolean setIfAbsent(CacheKeyEnum cacheEnum, Object obj, T value, long timeout, TimeUnit unit) {
		logger.debug("Enter setIfAbsent valueOperation redis value. cacheEnum is {}, value is {}", cacheEnum, value);
		boolean b = false;
		try {
			String v = CacheKeyHelper.value2String(value);
			String cacheKey = CacheKeyHelper.getCacheKeyAndCheck(cacheEnum, obj);
			// 如果获取的key为空，则说明缓存开关是关闭的
			if (StringUtils.isEmpty(cacheKey)) {
				return false;
			}
			b = yhValueOperations.setIfAbsent(cacheKey, v);
			yHRedisTemplate.longExpire(cacheKey, timeout, unit);
		}catch (Exception e){
			logger.warn("Redis exception. setIfAbsent redis . cacheEnum is {}, obj is {}, value is {}, error msg is {}", cacheEnum, obj, value, e.getMessage());
		}
		return b;
	}

	/**
	 * 批量set
	 * 
	 * @param cacheEnum
	 *            缓存开关的枚举
	 * @param m
	 *            需要设置的值（该map的key是去除前缀的值）
	 * @param timeout
	 *            超时时间
	 * @param unit
	 *            超时时间单位
	 */
	public <T> void multiSet(CacheKeyEnum cacheEnum, Map<? extends String, ? extends T> m, long timeout, TimeUnit unit) {
		logger.debug("Enter multiSet valueOperation redis value. cacheEnum is {}", cacheEnum);
		if (MapUtils.isEmpty(m)) {
			return;
		}
		try {
			String cacheKey = CacheKeyHelper.getCacheKeyAndCheck(cacheEnum, "");
			// 如果获取的key为空，则说明缓存开关是关闭的
			if (StringUtils.isEmpty(cacheKey)) {
				return;
			}
			// 把value值转为string
			Map<String, String> map = new HashMap<String, String>();
			for (Entry<? extends String, ? extends T> entry : m.entrySet()) {
				map.put(cacheKey + entry.getKey(), CacheKeyHelper.value2String(entry.getValue()));
			}
			// 批量set
			yhValueOperations.multiSet(map);
			// 设置超时
			for (Entry<String, String> entry : map.entrySet()) {
				yHRedisTemplate.longExpire(entry.getKey(), timeout, unit);
			}
		}catch (Exception e){
			logger.warn("Redis exception. value redis multiSet . cacheEnum is {}, map is {}, exception msg is {}", cacheEnum, m, e.getMessage());
		}

	}

	/**
	 * 批量set
	 * 
	 * @param cacheEnum
	 *            缓存开关的枚举
	 * @param m
	 *            需要设置的值（该map的key是去除前缀的值）
	 * @param timeout
	 *            超时时间
	 * @param unit
	 *            超时时间单位
	 */
	public <T> void multiSetIfAbsent(CacheKeyEnum cacheEnum, Map<? extends String, ? extends T> m, long timeout, TimeUnit unit) {
		logger.debug("Enter multiSet valueOperation redis value. cacheEnum is {}", cacheEnum);
		if (MapUtils.isEmpty(m)) {
			return;
		}
		try {
			String cacheKey = CacheKeyHelper.getCacheKeyAndCheck(cacheEnum, "");
			// 如果获取的key为空，则说明缓存开关是关闭的
			if (StringUtils.isEmpty(cacheKey)) {
				return;
			}
			// 把value值转为string
			Map<String, String> map = new HashMap<String, String>();
			for (Entry<? extends String, ? extends T> entry : m.entrySet()) {
				map.put(cacheKey + entry.getKey(), CacheKeyHelper.value2String(entry.getValue()));
			}
			// 批量set
			yhValueOperations.multiSetIfAbsent(map);
			// 设置超时
			for (Entry<String, String> entry : map.entrySet()) {
				yHRedisTemplate.longExpire(entry.getKey(), timeout, unit);
			}
		}catch (Exception e){
			logger.warn("Redis exception. value redis multiSetIfAbsent . cacheEnum is {}, map is {}, exception msg is {}", cacheEnum, m, e.getMessage());
		}
	}

	/**
	 * 清除
	 * 
	 * @param cacheEnum
	 *            缓存开关的枚举
	 * @param obj
	 *            key的后缀
	 * 
	 */
	public <T> void delete(CacheKeyEnum cacheEnum, Object obj) {
		logger.debug("Enter delete valueOperation redis value. cacheEnum is {}", cacheEnum);
		try {
			String cacheKey = CacheKeyHelper.getCacheKeyAndCheck(cacheEnum, obj);
			// 如果获取的key为空，则说明缓存开关是关闭的
			if (StringUtils.isEmpty(cacheKey)) {
				return;
			}
			yHRedisTemplate.delete(cacheKey);
		}catch (Exception e){
			logger.warn("Redis exception. value redis delete . cacheEnum is {}, obj is {}, exception msg is {}", cacheEnum, obj, e.getMessage());
		}
	}

	public static void main(String[] args) {
		String str = "12345";
		String jsonStr = JSON.toJSONString(str);
		System.out.println(JSON.toJSONString(str));
		System.out.println(jsonStr.toString());
	}

}
