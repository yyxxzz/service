package com.yoho.gateway.service.search.wrapper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.google.common.util.concurrent.AtomicLongMap;
import com.yoho.core.rest.client.ServiceCaller;

/**
 * 对调用搜索的地方做统一跟踪包装
 * @author xieyong
 *
 */
@Service
public class SearchRestTemplateWrapper {
	
	private final static Logger STATIC_LOGGER = LoggerFactory.getLogger("search-static");
	
	private final static Logger INVOKE_LOGGER = LoggerFactory.getLogger("search-invoke");
	
	private final static AtomicInteger successInvokeTimes=new AtomicInteger(0);
	
	private final static AtomicInteger failedInvokeTimes=new AtomicInteger(0);
	
    private final static AtomicLongMap<String> totalCostTime = AtomicLongMap.create();
	
	@Autowired
	private ServiceCaller serviceCaller;
	
	static{
		//启动一个任务，1分钟跑一次
		ScheduledExecutorService scheduledExecutorService=Executors.newScheduledThreadPool(1);
		scheduledExecutorService.scheduleAtFixedRate((new Runnable() {
			@Override
			public void run() {
				try
				{
					STATIC_LOGGER.info("every minutes successInvokeTimes:{},failedInvokeTimes:{},average costTime:{}",
							successInvokeTimes.get(),
							failedInvokeTimes.get(),
							successInvokeTimes.get()==0?0:totalCostTime.get("search")/ successInvokeTimes.get());
					//1分钟归0一次
					successInvokeTimes.set(0);
					failedInvokeTimes.set(0);
					totalCostTime.remove("search");
				}
				catch(Exception e)
				{
					//donothing
				}
			}
		}), 2,1, TimeUnit.MINUTES);
	}
	
	public <T> T getForObject(String searchFrom,String url, Class<T> responseType, Map<String, ?> urlVariables) throws RestClientException {
		long beignTime=System.currentTimeMillis();
		INVOKE_LOGGER.info("begin invoke From :{},url is:{},param is:{}",searchFrom,url,urlVariables);
		T t=null;
		try
		{	
			t=serviceCaller.get(null==searchFrom?"search.query":searchFrom, url, new HashMap<String, Object>(), responseType, null).get(1);
		}catch(Exception e)
		{	
			failedInvokeTimes.incrementAndGet();
			throw e;
		}
		successInvokeTimes.incrementAndGet();
		long costTime=System.currentTimeMillis()-beignTime;
		totalCostTime.addAndGet("search", costTime);
		INVOKE_LOGGER.info("end invoke From :{},url is:{},costTime is:{}",searchFrom,url,costTime);
		return t;
	}
}	
