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
import com.yoho.gateway.model.request.LikeBrandVO;
import com.yoho.service.model.profile.LikeBrandBO;
import com.yoho.service.model.response.CommonRspBO;

@Controller
public class LikeBrandController {
	private Logger logger = LoggerFactory.getLogger(LikeBrandController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	// 修改喜爱品牌服务
	private final static String MODIFY_LIKEBRAND_SERVICE = "users.modifyLikeBrand";

	// 获取喜爱品牌服务
	private final static String GET_LIKEBRAND_SERVICE = "users.getLikeBrand";

	@RequestMapping(params = "method=web.passport.getLikeBrand")
	@ResponseBody
	public ApiResponse getLikeBrand(LikeBrandVO likeBrandVO) {
		logger.debug("Enter gateway LikeBrandController.getLikeBrand. param likeBrandVO is {}", likeBrandVO);

		// (1)参数初始化
		LikeBrandBO likeBrandBO = new LikeBrandBO();
		likeBrandBO.setUid(likeBrandVO.getUid());

		// (2)调用服务，获取用户喜爱品牌
		likeBrandBO = serviceCaller.call(GET_LIKEBRAND_SERVICE, likeBrandBO, LikeBrandBO.class);

		// (3)返回
		likeBrandVO = new LikeBrandVO();
		likeBrandVO.setBrand(likeBrandBO.getBrand());
		likeBrandVO.setUid(likeBrandBO.getUid());
		return new ApiResponse.ApiResponseBuilder().code(200).message("成功").data(likeBrandVO).build();
	}

	@RequestMapping(params = "method=web.passport.modifyLikeBrand")
	@ResponseBody
	public ApiResponse modifyLikeBrand(LikeBrandVO likeBrandVO) {
		logger.debug("Enter gateway LikeBrandController.modifyLikeBrand. param likeBrandVO is {}", likeBrandVO);

		// (1)参数初始化
		LikeBrandBO likeBrandBO = new LikeBrandBO();
		likeBrandBO.setUid(likeBrandVO.getUid());
		likeBrandBO.setBrand(likeBrandVO.getBrand());

		// (2)调用服务，修改用户喜爱品牌
		serviceCaller.call(MODIFY_LIKEBRAND_SERVICE, likeBrandBO, CommonRspBO.class);
		return new ApiResponse.ApiResponseBuilder().code(200).message("修改成功").data(new JSONObject()).build();
	}

}
