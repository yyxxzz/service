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
import com.yoho.gateway.helper.MobileHelper;
import com.yoho.gateway.model.request.UserContactsVO;
import com.yoho.service.model.profile.UserContactsBO;
import com.yoho.service.model.response.CommonRspBO;

@Controller
public class UserContactsController {
	private Logger logger = LoggerFactory.getLogger(UserContactsController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	// 修改联系信息服务
	private final static String MODIFY_CONTACTS_SERVICE = "users.modifyUserContacts";

	// 获取联系信息服务
	private final static String GET_CONTACTS_SERVICE = "users.getUserContacts";

	@RequestMapping(params = "method=web.passport.getUserContacts")
	@ResponseBody
	public ApiResponse getUserContacts(UserContactsVO userContactsVO) {
		logger.debug("Enter gateway UserContactsController.getUserContacts. param userContactsVO is {}", userContactsVO);

		// (1)参数初始化
		UserContactsBO userContactsBO = new UserContactsBO();
		userContactsBO.setUid(userContactsVO.getUid());

		// (2)调用服务，获取用户联系信息
		userContactsBO = serviceCaller.call(GET_CONTACTS_SERVICE, userContactsBO, UserContactsBO.class);

		// (3)返回
		userContactsVO = new UserContactsVO();
		userContactsVO.setArea_code(userContactsBO.getCodeAddress());
		userContactsVO.setFull_address(userContactsBO.getFullAddress());
		userContactsVO.setMobile(MobileHelper.coverMobile(userContactsBO.getMobile()));
		userContactsVO.setMsn(userContactsBO.getMsn());
		userContactsVO.setPhone(MobileHelper.coverMobile(userContactsBO.getPhone()));
		userContactsVO.setQq(userContactsBO.getQq());
		userContactsVO.setUid(userContactsBO.getUid());
		userContactsVO.setZip_code(userContactsBO.getZipCode());
		return new ApiResponse.ApiResponseBuilder().code(200).message("成功").data(userContactsVO).build();
	}

	@RequestMapping(params = "method=web.passport.modifyUserContacts")
	@ResponseBody
	public ApiResponse modifyUserContacts(UserContactsVO userContactsVO) {
		logger.debug("Enter gateway UserContactsController.modifyUserContacts. param userContactsVO is {}", userContactsVO);

		// (1)参数初始化
		UserContactsBO userContactsBO = new UserContactsBO();
		userContactsBO.setCodeAddress(userContactsVO.getArea_code());
		userContactsBO.setFullAddress(userContactsVO.getFull_address());
		userContactsBO.setMobile(userContactsVO.getMobile());
		userContactsBO.setMsn(userContactsVO.getMsn());
		userContactsBO.setPhone(userContactsVO.getPhone());
		userContactsBO.setQq(userContactsVO.getQq());
		userContactsBO.setUid(userContactsVO.getUid());
		userContactsBO.setZipCode(userContactsVO.getZip_code());

		// (2)调用服务，修改用户联系信息
		serviceCaller.call(MODIFY_CONTACTS_SERVICE, userContactsBO, CommonRspBO.class);
		return new ApiResponse.ApiResponseBuilder().code(200).message("修改成功").data(new JSONObject()).build();
	}

}
