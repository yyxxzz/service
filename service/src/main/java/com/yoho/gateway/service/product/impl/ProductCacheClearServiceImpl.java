package com.yoho.gateway.service.product.impl;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.product.CacheClearClientHolder;
import com.yoho.gateway.service.product.ProductCacheClearService;
import com.yoho.product.model.ProductBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.BatchBaseRequest;

/**
 * 描述:清理商品缓存
 */
@Service
public class ProductCacheClearServiceImpl implements ProductCacheClearService {

	static final Logger logger = LoggerFactory.getLogger(ProductCacheClearServiceImpl.class);
	
	@Autowired
    private ServiceCaller serviceCaller;
	
	@Autowired
	private MemecacheClientHolder memecacheClientHolder;
	
	@Autowired
	private CacheClearClientHolder cacheClearClientHolder;
	
	private ExecutorService threadPoolExecutor = new ThreadPoolExecutor(2, 10, 10L, TimeUnit.MILLISECONDS,   new LinkedBlockingQueue<Runnable>(10000), new DefaultHandler());
	
	private final static String LEVEL1_CACHEKEY="yh:gw:product:level1:";
	// 商品详情页下半页
	private final static String PRODUCTINTRO_LEVEL1_CACHEKEY="YH:GW:L1:yh_gw:queryProductInfo:";
	private final static String H5_PRODUCTINTRO_LEVEL1_CACHEKEY="YH:GW:L1:yh_gw:queryProductIntro:";
	
	// pc商品详情页模特卡
	private final static String PRODUCTMODELCARD_LEVEL1_CACHEKEY="YH:GW:L1:yh_gw:queryProductModelcardByPrdId:";
	
	// pc商品详情页模特卡
	private final static String PRODUCTCOMFORT_LEVEL1_CACHEKEY="YH:GW:L1:yh_gw:queryProductComfort:";
	
	// pc商品详情页模特卡
	private final static String MODELTRY_LEVEL1_CACHEKEY="YH:GW:L1:yh_gw:queryModelTryBySkn:";
	
	// pc商品详情页模特卡
	private final static String PRODUCTCOLLOCATION_LEVEL1_CACHEKEY="YH:GW:L1:yh_gw:queryProductCollocationByPrdId:";
	

	@Override
	public void clearProductCacheBySkn(Integer productSkn) {
		//清理gw的缓存key:SKN
		logger.info("start clear memcache, productSkn is:{}", productSkn);
		memecacheClientHolder.getLevel1Cache().delete(LEVEL1_CACHEKEY+productSkn);
		//清理商品详情页下半部分
		memecacheClientHolder.getLevel1Cache().delete(PRODUCTINTRO_LEVEL1_CACHEKEY+productSkn+"-1-");
		memecacheClientHolder.getLevel1Cache().delete(PRODUCTINTRO_LEVEL1_CACHEKEY+productSkn+"-2-");
		memecacheClientHolder.getLevel1Cache().delete(PRODUCTINTRO_LEVEL1_CACHEKEY+productSkn+"-1-1,3");
		memecacheClientHolder.getLevel1Cache().delete(PRODUCTINTRO_LEVEL1_CACHEKEY+productSkn+"-2-2,3");
		//清理h5的商品详情页下半部分缓存
		memecacheClientHolder.getLevel1Cache().delete(H5_PRODUCTINTRO_LEVEL1_CACHEKEY+productSkn);
		//清理pc的商品详情页模特卡、模特试穿、舒适度等缓存
		memecacheClientHolder.getLevel1Cache().delete(MODELTRY_LEVEL1_CACHEKEY+productSkn);
		//清理两云之间的缓存
		cacheClearClientHolder.clearCache(LEVEL1_CACHEKEY+productSkn);
		
		BatchBaseRequest<Integer> batchBaseRequest=new BatchBaseRequest<Integer>();
		batchBaseRequest.setParams(Lists.newArrayList(productSkn));
		ProductBo[] productBoList=serviceCaller.call("product.batchQueryProductBasicInfo", batchBaseRequest, ProductBo[].class);
		if(null==productBoList||productBoList.length==0)
		{
			return ;
		}
		//清理gw的缓存 key:ID
		memecacheClientHolder.getLevel1Cache().delete(LEVEL1_CACHEKEY+productBoList[0].getId());
		//清理pc的商品详情页模特卡、模特试穿、舒适度等缓存
		memecacheClientHolder.getLevel1Cache().delete(PRODUCTMODELCARD_LEVEL1_CACHEKEY+productBoList[0].getId());
		memecacheClientHolder.getLevel1Cache().delete(PRODUCTCOMFORT_LEVEL1_CACHEKEY+productBoList[0].getId());
		memecacheClientHolder.getLevel1Cache().delete(PRODUCTCOLLOCATION_LEVEL1_CACHEKEY+productBoList[0].getId());
				
		//清理两云之间的缓存
		cacheClearClientHolder.clearCache(LEVEL1_CACHEKEY+productBoList[0].getId());
		logger.info("success clear memcache, productSkn is:{}", productSkn);
		
		logger.info("start clear product server redis, productSkn is:{}", productSkn);
		BaseRequest<Integer> baseRequest=new BaseRequest<Integer>();
		baseRequest.setParam(productSkn);
		//调用服务的接口清除缓存
		serviceCaller.call("product.postClearProductCache", baseRequest, String.class);
		logger.info("sucess clear product server redis, productSkn is:{}", productSkn);
		
	}


	@Override
	public void clearBatchProductCacheBySkn(List<Integer> productSkns) {
		threadPoolExecutor.execute(new Runnable() {
			@Override
			public void run() {
				for (Integer productSkn : productSkns) {
					clearProductCacheBySkn(productSkn);
				}
			}
		});
	}

	static class DefaultHandler implements RejectedExecutionHandler{
		@Override
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			logger.warn("threadPoolExecutor invoke find wrong. className is {}", r.getClass().getName());
		}
	}
    
}
