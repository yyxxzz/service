package com.yoho.gateway.controller.sns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.service.sns.UserAttentionService;

@Controller
@RequestMapping("/guang/api")
public class UserAttentionController {

	private static Logger logger = LoggerFactory.getLogger(UserAttentionController.class);
	
	@Autowired
	private UserAttentionService userAttentionService;

	@RequestMapping("/*/attention/getlist")
	@ResponseBody
	public ApiResponse getAttentionList(@RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "") String gender,
			@RequestParam(defaultValue = "H5") String client_type, @RequestParam(defaultValue = "0") Integer uid) {
		logger.info("enter getAttentionList. param  page is {}, gender is {},client_type is {}, uid is {}, ", page, gender, client_type, uid);
		JSONObject result = userAttentionService.getAttentionList(page, gender, client_type, uid);
		return new ApiResponse.ApiResponseBuilder().code(200).message("关注列表").data(result).build();
	}
	
}
