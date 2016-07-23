package com.yoho.gateway.controller.message;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.BaseSmsSendReqVO;
import com.yoho.service.model.sms.request.BaseSmsSendReqBO;

@Controller
public class SmsSendController {
	
	static Logger logger = LoggerFactory.getLogger(SmsSendController.class);
	
	 @Autowired
	 private ServiceCaller service;
	 
	 //发送成功
	 private final static int SMS_SEND_SUCCESS_CODE= 200;
	 private final static String SMS_SEND_SUCCESS_MSG = "发送成功.";
	
	@RequestMapping(params = "method=Base.Sms.send")
    @ResponseBody
    public ApiResponse sendMessageToMobile(BaseSmsSendReqVO vo) throws GatewayException{
		logger.info("SmsSendController.sendMessageToMobile param is {}",vo);
		
		ApiResponse apiResponse = null;
		if(StringUtils.isEmpty(vo.getProject())){
			logger.warn("validRequest fail because mobile is null, request is {}",vo);
			throw new ServiceException(ServiceError.SMS_TEMPLATE_PROJECT_NULL);
		}
		if(StringUtils.isEmpty(vo.getMessage())){
			logger.warn("validRequest fail because message is null,request is {}",vo);
			throw new ServiceException(ServiceError.SMS_MESSAGE_IS_NULL);
		}
		if(StringUtils.isEmpty(vo.getTarget())){
			logger.warn("validRequest fail because mobile is null,request is {}",vo);
 			throw new ServiceException(ServiceError.MOBILE_IS_NULL);
		}
//		if(StringUtils.isEmpty(vo.getToken())){
//			logger.warn("validRequest fail because token is null,request is {}",vo);
//			throw new ServiceException(ServiceError.SMS_TOKEN_IS_NULL);
//		}
		
		BaseSmsSendReqBO bo = new BaseSmsSendReqBO();
		bo.setProject(vo.getProject());
		bo.setEnd_time(vo.getEnd_time());
		bo.setStart_time(vo.getStart_time());
//		bo.setToken(vo.getToken());
		bo.setTarget(vo.getTarget());
		bo.setMessage(vo.getMessage());
		service.call("message.baseSmsSend", bo, String.class);
		
		apiResponse = new ApiResponse.ApiResponseBuilder().code(SMS_SEND_SUCCESS_CODE).message(SMS_SEND_SUCCESS_MSG).build();
		return apiResponse;
	}
}
