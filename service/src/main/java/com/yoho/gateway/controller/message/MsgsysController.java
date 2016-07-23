package com.yoho.gateway.controller.message;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Strings;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.push.GetuiTokenBO;
import com.yoho.service.model.push.IosTokenBO;
import com.yoho.service.model.push.request.OpenCountReqBO;

/**
 * 更新 android 和 ios token表
 * <p>
 * Created by ming on 16/2/16.
 */
@Controller
public class MsgsysController {
	static Logger logger = LoggerFactory.getLogger(MsgsysController.class);

	private static final String IOS_MSG = "IOS MSG";
	private static final String ANDROID_MSG = "ANDROID MSG";
	private static final String ADD_OPENCOUNT = "ADD_OPENCOUNT";

	@Autowired
	private ServiceCaller serviceCaller;

	/**
	 * 增加消息的打开数
	 * 
	 * @param app_version
	 * @param analysis_platform
	 * @param uid
	 * @param msgid
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=app.msgsys.addOpenCount")
	@ResponseBody
	public ApiResponse addOpenCount(@RequestParam(name = "app_version", defaultValue = "") String app_version,
			@RequestParam(name = "analysis_platform", defaultValue = "ios") String analysis_platform, @RequestParam(name = "uid", defaultValue = "0") int uid,
			@RequestParam(name = "msgid", defaultValue = "0") int msgid) throws ServiceException {
		logger.info("Begin call addOpenCount, app_version={}, analysis_platform={},uid={},msgid={}", app_version, analysis_platform, uid, msgid);
		OpenCountReqBO openCountReqBO = new OpenCountReqBO();
		openCountReqBO.setId(msgid);
		serviceCaller.call("message.addOpenCount", openCountReqBO, Void.class);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().code(200).message(ADD_OPENCOUNT).build();
		return response;
	}

	/**
	 * 绑定 Ios 设备与用户
	 *
	 * @param
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=app.msgsys.getIosUser")
	@ResponseBody
	public ApiResponse getIosUser(@RequestParam("app_version") String app_version, @RequestParam("token") String token, @RequestParam("uid") int uid) throws ServiceException {
		logger.info("Begin call getIosUser, app_version={}, uid={}, token={}", app_version, uid, token);
		if (Strings.isNullOrEmpty(token)) {
			ApiResponse response = new ApiResponse.ApiResponseBuilder().code(412).message("token不能为空").build();

			return response;
		}
		// 1. 判断是否已存在此 token
		IosTokenBO iosTokenBO = serviceCaller.call("message.getIosByToken", token, IosTokenBO.class);

		int uid_t;
		int msg_status;

		// 2. 如果不存在
		if (null == iosTokenBO) {
			uid_t = uid > 0 ? uid : 0;
			IosTokenBO newToken = new IosTokenBO();
			newToken.setToken(token);
			newToken.setUid(uid_t);
			newToken.setCreateTime((int) (System.currentTimeMillis() / 1000L));
			newToken.setVersion(app_version);
			serviceCaller.call("message.setIosToken", newToken, Void.class);
			msg_status = 1;
		} else {
			// 3. 如果存在
			uid_t = uid > 0 ? uid : iosTokenBO.getUid();
			iosTokenBO.setUid(uid_t);
			serviceCaller.call("message.updateIosToken", iosTokenBO, Void.class);
			msg_status = iosTokenBO.getStatus();
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("msg_status", String.valueOf(msg_status));

		ApiResponse response = new ApiResponse.ApiResponseBuilder().code(200).message(IOS_MSG).data(map).build();

		return response;
	}

	/**
	 * 绑定 Android 个推设备与用户
	 *
	 * @param
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=app.Msgsys.getAndroidUser")
	@ResponseBody
	public ApiResponse getAndroidUser(@RequestParam("app_version") String app_version, @RequestParam("token") String token, @RequestParam("uid") int uid) throws ServiceException {
		logger.info("Begin call getAndroidUser, app_version={}, uid={}, token={}", app_version, uid, token);

		if (Strings.isNullOrEmpty(token)) {
			ApiResponse response = new ApiResponse.ApiResponseBuilder().code(412).message("token不能为空").build();

			return response;
		}
		// 1. 判断是否已存在此 token
		GetuiTokenBO getuiTokenBO = serviceCaller.call("message.getGetuiByToken", token, GetuiTokenBO.class);

		int uid_t;
		int msg_status;

		// 2. 如果不存在
		if (null == getuiTokenBO) {
			uid_t = uid > 0 ? uid : 0;
			GetuiTokenBO newToken = new GetuiTokenBO();
			newToken.setGetuiCid(token);
			newToken.setUid(uid_t);
			newToken.setCreateTime((int) (System.currentTimeMillis() / 1000L));
			newToken.setVersion(app_version);
			serviceCaller.call("message.setGetuiToken", newToken, Void.class);
			msg_status = 1;
		} else {
			// 3. 如果存在
			uid_t = uid > 0 ? uid : getuiTokenBO.getUid();
			getuiTokenBO.setUid(uid_t);
			serviceCaller.call("message.updateGetuiToken", getuiTokenBO, Void.class);
			msg_status = (null == getuiTokenBO.getStatus()) ? 0 : getuiTokenBO.getStatus();
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("msg_status", String.valueOf(msg_status));

		ApiResponse response = new ApiResponse.ApiResponseBuilder().code(200).message(ANDROID_MSG).data(map).build();

		return response;
	}

	@RequestMapping(params = "method=app.msgsys.pushIosUser")
	@ResponseBody
	public ApiResponse pushIosUser(@RequestParam("app_version") String app_version, @RequestParam("token") String token, @RequestParam("uid") int uid,
			@RequestParam("status") int status) throws ServiceException {
		logger.info("Begin call pushIosUser, app_version={}, uid={}, token={}", app_version, uid, token);
		if (Strings.isNullOrEmpty(token)) {
			ApiResponse response = new ApiResponse.ApiResponseBuilder().code(412).message("token不能为空").build();

			return response;
		}

		IosTokenBO iosTokenBO = new IosTokenBO();
		iosTokenBO.setToken(token);
		iosTokenBO.setUid(uid > 0 ? uid : 0);
		iosTokenBO.setStatus((byte) status);
		int suc = serviceCaller.call("message.publishIosUser", iosTokenBO, Integer.class);

		if (suc != 1) {
			ApiResponse response = new ApiResponse.ApiResponseBuilder().code(500).message("推送消息设置失败!").build();
			return response;
		}

		ApiResponse response = new ApiResponse.ApiResponseBuilder().code(200).message("推送消息设置成功!").build();

		return response;

	}

	@RequestMapping(params = "method=app.Msgsys.pushAndroidUser")
	@ResponseBody
	public ApiResponse pushAndroidUser(@RequestParam("app_version") String app_version, @RequestParam("token") String token, @RequestParam("uid") int uid,
			@RequestParam("status") int status) throws ServiceException {
		logger.info("Begin call pushAndroidUser, app_version={}, uid={}, token={}", app_version, uid, token);
		if (Strings.isNullOrEmpty(token)) {
			ApiResponse response = new ApiResponse.ApiResponseBuilder().code(412).message("token不能为空").build();

			return response;
		}

		GetuiTokenBO getuiTokenBO = new GetuiTokenBO();
		getuiTokenBO.setGetuiCid(token);
		getuiTokenBO.setUid(uid > 0 ? uid : 0);
		getuiTokenBO.setStatus((byte) status);
		int suc = serviceCaller.call("message.publishGetuiUser", getuiTokenBO, Integer.class);

		if (suc != 1) {
			ApiResponse response = new ApiResponse.ApiResponseBuilder().code(500).message("推送消息设置失败!").build();
			return response;
		}

		ApiResponse response = new ApiResponse.ApiResponseBuilder().code(200).message("推送消息设置成功!").build();

		return response;
	}
}
