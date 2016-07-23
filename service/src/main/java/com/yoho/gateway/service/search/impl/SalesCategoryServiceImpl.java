package com.yoho.gateway.service.search.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.search.SalesCategoryController;
import com.yoho.gateway.service.search.SalesCategoryService;
import com.yoho.product.model.QuerySalesCategoryReq;
import com.yoho.product.model.SalesCategoryBo;

/**
 * 销售分类的操作接口
 * @author mali
 *
 */
@Service
public class SalesCategoryServiceImpl implements SalesCategoryService {
	private static final String CHANNEL_LIFE = "4";

	private static final String CHANNEL_CHRILD = "3";

	private static final String CHANNEL_GIRAL = "2";

	private static final String CHANNEL_BOY = "1";

	/**
	 * LOGGER
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SalesCategoryController.class);

	private static final Map<String, String> defaultRelationParamMap = new HashMap<String, String>(4);
	
	static {
		defaultRelationParamMap.put(CHANNEL_BOY, "114,293,116,117,115,119,258,124,125,227,122,118,123,121,173,131,129,346,133,348,147,149,148,151,231,152,153,196,238,239,240,349,157,342,156,161,300,160,233,341,340,210,339,351,186,235,163,224,322,323,324,325,326,327,328,329,330,331,72,74,75,76,142,77,78,79,80,199,213,217,218,234,303,305,306,307,334,162,316,164,317,295,321,311,357,359,362,320,195,197,413,130");
		defaultRelationParamMap.put(CHANNEL_GIRAL, "227,173,125,258,121,123,119,124,118,348,122,115,117,346,114,293,131,133,186,235,163,224,322,323,324,325,326,327,328,329,330,331,72,74,75,76,142,77,78,79,80,199,213,217,218,234,303,305,306,307,334,162,316,320,164,317,295,321,147,149,148,151,231,311,364,357,359,362,116,129,134,135,413,152,153,238,239,240,349,157,342,156,161,300,160,233,341,340,210,339,351,195,196,197");
		defaultRelationParamMap.put(CHANNEL_CHRILD, "407,405,403,397,374,389,385,377,381,378,379,383,393,395,376,375,373");
		defaultRelationParamMap.put(CHANNEL_LIFE, "285,113,171,185,200,211,212,216,313,398,267,268,269,270,271,272,273,274,275,276,277,332,333,353,281,282,283,284,292,304,110,111,112,184,260,261,262,263,264,265,352,355,302,343,344,399");
	}
	
	/**
	 * http请求工具
	 */
	@Autowired
    private ServiceCaller serviceCaller;
	
	/**
	 * 根据yh_channel查询其关联物理分类信息
	 * @param yh_channel
	 * @return 关联物理分类信息   查询部到则返回null
	 */
	@Override
	public String queryRelationParamter(String yh_channel) {
		LOGGER.info("Method queryRelationParamter in; yh_channel is:{}", yh_channel);
		
		SalesCategoryBo responseBean = null;
		QuerySalesCategoryReq req = new QuerySalesCategoryReq();
		
		try {
			req.setCategoryId(Integer.valueOf(yh_channel));
		} catch (NumberFormatException e) {
			LOGGER.warn("queryRelationParamter find wrong because yh_channel is not number. yh_channel is :{}" , yh_channel);
			return defaultRelationParamMap.get(CHANNEL_BOY);
		}
		try {
			responseBean = serviceCaller.call("product.querySalesCategoryById", req, SalesCategoryBo.class);
		} catch (Exception e) {
			LOGGER.warn("querySalesCategoryById find wrong. yh_channel is :{}" , yh_channel, e);
		} 
		if (null == responseBean) {
			LOGGER.warn("The result of get RelationParamter from yh_channel is null. yh_channel is :", yh_channel);
			return defaultRelationParamMap.get(yh_channel);
		}
		String relationParameter = responseBean.getRelationParameter();
		LOGGER.info("product.queryRelationParamter invoke success. responseBean is :{}", relationParameter);
		return relationParameter;
	}
}
