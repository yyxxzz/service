package com.yoho.gateway.controller.product.h5;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Lists;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.PreferenceSearchService;
import com.yoho.gateway.utils.ListUtils;
import com.yoho.product.request.search.BaseSearchRequest;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 给H5提供的为你优选接口,为你优选每个人看到的商品应该都不能一样，至少顺序是不能一样的
 * @author xieyong
 *
 */
@Controller
public class H5PreferenceSearchController {
	 	
	private final Logger logger = LoggerFactory.getLogger(H5PreferenceSearchController.class);
	
	 //默认优选的条数
    private final static Integer DEFAULT_PREFERENCESIZE=9;
    
    @Autowired
    private PreferenceSearchService preferenceSearchService;
    
	@Autowired
    private ServiceCaller serviceCaller;
	
	@Autowired
	private CacheClient cacheClient;
	
	/**
	 * 商品页的为你优选,优选逻辑都是同一个品牌下的商品
	 * @param yhchannel 频道
	 * @param brandId 品牌id
	 * @return
	 * @throws GatewayException 
	 */
	@RequestMapping(params = "method=h5.preference.Search")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_product_preference)
	public JSONArray queryPreference(
			@RequestParam(value = "yhchannel", required = true) Integer yhchannel,
			@RequestParam(value = "brandId", required = false) Integer brandId) throws GatewayException {
		
		if(null==yhchannel)
		{
			throw new GatewayException(404, "yhchannel Is Null");
		}
		//商品详情页没有品牌就没有为你优选
		if(null==brandId)
		{	
			logger.info("no Preference method=h5.preference.Search yhchannel is:{},brandId is:{}",yhchannel,brandId);
			return new JSONArray();
		}
		logger.info("queryPreference method=h5.preference.Search yhchannel is:{},brandId is:{}",yhchannel,brandId);
		
		ProductSearchReq req = new ProductSearchReq().setBrand(String.valueOf(brandId)).setYhChannel(null==yhchannel?null:String.valueOf(yhchannel))
				.setSearchFrom("query.preference");
		JSONArray preferenceJsonArray = preferenceSearchService.queryPreference(req);
		if (null != preferenceJsonArray && preferenceJsonArray.size() >= DEFAULT_PREFERENCESIZE) {
			preferenceJsonArray = new JSONArray(ListUtils.getSubList(preferenceJsonArray, 0, DEFAULT_PREFERENCESIZE));
		}
		logger.info("queryPreference\"method=h5.preference.Search exit yhchannel is:{},brandId is:{} preferenceJsonArray size:{}", yhchannel, brandId, null == preferenceJsonArray ? 0
				: preferenceJsonArray.size());
		return preferenceJsonArray;
	}
	
	
	
	/**
	 * //除去商品页的为你优选
	 * @param yhchannel 频道
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=h5.sortPreference.Search")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_product_preference)
	public JSONArray querySortPreference(@RequestParam(value = "yhchannel", required = true) Integer yhchannel) throws GatewayException {
		
		if(null==yhchannel)
		{
			throw new GatewayException(404, "yhchannel Is Null");
		}
		logger.info("querySortPreference method=h5.sortPreference.Search yhchannel is:{}",yhchannel);
		ProductSearchReq req = new ProductSearchReq().setYhChannel(null==yhchannel?null:String.valueOf(yhchannel)).setSearchFrom("query.preference");
		JSONArray jsonArray = preferenceSearchService.querySortPreference(req);
		
		if (CollectionUtils.isNotEmpty(jsonArray)) {
			if (jsonArray.size() > DEFAULT_PREFERENCESIZE) { // 最多返回9个
				jsonArray = new JSONArray(ListUtils.getSubList(jsonArray, 0, DEFAULT_PREFERENCESIZE));
			}
		}
		
		logger.info("queryPreference method=h5.sortPreference.Search exit yhchannel is:{}",yhchannel);
		return jsonArray;
	}
}
