package com.yoho.gateway.controller.shops;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.shops.AppVersionCheckRequestVO;
import com.yoho.gateway.model.shops.AppVersionRequestVO;
import com.yoho.service.model.shops.request.AppVersionCheckRequestBO;
import com.yoho.service.model.shops.response.AppVersionRspBO;
import com.yoho.service.model.response.CommonRspBO;

@Controller
public class AppVersionController {

	@Resource
	ServiceCaller serviceCaller;

	private Logger logger = LoggerFactory.getLogger(AppVersionController.class);
	

	/**
	 * 商家端，关于我们，检查客户的版本
	 */
	@RequestMapping(params = "method=app.shops.checkAppVersion")
	@ResponseBody	
	public ApiResponse checkAppVersion(AppVersionCheckRequestVO request) throws GatewayException{
		logger.info("AppVersionController.checkAppVersion request is {}",request);
		String app_version = request.getApp_version();
		if(StringUtils.isEmpty(app_version)){
			logger.warn("AppVersionServiceImpl.setAppVersion error appVersion is null");
			throw new ServiceException(ServiceError.SHOPS_APP_VERSION_IS_NULL);
		}
		String client_type = request.getClient_type();
		if(StringUtils.isEmpty(client_type)){
			logger.warn("AppVersionServiceImpl.setAppVersion error clientType is null");
			throw new ServiceException(ServiceError.SHOPS_CLIENT_TYPE_IS_NULL);			
		}
		
		AppVersionCheckRequestBO check = new AppVersionCheckRequestBO();
		check.setApp_version(app_version);
		check.setClient_type(client_type);
		AppVersionRspBO response =  serviceCaller.call("platform.checkAppVersion", check, AppVersionRspBO.class);
		return new ApiResponse.ApiResponseBuilder().data(response).build();		
	}

}
