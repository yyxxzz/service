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
import com.yoho.gateway.model.request.UserHabitsVO;
import com.yoho.service.model.profile.UserHabitsBO;
import com.yoho.service.model.response.CommonRspBO;

@Controller
public class UserHabitsController {
	private Logger logger = LoggerFactory.getLogger(UserHabitsController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	// 修改购物&着装习惯服务
	private final static String MODIFY_HABITS_SERVICE = "users.modifyUserHabits";

	// 获取购物&着装习惯服务
	private final static String GET_HABITS_SERVICE = "users.getUserHabits";

	@RequestMapping(params = "method=web.passport.getUserHabits")
	@ResponseBody
	public ApiResponse getUserHabits(UserHabitsVO userHabitsVO) {
		logger.debug("Enter gateway UserHabitsController.getUserHabits. param userHabitsVO is {}", userHabitsVO);

		// (1)参数初始化
		UserHabitsBO userHabitsBO = new UserHabitsBO();
		userHabitsBO.setUid(userHabitsVO.getUid());

		// (2)调用服务，获取用户购物&着装习惯
		userHabitsBO = serviceCaller.call(GET_HABITS_SERVICE, userHabitsBO, UserHabitsBO.class);

		// (3)返回
		userHabitsVO = new UserHabitsVO();
		userHabitsVO.setDress(userHabitsBO.getDress());
		userHabitsVO.setShopping(userHabitsBO.getShopping());
		userHabitsVO.setUid(userHabitsBO.getUid());
		return new ApiResponse.ApiResponseBuilder().code(200).message("成功").data(userHabitsVO).build();
	}

	@RequestMapping(params = "method=web.passport.modifyUserHabits")
	@ResponseBody
	public ApiResponse modifyUserHabits(UserHabitsVO userHabitsVO) {
		logger.debug("Enter gateway UserHabitsController.modifyUserHabits. param userHabitsVO is {}", userHabitsVO);

		// (1)参数初始化
		UserHabitsBO userHabitsBO = new UserHabitsBO();
		userHabitsBO.setDress(userHabitsVO.getDress());
		userHabitsBO.setShopping(userHabitsVO.getShopping());
		userHabitsBO.setUid(userHabitsVO.getUid());

		// (2)调用服务，修改用户购物&着装习惯
		serviceCaller.call(MODIFY_HABITS_SERVICE, userHabitsBO, CommonRspBO.class);
		return new ApiResponse.ApiResponseBuilder().code(200).message("修改成功").data(new JSONObject()).build();
	}

}
