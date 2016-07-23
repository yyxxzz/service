package com.yoho.gateway.controller.users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.user.sign.UserSignInfoRspVO;
import com.yoho.gateway.model.user.sign.UserSignRspVO;
import com.yoho.gateway.service.PushTokenService;
import com.yoho.gateway.service.user.UserSignService;
import com.yoho.service.model.response.CommonRspBO;

@Controller
public class UserSignController {

	private Logger logger = LoggerFactory.getLogger(UserSignController.class);

	@Autowired
	private UserSignService userSignService;
	@Autowired
	private PushTokenService pushTokenService;

	/**
	 * 获取用户签到信息接口
	 * 
	 * @param uid
	 * @return
	 */
	@RequestMapping(params = "method=app.sign.userSignInfo")
	@ResponseBody
	public ApiResponse getUserSignInfo(Integer uid) {
		logger.info("Enter gateway UserSignController.getUserSignInfo. param uid is {}", uid);
		UserSignInfoRspVO vo = userSignService.getUserSignInfo(uid);
		return new ApiResponse.ApiResponseBuilder().code(200).message("获取签到信息成功").data(vo).build();
	}

	/**
	 * 用户签到接口
	 * 
	 * @param uid
	 * @return
	 */
	@RequestMapping(params = "method=app.sign.userSign")
	@ResponseBody
	public ApiResponse userSign(Integer uid) {
		logger.info("Enter gateway UserSignController.userSign. param uid is {}", uid);
		UserSignRspVO vo = userSignService.userSign(uid);
		return new ApiResponse.ApiResponseBuilder().code(200).message("用户签到成功").data(vo).build();
	}

	/**
	 * 修改签到的推送标识位
	 * 
	 * @param uid
	 * @param pushFlag
	 * @return
	 */
	@RequestMapping(params = "method=app.sign.changePushFlag")
	@ResponseBody
	public ApiResponse changeUserSignPushFlag(@RequestParam(value = "uid", required = true) Integer uid, @RequestParam(value = "pushFlag", required = true) Integer pushFlag,
			@RequestParam(value = "token", required = false, defaultValue = "") String token) {
		logger.info("Enter gateway UserSignController.changeUserSignPushFlag. param uid is {},pushFlag is {},token is{}", uid, pushFlag, token);
		CommonRspBO commonRspBO = userSignService.changeUserSignPushFlag(uid, pushFlag);
		// 如果打开，则强制更新这个TOKEN状态为1
		if (pushFlag != null && pushFlag > 0) {
			pushTokenService.updateUserTokenStatusOpen(String.valueOf(uid), token);
		}
		return new ApiResponse.ApiResponseBuilder().code(commonRspBO.getCode()).message("修改成功").build();
	}

}
