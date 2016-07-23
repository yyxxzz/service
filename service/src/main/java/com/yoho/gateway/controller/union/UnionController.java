package com.yoho.gateway.controller.union;

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
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.gateway.model.union.request.ActiveUnionRequestVO;
import com.yoho.gateway.model.union.request.AddUnionRequestVO;
import com.yoho.gateway.model.union.response.UnionResponseVO;
import com.yoho.service.model.union.request.ActiveUnionRequestBO;
import com.yoho.service.model.union.request.AddUnionRequestBO;
import com.yoho.service.model.union.response.UnionResponseBO;

@Controller
public class UnionController {

	static Logger log = LoggerFactory.getLogger(UnionController.class);
	
	@Resource
	ServiceCaller serviceCaller;
	
	/**
	 * 联盟调用接口
	 * @param vo
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping("/addUnion")
	@ResponseBody
	public UnionResponseVO addUnion(AddUnionRequestVO vo) throws GatewayException {
		log.debug("enter addUnion");
		log.info("Begin call addUnion gateway. with param is {}", vo);
		AddUnionRequestBO bo = new AddUnionRequestBO();
		BeanUtils.copyProperties(vo, bo);
		UnionResponseBO responseBO = serviceCaller.call("union.addUnion", bo, UnionResponseBO.class);
		UnionResponseVO responseVO = new UnionResponseVO();
		BeanUtils.copyProperties(responseBO, responseVO);
		return responseVO;
	}
	
	/**
	 * 联盟激活接口
	 * @param vo
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.dingdang.activeUnion")
	@ResponseBody
	public ApiResponse activeUnion(ActiveUnionRequestVO vo) throws GatewayException {
		log.debug("enter activeUnion");
		log.info("Begin call activeUnion gateway. with param is {}", vo);
		ActiveUnionRequestBO bo = new ActiveUnionRequestBO();
		BeanUtils.copyProperties(vo, bo);
		bo.setClientIP(RemoteIPInterceptor.getRemoteIP());
		UnionResponseBO responseBO = serviceCaller.call("union.activeUnion", bo, UnionResponseBO.class);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("success").data(responseBO).build();
		return response;
	}
	
	/**
	 * 这个接口已经在广点通实现，所以写一个空方法
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=union.analytics.pairui")
	@ResponseBody
	public ApiResponse pairui() throws GatewayException{
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("success").build();
		return response;
	}
}
