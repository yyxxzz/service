package com.yoho.gateway.controller.users;

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
import com.yoho.gateway.model.request.SubscriberReqVO;
import com.yoho.service.model.request.SubscriberReqBO;
import com.yoho.service.model.response.CommonRspBO;

/**
 * 通过邮箱添加订阅，往yh_passport.subscriber表中插入数据
 * @author gezhengwen
 *
 */
@Controller
public class SubscriberController {
	@Autowired
	ServiceCaller serviceCaller;
	
	private static final Logger logger = LoggerFactory.getLogger(SubscriberController.class);
	
	@RequestMapping(params ="method=open.subscriber.subscriber")
	@ResponseBody
	public ApiResponse emailSubscriber(SubscriberReqVO vo) throws GatewayException{
		logger.info("SubscriberController emailSubscriber param is {}",vo);
		String email = vo.getEmail();
		if(StringUtils.isEmpty(email)){
			logger.warn("saveSubscriber error because email is null");
			throw new ServiceException(ServiceError.EMAIL_NULL);
		}
		SubscriberReqBO bo = new SubscriberReqBO();
		bo.setEmail(email);
		bo.setUid(vo.getUid());
		CommonRspBO response = serviceCaller.call("users.subscriber", bo, CommonRspBO.class);
		return new ApiResponse.ApiResponseBuilder().message("success").data(response).build();
		
	}
	
}
