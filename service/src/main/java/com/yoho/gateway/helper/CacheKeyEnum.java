/**
 * 
 */
package com.yoho.gateway.helper;

import org.apache.commons.lang3.StringUtils;

/**
 * 描述：
 * 
 * @author ping.huang 2016年3月17日
 */
public enum CacheKeyEnum {

	MY_VIP("yh:users:myvip:", "redis.user.vip.switch", "users.vip.redisExpire", "用户VIP"),
	MY_PROFILE("yh:users:myprofile:", "redis.user.profile.switch", "users.profile.redisExpire", "个人信息"),
	AREA_LIST("yh:users:arealist:", "redis.user.arealist.switch", "users.arealist.redisExpire", "区域列表信息"),
	;

	// 缓存的key
	private String cacheKey;
	// 开关的key（如果为空，则开关key与缓存key一样）
	private String switchKey;
	// 超时时间配置的key
	private String expireKey;
	private String desc;

	private CacheKeyEnum(String cacheKey, String switchKey, String expireKey, String desc) {
		this.cacheKey = cacheKey;
		this.switchKey = switchKey;
		this.expireKey = expireKey;
		this.desc = desc;
	}

	/**
	 * 获取该枚举的信息
	 * 
	 * @param key
	 * @return
	 */
	public static CacheKeyEnum getEnumByKey(String key) {
		if (StringUtils.isEmpty(key)) {
			return null;
		}
		for (CacheKeyEnum e : values()) {
			if (key.equalsIgnoreCase(e.name())) {
				return e;
			}
		}
		return null;
	}

	public String getCacheKey() {
		return cacheKey;
	}

	public String getSwitchKey() {
		return switchKey;
	}

	public String getExpireKey() {
		return expireKey;
	}

	public String getDesc() {
		return desc;
	}

}