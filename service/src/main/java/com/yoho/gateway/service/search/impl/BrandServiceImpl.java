package com.yoho.gateway.service.search.impl;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.search.SimpleBrandInfoVo;
import com.yoho.gateway.service.assist.BrandInfoConvert;
import com.yoho.gateway.service.search.BrandService;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.QueryBrandIdReq;
import com.yoho.product.model.SimpleBrandInfo;
import com.yoho.product.request.BaseRequest;

/**
 * 品牌相关接口实现
 * @author mali
 *
 */
@Service
public class BrandServiceImpl implements BrandService {
	/**
	 * LOG
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(BrandServiceImpl.class);
	
	/**
	 * http请求工具
	 */
	@Autowired
    private ServiceCaller serviceCaller;
	
	/**
	 * http请求工具
	 */
	@Autowired
	private BrandInfoConvert brandInfoConvert;
	
	/**
	 * 查询与关键词匹配的品牌ID
	 * @param query 关键词
	 * @return 匹配的品牌ID，匹配不上返回NUll
	 */
	@Override
	public SimpleBrandInfoVo getBrandInfoByQuery(String query) {
		SimpleBrandInfo responseBean = null;
		
		try {
			responseBean = serviceCaller.call("product.getBrandInfoByQuery", new QueryBrandIdReq(query), SimpleBrandInfo.class);
		} catch (Exception e) {
			LOGGER.warn("BrandServiceImpl.getBrandInfoByQuery(String) find wrong. query is:{}",query,e);
		} 
		
		// 保存失败，则记录日志。无需中断用户操作
		if (null == responseBean) {
			LOGGER.info("product.getBrandInfoByQuery is empty. query is:{}", query);
			return null;
		}
		
		// 转换成VO
		return brandInfoConvert.convertVo(responseBean);
	}
	
	/**
	 * 查询所有的品牌列表
	 * @return
	 */
	@Override
	public Map<Integer, BrandBo> queryAllBrandList() {
		try {
			BrandBo[] brandBoList = serviceCaller.call("product.queryAllBrandList", new BaseRequest<Integer>(), BrandBo[].class);
			return buildBrandMap(brandBoList);
		} catch (Exception e) {
			LOGGER.warn("BrandServiceImpl.getBrandInfoByQuery(String) find wrong", e);
			return Maps.newHashMap();
		}
	}

	private Map<Integer, BrandBo> buildBrandMap(BrandBo[] brandBoList) {
		Map<Integer, BrandBo> brandMap=new HashMap<Integer, BrandBo>(brandBoList.length);
		for (BrandBo brandBo : brandBoList) {
			brandMap.put(brandBo.getId(), brandBo);
		}
		return brandMap;
	}
}
