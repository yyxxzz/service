package com.yoho.gateway.service.search.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.core.rest.exception.ServiceNotFoundException;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.service.search.KeywordsService;
import com.yoho.product.model.KeyWordInfo;

/**
 * 搜索关键词相关接口
 * @author mali
 *
 */
@Service
public class KeywordsServiceImpl implements KeywordsService {
	/**
	 * LOG
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KeywordsServiceImpl.class);
	
	/**
	 * http请求工具
	 */
	@Autowired
    private ServiceCaller serviceCaller;
	
	/**
	 * 将搜索关键词入搜索记录表
	 * @param query 去html预定便签的关键词
	 * @param platform 平台标识
	 */
	@Override
	public void saveKeyWords(String query, String platform) {
		if (StringUtils.isEmpty(query)) {
			LOGGER.warn("saveKeyWords find query is null.");
			return;
		}
		Integer responseBean = null;
		try {
			responseBean = serviceCaller.call("product.saveKeyWords", new KeyWordInfo(query, platform), Integer.class);
		} catch (ServiceException e) {
			LOGGER.warn("KeywordsServiceImpl.saveKeyWords find wrong. query is:{},platform is:{}", query , platform, e);
		} catch (ServiceNotAvaibleException e) {
			LOGGER.warn("Service not avaible; query is:{},platform is :{}",query , platform, e);
		} catch (ServiceNotFoundException e) {
			LOGGER.warn("Service not found; query is:{},platform is :{}",query , platform, e);
		}
		// 保存失败，则记录日志。无需中断用户操作
		if (null == responseBean || 0 == responseBean) {
			LOGGER.warn("product.saveKeyWords is fail. query is:{}, platform is :{}", query, platform);
		}
	}
}
