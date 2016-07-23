package com.yoho.gateway.service.search.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.gateway.service.search.DiscountService;
import com.yoho.gateway.service.search.wrapper.SearchRestTemplateWrapper;

/**
 * 打折查询接口
 * @author mali
 *
 */
@Service
public class DiscountServiceImpl implements DiscountService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DiscountServiceImpl.class);
	
	// 搜索推荐URL的链接
	@Value("${ip.port.search.server}")
	private String searchServerIpAndPort;

    @Autowired
    private SearchRestTemplateWrapper searchRestTemplateWrapper;
	
    /**
     * 查询打折信息
     * @param paramMap
     * @return  打折集合的JSON对象
     */
	@Override
	public Object getDiscount(String searchFrom,String dynamicParam) {
		LOGGER.info("search discount list begin. dynamicParam : {}", dynamicParam);
		
		String discountStr = null;
		
		// 发送http请求 
		String url = getUrl(dynamicParam);
		try {
			discountStr = searchRestTemplateWrapper.getForObject(searchFrom,url, String.class, Maps.newHashMap());
		} catch (Exception e) {
			LOGGER.warn("The Result search discount list. discount : {}" ,discountStr, e);
			throw new ServiceNotAvaibleException("search server!!!", e);
		}
		LOGGER.info("Method of getDiscount end. dynamicParam is " + dynamicParam);
		LOGGER.debug("The result of search discount list is {}, url : {}, dynamicParam : {}", 
				discountStr, url, dynamicParam);
		
		JSONObject discountJson = (JSONObject)JSONObject.parse(discountStr);
		if (null == discountJson) {
			LOGGER.warn("The Result of yohosearch/discount.json is wrong. dynamicParam is {}, discountStr is {}",
					dynamicParam, discountStr);
			return null;
		}
		JSONObject data = (JSONObject)discountJson.get("data");
		if (null == data) {
			LOGGER.warn("The Result of yohosearch/discount.json is wrong. dynamicParam is {}, discountStr is {}", 
					dynamicParam, discountStr);
			return null;
		}
		return data.get("discount");
	}
	
	private String getUrl(String dynamicParam) {
		return "http://" + searchServerIpAndPort + "/yohosearch/discount.json?" + dynamicParam;
	}
}
