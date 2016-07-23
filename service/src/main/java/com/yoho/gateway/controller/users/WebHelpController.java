package com.yoho.gateway.controller.users;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import com.yoho.gateway.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.user.percenter.WebHelpDetailReqVO;
import com.yoho.service.model.request.WebHelpDetailReqBO;
import com.yoho.service.model.request.WebHelpReqBO;
import com.yoho.service.model.response.PageResponseBO;
import com.yoho.service.model.response.WebHelpDetailRspBO;
import com.yoho.service.model.response.WebHelpRepBO;

@Controller
public class WebHelpController {
	private static final Logger logger = LoggerFactory.getLogger(WebHelpController.class);
	
	@Resource
	ServiceCaller serviceCaller;

	// CategoryId不能为空
	private final static int MODIFY_HEAD_CATEGORYID_NULL_CODE = 401;
	private final static String MODIFY_HEAD_CATEGORYID_NULL_MSG = "CategoryId Is Null.";
	
	@RequestMapping(params="method=web.help.category")
	@ResponseBody
    public ApiResponse webHelpCategory(WebHelpReqBO vo) throws GatewayException{

        logger.info("enter WebHelpController.webHelpCategory param {} is",vo);

        WebHelpReqBO bo = new WebHelpReqBO();
        bo.setCaption(vo.getCaption());
        //请求服务
        WebHelpRepBO[] webHelpRepBO = serviceCaller.call("users.getCategoryList", bo, WebHelpRepBO[].class);

        //返回
        return new ApiResponse.ApiResponseBuilder().data(webHelpRepBO).build();
    }
	
	@RequestMapping(params="method=web.help.getHelpDetailList")
	@ResponseBody
	public ApiResponse webHelpDetailList(WebHelpDetailReqVO vo) throws GatewayException{
		logger.info("enter WebHelpController.webHelpDetailList param {} is",vo);
		
		WebHelpDetailReqBO bo = new WebHelpDetailReqBO();
		String categoryId = vo.getCategory_id();
		if(StringUtils.isEmpty(categoryId)||!categoryId.matches("[0-9]+")){
			throw new GatewayException(MODIFY_HEAD_CATEGORYID_NULL_CODE,MODIFY_HEAD_CATEGORYID_NULL_MSG);
		}
		bo.setCategory_id(vo.getCategory_id());
		bo.setLimit(vo.getLimit());
		bo.setPage(vo.getPage());
		bo.setProblem(vo.getProblem());
		
		//调用服务
		PageResponseBO<WebHelpDetailRspBO> result = serviceCaller.call("users.getHelpDetailList", bo, PageResponseBO.class);
		logger.debug("call users.getHelpDetailList with param is{},with result is{}",bo,result);				
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("page_total", result.getPage_total());
		map.put("page", result.getPage());
		map.put("total", result.getTotal());
		map.put("helpdetail_list", result.getList());
		ApiResponse response = new ApiResponse.ApiResponseBuilder().data(map).build();
		return response;
	}

	@RequestMapping(params="method=web.help.getCommonFaqList")
	@ResponseBody
	public ApiResponse webCommonFaqList(WebHelpDetailReqVO vo) throws GatewayException{
		logger.info("enter WebHelpController.webCommonFaqList param {} is",vo);
		
		WebHelpDetailReqBO bo = new WebHelpDetailReqBO();
		String categoryId = vo.getCategory_id();
		if(StringUtils.isEmpty(categoryId)||!categoryId.matches("[0-9]+")){
			throw new GatewayException(MODIFY_HEAD_CATEGORYID_NULL_CODE,MODIFY_HEAD_CATEGORYID_NULL_MSG);
		}
		bo.setCategory_id(vo.getCategory_id());
		bo.setProblem(vo.getProblem());
		//请求服务
		WebHelpDetailRspBO[] webHelpDetailRspBO=serviceCaller.call("users.getCommonFaqList", bo, WebHelpDetailRspBO[].class);
		
		 //返回
        return new ApiResponse.ApiResponseBuilder().data(webHelpDetailRspBO).build();
	}

}
