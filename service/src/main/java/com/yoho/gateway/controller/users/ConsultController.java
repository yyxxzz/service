package com.yoho.gateway.controller.users;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.user.percenter.ConsultReqVO;
import com.yoho.service.model.request.MyConsultReqBO;
import com.yoho.service.model.response.MyConsultRspBO;
import com.yoho.service.model.response.PageResponseBO;

/**
 * 购买咨询
 * @author yoho
 *
 */
@Controller("ConsultController")
public class ConsultController {
	
	@Resource
	ServiceCaller serviceCaller;
	
	private static final String MY_CONSULT_LIST = "my consult list";
	
	private Logger logger = LoggerFactory.getLogger(ConsultController.class);
	
	@RequestMapping(params = "method=web.personCen.buyConsult")
	@ResponseBody
	public ApiResponse buyConsult(ConsultReqVO consultReqVO) throws ServiceException{
		logger.info("Enter ConsultController.buyConsult.param is {} ",consultReqVO);
		
		MyConsultReqBO myConsultReqBO = new MyConsultReqBO();
		myConsultReqBO.setUid(consultReqVO.getUid());
		myConsultReqBO.setSize(consultReqVO.getLimit());
		myConsultReqBO.setPage(consultReqVO.getPage());
		
		//调用服务
		PageResponseBO<MyConsultRspBO> result = serviceCaller.call("users.getConsultListByAskUserId", myConsultReqBO, PageResponseBO.class);
		logger.debug("call users.getConsultListByAskUserId with param is{},with result is{}",consultReqVO,result);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("page_total", result.getPage_total());
		map.put("page", result.getPage());
		map.put("total", result.getTotal());
		map.put("consult_list", result.getList());
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(MY_CONSULT_LIST).data(map).build();
		return response;
	}
	
}
