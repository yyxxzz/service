package com.yoho.gateway.controller.users;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.UidbackRequestVO;
import com.yoho.service.model.request.UidbackRequestBO;
import com.yoho.service.model.response.UidbackRspBO;

    /**
    * 功能描述：根据sso_id获取uid，若无对应的uid，则向user_profile中插入记录，获取生成的uid
    *@author create by Ling Min 2016-5-7
    */
@Controller
public class UserUidbackController {
	
    @Resource
    private ServiceCaller service;
    private static final Logger logger = LoggerFactory.getLogger(UserPassportController.class);
	
    /**
     * 功能描述：根据sso_id获取uid
     * @param requestVO
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.getUidback")	
    @ResponseBody
	public ApiResponse getUidback(UidbackRequestVO requestVO) throws GatewayException {
		logger.info("Begin call getUidback. UidbackReq is {}", requestVO);
		UidbackRequestBO requestBO = new UidbackRequestBO();
		BeanUtils.copyProperties(requestVO, requestBO);
		//发送请求到服务端，请求uid信息
    	UidbackRspBO uidbackrsp = service.call("users.getUidback", requestBO, UidbackRspBO.class);
    	logger.info("End call getUidback. uidbackrsp is {}",uidbackrsp);
		return new ApiResponse.ApiResponseBuilder().data(uidbackrsp).code(200).message("Uid info").build();
	 }

    /**
     * 功能描述：向表user_profile中插入记录，获取生成uid，并向表sso_user_relation插入记录
     * @param requestVO
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.addUserprofile")
    @ResponseBody
    public ApiResponse addUserprofile(UidbackRequestVO requestVO) throws GatewayException {
		logger.info("Begin call addUserprofile. requestVO is {}",requestVO);
		UidbackRequestBO requestBO = new UidbackRequestBO();
		BeanUtils.copyProperties(requestVO, requestBO);
		UidbackRspBO uidbackrsp = service.call("users.addUserprofile", requestBO, UidbackRspBO.class);
		logger.info("End call addUserprofile. uidbackrsp is {}",uidbackrsp);
		return new ApiResponse.ApiResponseBuilder().data(uidbackrsp).code(200).message("Uid info").build();
	 }

}
