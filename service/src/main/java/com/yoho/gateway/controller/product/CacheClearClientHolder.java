package com.yoho.gateway.controller.product;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.yoho.core.cache.CacheClient;
import com.yoho.core.cache.impl.MemcachedClientFactory;

//TODO  临时清除缓存的方法
@Component
public class CacheClearClientHolder {
	
	private CacheClient aliyunCache;
	 
	private CacheClient awsCache;
	
	private final static String QQ_ADDRESS="mem01.l1.yohoops.org:21211,mem01.l1.yohoops.org:21212,mem02.l1.yohoops.org:21211,mem02.l1.yohoops.org:21212,mem03.l1.yohoops.org:21211,mem03.l1.yohoops.org:21212";
	
	private final static String AWS_ADDRESS="mem01.l1.yohoops.org:21211,mem01.l1.yohoops.org:21212,mem02.l1.yohoops.org:21211,mem02.l1.yohoops.org:21212,mem03.l1.yohoops.org:21211,mem03.l1.yohoops.org:21212";
	
	private volatile boolean inited=false;
	
	//在使用的时候去初始化
	public void clearCache(final String key)
	{		
		if(!inited)
		{
			initClient();
		}
		try
		{
			aliyunCache.delete(key);
			awsCache.delete(key);
		}catch(Exception e)
		{
			//donothing
		}
	}

	private void initClient() {
		try
		{
			//阿里云 memcache
			if(StringUtils.isNotBlank(QQ_ADDRESS))
			{
				MemcachedClientFactory aliyunClientFactory = new MemcachedClientFactory(StringUtils.split(QQ_ADDRESS, ","));
				aliyunCache = aliyunClientFactory.getObject();
			}
			
			if(StringUtils.isNotBlank(AWS_ADDRESS))
			{
				MemcachedClientFactory awsClientFactory = new MemcachedClientFactory(StringUtils.split(AWS_ADDRESS, ","));
				awsCache = awsClientFactory.getObject();
			}
		}catch(Exception e)
		{
			//donothing
		}
		inited=true;
	}
}
