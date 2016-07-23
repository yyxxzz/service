package com.yoho.gateway.controller.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.service.model.request.HelpReqBO;
import com.yoho.service.model.response.HelpCategoryRspBO;
import com.yoho.service.model.response.HelpContentRspBO;

@Controller
@RequestMapping(value = "/operations/api/*/help")
public class 	HelpController {

	private Logger logger = LoggerFactory.getLogger(HelpController.class);

	@Autowired
	ServiceCaller serviceCaller;

	private static final String HELP_CATEGORYLIST_SERVICE = "users.getHelpCategory";
	private static final int HELP_CATEGORYLIST_CODE = 200;
	private static final String HELP_CATEGORYLIST_MSG = "help category";

	// 帮助内容列表
	private static final String HELP_HELPCONTENTLIST_SERVICE = "users.getHelp";
	private static final int HELP_HELPCONTENTLIST_CODE = 200;
	private static final String HELP_HELPCONTENTLIST_MSG = "帮助列表";

	// 帮助内容
	private static final String HELP_HELPCONTENT_SERVICE = "users.getHelpContent";
	private static final int HELP_HELPCONTENT_CODE = 200;
	private static final String HELP_HELPCONTENT_MSG = "帮助内容";

	/**
	 * 获取帮助分类列表
	 * 
	 * @param suggestQueryReqVO
	 * @return
	 */
	@RequestMapping(value = "/getCategory")
	@ResponseBody
	public ApiResponse getCategory(HelpReqBO helpReqBO) throws GatewayException{
		logger.info("Enter HelpController.getCategory. helpReqBO is {}", helpReqBO);

		// (1)请求服务
		HelpCategoryRspBO[] helpCategoryList = serviceCaller.call(HELP_CATEGORYLIST_SERVICE, helpReqBO, HelpCategoryRspBO[].class);
		// (2)返回
		return new ApiResponse.ApiResponseBuilder().code(HELP_CATEGORYLIST_CODE).message(HELP_CATEGORYLIST_MSG).data(helpCategoryList).build();
	}

	/**
	 * 获取帮助内容列表
	 * 
	 * @param helpReqBO
	 * @return
	 */
	@RequestMapping(value = "/getHelp")
	@ResponseBody
	public ApiResponse getHelp(HelpReqBO helpReqBO) throws GatewayException{
		logger.info("Enter HelpController.getHelp. param helpReqBO is {}", helpReqBO);

		// (1)请求服务
		HelpContentRspBO[] helpContentRspBOArr = serviceCaller.call(HELP_HELPCONTENTLIST_SERVICE, helpReqBO, HelpContentRspBO[].class);

		// (2)返回
		return new ApiResponse.ApiResponseBuilder().code(HELP_HELPCONTENTLIST_CODE).message(HELP_HELPCONTENTLIST_MSG).data(helpContentRspBOArr).build();
	}
	
	/**
	 * 获取帮助内容
	 * 
	 * @param helpReqBO
	 * @return
	 */
	@RequestMapping(value = "/getHelpContent")
	@ResponseBody
	public ApiResponse getHelpContent(HelpReqBO helpReqBO) throws GatewayException{
		logger.info("Enter HelpController.getHelpContent. param helpReqBO is {}", helpReqBO);

		// (1)请求服务
		HelpContentRspBO helpContentRspBO = serviceCaller.call(HELP_HELPCONTENT_SERVICE, helpReqBO, HelpContentRspBO.class);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("id", helpContentRspBO.getId());
		jsonObject.put("content", helpContentRspBO.getContent());
		jsonObject.put("title", helpContentRspBO.getTitle());
		
		// (2)返回
		return new ApiResponse.ApiResponseBuilder().code(HELP_HELPCONTENT_CODE).message(HELP_HELPCONTENT_MSG).data(jsonObject).build();
	}
}
