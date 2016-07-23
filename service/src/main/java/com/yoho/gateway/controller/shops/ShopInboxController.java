package com.yoho.gateway.controller.shops;

import java.util.Map;

import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.common.restbean.ResponseBean;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.service.model.shops.request.ShopInboxReqBO;

@Controller
public class ShopInboxController {

	static Logger logger = LoggerFactory.getLogger(ShopInboxController.class);
	
	@Autowired
	ServiceCaller serviceCaller;
	/**
	 * 商家端，获取站内信列表
	 */
	@RequestMapping(params="method=app.shopInbox.getList")
	@ResponseBody
	public ApiResponse getList(@RequestParam("shopsId") String shopsId) throws GatewayException{
		logger.info("enter ShopInboxController.getList shopsId is {}",shopsId);
		//shopsId检验
		if(StringUtils.isEmpty(shopsId)||!shopsId.matches("\\d+")){
			logger.warn("ShopInboxController.getList shopsId is error,shopsId is {}",shopsId);
			throw new ServiceException(ServiceError.SHOPS_ID_IS_ERROR);
			
		}
		ShopInboxReqBO shopInboxReqBO = new ShopInboxReqBO();
		shopInboxReqBO.setShopsId(shopsId);
		Map<String, Object> response = serviceCaller.call("platform.queryListByShopsId", shopInboxReqBO, Map.class);
		return new ApiResponse.ApiResponseBuilder().data(response).build();
	}
	
	/**
	 * 根据shopsID，站内信id删除商家端用户站内信
	 */
	@RequestMapping(params="method=app.shopInbox.delShopInbox")
	@ResponseBody
	public ApiResponse delShopInbox(@RequestParam("shopsId") String shopsId,@RequestParam("ids") String ids) throws GatewayException{
		logger.info("enter ShopInboxController.delShopInbox shopsId is {},",shopsId);
		//shopsId检验
		if(StringUtils.isEmpty(shopsId)||!shopsId.matches("\\d+")){
			logger.warn("ShopInboxController.delShopInbox uid is error,shopsId is {}",shopsId);
			throw new ServiceException(ServiceError.SHOPS_ID_IS_ERROR);
		}
		if(StringUtils.isEmpty(ids)){
			logger.warn("ShopInboxController.delShopInbox ids is error,ids is {}",ids);
			throw new ServiceException(ServiceError.SHOPS_INBOX_ID_IS_ERROR);
		}
		ShopInboxReqBO shopInboxReqBO = new ShopInboxReqBO();
		shopInboxReqBO.setShopsId(shopsId);
		shopInboxReqBO.setIds(ids);
		ResponseBean response = serviceCaller.call("platform.delByIdsAndShopsId", shopInboxReqBO, ResponseBean.class);
		return new ApiResponse.ApiResponseBuilder().data(response).build();
	}
	
	/**
	 *  获取消息的数量, 根据is_read条件过滤.
	 */
	@RequestMapping(params="method=app.shopInbox.getShopInboxTotal")
	@ResponseBody
	public ApiResponse getShopInboxTotal(@RequestParam("shopsId") String shopsId,@RequestParam("isRead") String isRead) throws GatewayException{
		logger.info("enter ShopInboxController.getShopInboxTotal shopsId is {}, isRead is {}",shopsId,isRead);
		//shopsId检验
		if(StringUtils.isEmpty(shopsId)||!shopsId.matches("\\d+")){
			logger.warn("ShopInboxController.getShopInboxTotal uid is error,shopsId is {}",shopsId);
			throw new ServiceException(ServiceError.SHOPS_ID_IS_ERROR);
		}
		ShopInboxReqBO shopInboxReqBO = new ShopInboxReqBO();
		shopInboxReqBO.setShopsId(shopsId);
		shopInboxReqBO.setIsRead(isRead);
		Integer response = serviceCaller.call("platform.getShopInboxTotal", shopInboxReqBO, Integer.class);
		return new ApiResponse.ApiResponseBuilder().data(response).build();
	}
	
	/**
	 * 批量设置为已读
	 */
	@RequestMapping(params="method=app.shopInbox.batchSetIsRead")
	@ResponseBody
	public ApiResponse batchSetIsRead(@RequestParam("shopsId") String shopsId,@RequestParam("ids") String ids) throws GatewayException{
		logger.info("enter ShopInboxController.delShopInbox shopsId is {}, isRead is {}",shopsId,ids);
		//shopsId检验
		if(StringUtils.isEmpty(shopsId)||!shopsId.matches("\\d+")){
			logger.warn("ShopInboxController.getShopInboxTotal uid is error,shopsId is {}",shopsId);
			throw new ServiceException(ServiceError.SHOPS_ID_IS_ERROR);
		}
		if(StringUtils.isEmpty(ids)){
			logger.warn("ShopInboxController.delShopInbox uid is error,shopsId is {}",shopsId);
			throw new ServiceException(ServiceError.SHOPS_INBOX_ID_IS_ERROR);
		}
		ShopInboxReqBO shopInboxReqBO = new ShopInboxReqBO();
		shopInboxReqBO.setShopsId(shopsId);
		shopInboxReqBO.setIds(ids);
		
		ResponseBean response = serviceCaller.call("platform.batchSetIsRead", shopInboxReqBO, ResponseBean.class);
		return new ApiResponse.ApiResponseBuilder().data(response).build();
	}
}
