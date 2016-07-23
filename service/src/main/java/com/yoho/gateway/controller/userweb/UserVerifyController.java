package com.yoho.gateway.controller.userweb;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.gateway.model.request.ChangeBindVO;
import com.yoho.gateway.model.request.SmsbindVO;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.gateway.utils.PhoneUtil;
import com.yoho.gateway.utils.constants.Constants;
import com.yoho.service.model.consts.Constant;
import com.yoho.service.model.request.ChangePwdRequestBO;
import com.yoho.service.model.request.DESRequestBO;
import com.yoho.service.model.request.ProfileRequestBO;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.response.ProfileInfoRsp;
import com.yoho.service.model.sms.request.SMSTemplateReqBO;
import com.yoho.service.model.sms.response.SMSRspModel;
import com.yoho.service.model.users.request.UserRequestBO;
import com.yoho.service.model.users.response.userverify.UserVerifyResponseBO;

@Controller
public class UserVerifyController {

	@Resource
	private ServiceCaller serviceCaller;

	@Resource(name = "yhRedisTemplate")
	private YHRedisTemplate<String, String> yhRedisTemplate;

	@Autowired
	private YHValueOperations<String, String> yhValueOperations;

	static Logger log = LoggerFactory.getLogger(UserVerifyController.class);

	private static final int MOBILE_IS_NULL_CODE = 401;
	private static final String MOBILE_IS_NULL_MESSAGE = "手机号码错误";
	private static final int MOBILE_IS_ERROR_CODE = 402;
	private static final String MOBILE_IS_ERROR_MESSAGE = "手机号码格式错误";
	private static final String SMS_TEMPLATE_BIND = "appbind";

	/**
	 * 检查该邮箱是否可以验证
	 * @param uid
	 * @param email
	 * @param callback
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.checkVerifyEmail")
	@ResponseBody
	public ApiResponse checkVerifyEmail(int uid, String email, String callback) throws GatewayException {
		log.debug("Begin call checkVerifyEmail controller. uid = {}, email={}", uid, email);
		ApiResponse apiResponse = null;

		// 发送请求到服务端
		ProfileRequestBO bo = new ProfileRequestBO();
		bo.setEmail(email);
		bo.setUid(uid);
		serviceCaller.call("users.checkVerifyEmail", bo, CommonRspBO.class);

		// 组装返回参数
		apiResponse = new ApiResponse.ApiResponseBuilder().build();
		return apiResponse;
	}
	
	/**
	 * 验证邮箱，并且发送验证邮件
	 * 
	 * @param uid
	 * @param email
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.verifyEmail")
	@ResponseBody
	public ApiResponse verifyEmail(int uid, String email, String callback) throws GatewayException {
		log.debug("Begin call verifyEmail controller. uid = {}, email={}", uid, email);
		ApiResponse apiResponse = null;

		// 发送请求到服务端
		ProfileRequestBO bo = new ProfileRequestBO();
		bo.setEmail(email);
		bo.setUid(uid);
		bo.setCallback(callback);
		serviceCaller.call("users.verifyEmail", bo, CommonRspBO.class);

		// 组装返回参数
		apiResponse = new ApiResponse.ApiResponseBuilder().build();
		return apiResponse;
	}

	/**
	 * 修改验证邮箱
	 * 
	 * @param code
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.changeVerifyEmail")
	@ResponseBody
	public ApiResponse changeVerifyEmail(String code) throws GatewayException {
		log.info("changeVerifyEmail with code={}", code);
		try {
			if (StringUtils.isEmpty(code)) {
				log.warn("changeVerifyEmail with empty code");
				throw new ServiceException(ServiceError.CODE_IS_EMPTY);
			}

			DESRequestBO desRequestBO = new DESRequestBO();
			desRequestBO.setCode(code);
			String desCode = serviceCaller.call("users.desDecrypt", desRequestBO, String.class);
			JSONObject json = JSONObject.parseObject(desCode);
			int uid = json.getIntValue("uid");
			int time = json.getIntValue("time");
			String email = json.getString("email");
			// 验证邮件发送超过一天都没有打开，则验证信息无效
			if (uid == 0 || time + 86400 < DateUtil.getCurrentTimeSecond()) {
				log.warn("changeVerifyEmail time over with code={}, uid={}, time={}, email={}", code, uid, time, email);
				throw new ServiceException(ServiceError.CODE_IS_INVALID);
			}

			ProfileRequestBO bo = new ProfileRequestBO();
			bo.setUid(uid);
			bo.setEmail(email);
			serviceCaller.call("users.changeVerifyEmail", bo, CommonRspBO.class);
		} catch (Exception e) {
			log.error("changeVerifyEmail error with code=" + code, e);
			throw new ServiceException(ServiceError.CODE_IS_INVALID);
		}
		return new ApiResponse.ApiResponseBuilder().message(Constant.DEFAULT_SUCCESS_MESSAGE).build();
	}

	/**
	 * 获取用户的验证信息
	 * 
	 * @param uid
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.getUserVerifyInfo")
	@ResponseBody
	public ApiResponse getUserVerifyInfo(String uid) throws GatewayException {
		log.info("Begin call getUserVerifyInfo controller. uid = {}, ", uid);

		if (StringUtils.isEmpty(uid) || !uid.matches("\\d+")) {
			log.warn("getUserVerifyInfo error with uid is {}", uid);
			throw new ServiceException(ServiceError.BROWSE_DEL_UID_ISNULL);
		}
		// 发送请求到服务端
		UserRequestBO bo = new UserRequestBO();
		bo.setUid(Integer.parseInt(uid));
		UserVerifyResponseBO resp = serviceCaller.call("users.getUserVerifyInfo", bo, UserVerifyResponseBO.class);

		// 组装返回参数
		return new ApiResponse.ApiResponseBuilder().data(resp).build();
	}

	/**
	 * 验证用户输入密码是否正确
	 * 
	 * @param uid
	 * @param password
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.verifyUserPwd")
	@ResponseBody
	public ApiResponse verifyUserPwd(String uid, String password) throws GatewayException {
		log.info("Begin call verifyUserPwd controller. uid = {}, ", uid);

		if (StringUtils.isEmpty(uid) || !uid.matches("\\d+")) {
			log.warn("getUserVerifyInfo error with uid is {}", uid);
			throw new ServiceException(ServiceError.BROWSE_DEL_UID_ISNULL);
		}
		// 发送请求到服务端
		UserRequestBO bo = new UserRequestBO();
		bo.setUid(Integer.parseInt(uid));
		bo.setPassword(password);
		CommonRspBO resp = serviceCaller.call("users.verifyUserPwd", bo, CommonRspBO.class);

		// 组装返回参数
		return new ApiResponse.ApiResponseBuilder().data(resp).build();
	}

	/**
	 * 修改用户密码
	 * 
	 * @param uid
	 * @param newPassword
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.changePwd")
	@ResponseBody
	public ApiResponse changePwd(String uid, String newPassword) throws GatewayException {
		log.info("Begin call changePwd controller. uid = {}, ", uid);
		if (StringUtils.isEmpty(newPassword)) {
			log.warn("changePwd password is null with uid is {}", uid);
			throw new ServiceException(ServiceError.PARAM_ERROR);
		}
		if (StringUtils.isEmpty(uid) || !uid.matches("\\d+")) {
			log.warn("changePwd uid is {} ", uid);
			throw new ServiceException(ServiceError.BROWSE_DEL_UID_ISNULL);
		}

		ChangePwdRequestBO bo = new ChangePwdRequestBO();
		bo.setUid(Integer.parseInt(uid));
		bo.setNewpwd(newPassword);
		CommonRspBO resp = serviceCaller.call("users.changePwd", bo, CommonRspBO.class);

		// 组装返回参数
		return new ApiResponse.ApiResponseBuilder().data(resp).build();
	}

	/**
	 * 发送短信验证码
	 * 
	 * @param vo
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.sendcode")
	@ResponseBody
	public ApiResponse sendcode(SmsbindVO vo) throws GatewayException {
		log.info("Begin call sendcode gateway. with param is  {}", vo);
		// (1)校验手机号码和国家或者地区码, 而且号码要和地区码匹配
		if (StringUtils.isEmpty(vo.getMobile())) {
			return new ApiResponse.ApiResponseBuilder().code(MOBILE_IS_NULL_CODE).message(MOBILE_IS_NULL_MESSAGE).build();
		}
		String area = StringUtils.defaultString(vo.getArea());
		boolean isMobile = PhoneUtil.areaMobileVerify(area, vo.getMobile());
		if (!isMobile) {
			return new ApiResponse.ApiResponseBuilder().code(MOBILE_IS_ERROR_CODE).message(MOBILE_IS_ERROR_MESSAGE).build();
		}
		if (StringUtils.isEmpty(vo.getUid()) || !vo.getUid().matches("\\d+")) {
			log.warn("sendcode error uid is error with uid={}", vo.getUid());
			throw new ServiceException(ServiceError.BROWSE_DEL_UID_ISNULL);
		}
		
		//检查该手机号与uid是否匹配
		ProfileRequestBO p = new ProfileRequestBO();
		p.setUid(Integer.parseInt(vo.getUid()));
		ProfileInfoRsp resp = serviceCaller.call("users.getProfileByUid", p, ProfileInfoRsp.class);
		//uid与手机号码不符
		if (resp == null) {
			log.warn("sendcode error uid is not match mobile with uid={}, mobile={}, area={}", vo.getUid(), vo.getUid(), vo.getArea());
			throw new ServiceException(ServiceError.UID_NOT_MATCH_MOBILE);
		}
		if (resp.getMobile().indexOf("-") >= 0 && !resp.getMobile().split("-")[1].equals(vo.getMobile())) {
			log.warn("sendcode error uid is not match mobile with uid={}, mobile={}, area={}", vo.getUid(), vo.getUid(), vo.getArea());
			throw new ServiceException(ServiceError.UID_NOT_MATCH_MOBILE);
		}
		

		// (2)根据国家或者地区码组装手机号码, 并生成四位验证码, 将验证码存放在REDIS中.
		String ip = RemoteIPInterceptor.getRemoteIP();
		String mobile1 = PhoneUtil.makePhone(area, vo.getMobile());
		String verifyCode = PhoneUtil.getPhoneVerifyCode();
		log.info("sendcode: mobile is {}, verifyCode is {}", mobile1, verifyCode);
		try {
			yhValueOperations.set(Constants.VERIFY_MOBILE_MEM_KEY + mobile1, verifyCode);
			yhRedisTemplate.longExpire(Constants.VERIFY_MOBILE_MEM_KEY + mobile1, 10, TimeUnit.MINUTES);
		} catch (Exception e) {
			log.warn("sendcode: redis exception. mobile is {}, area is {}, verifyCode is {}. error msg is {}", mobile1, vo.getArea(), verifyCode, e.getMessage());
		}

		// (3)发送短信验证码，模版是
		SMSTemplateReqBO smsRequestBO = new SMSTemplateReqBO(SMS_TEMPLATE_BIND, mobile1, ip, verifyCode, area);
		List<SMSTemplateReqBO> smsTemplateBOList = new ArrayList<SMSTemplateReqBO>();
		smsTemplateBOList.add(smsRequestBO);
		serviceCaller.call("message.smsSend", smsTemplateBOList, SMSRspModel.class);

		// 短信发送成功，删除验证码校验次数
		try {
			yhRedisTemplate.delete(Constants.VERIFY_MOBILE_TIME_MEM_KEY + mobile1);
		} catch (Exception e) {
			log.warn("sendcode. delete message send times. read redis failed. mobile is {}", mobile1);
		}
		return new ApiResponse.ApiResponseBuilder().message("发送成功").build();
	}

	/**
	 * 检查短信验证码
	 * @param vo
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.checkcode")
	@ResponseBody
	public ApiResponse checkcode(ChangeBindVO vo) throws GatewayException {
		log.debug("Begin call checkcode gateway. with mobile={}, code={}", vo.getMobile(), vo.getCode());

		String mobile1 = PhoneUtil.makePhone(vo.getArea(), vo.getMobile());
		// 首先检查验证码是否正确
		// (1)从cache中获取当前手机号码的已经校验验证码的次数，5分钟之内，如果做了15次判断，则直接抛出异常
		try {
			long times = yhValueOperations.increment(Constants.VERIFY_MOBILE_TIME_MEM_KEY + mobile1, 1);
			yhRedisTemplate.longExpire(Constants.VERIFY_MOBILE_TIME_MEM_KEY + mobile1, 5, TimeUnit.MINUTES);
			if (times > 14) {
				log.info("mobile {} validate more than 15 times in 5 minuter. param area is {}, code is {} ", vo.getMobile(), vo.getArea(), vo.getCode());
				throw new ServiceException(ServiceError.VALID_CODE_TIMEOUT_CODE);
			}
		} catch (Exception e) {
			log.warn("checkcode. read redis failed. mobile is {}", mobile1);
			if (e instanceof ServiceException) {
				throw e;
			}
		}

		// (2)校验当前输入的验证码和cache中的验证码比对，是否是正确，
		try {
			String validCode = yhValueOperations.get(Constants.VERIFY_MOBILE_MEM_KEY + mobile1);
			log.info("checkcode mobile is {}, validCode is {}, vo code is {}", mobile1, validCode, vo.getCode());
			if (null == validCode || !vo.getCode().equals(validCode)) {
				log.info("valid code is incorrect. Param mobile is {}, area is {}, code is {}", vo.getMobile(), vo.getArea(), vo.getCode());
				throw new ServiceException(ServiceError.VALID_ERROR);
			}
		} catch (Exception e) {
			log.warn("checkcode redis exception. mobile is {}, error msg is {}", mobile1, e.getMessage());
			if (e instanceof ServiceException) {
				throw e;
			}
		}
		return new ApiResponse.ApiResponseBuilder().message("校验成功").build();
	}
	
	/**
	 * 修改验证手机号
	 * @param uid
	 * @param newMobile
	 * @param area
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.changeVerifyMobile")
	@ResponseBody
	public ApiResponse changeVerifyMobile(String uid, String newMobile, String area) throws GatewayException {
		log.info("Begin call changeVerifyMobile controller. uid = {}, newMobile is {}, area is {}", uid, newMobile, area);
		if (StringUtils.isEmpty(newMobile)) {
			log.warn("changeVerifyMobile newMobile is null with uid is {}", uid);
			throw new ServiceException(ServiceError.PARAM_ERROR);
		}
		if (StringUtils.isEmpty(uid) || !uid.matches("\\d+")) {
			log.warn("changeVerifyMobile uid is {} ", uid);
			throw new ServiceException(ServiceError.BROWSE_DEL_UID_ISNULL);
		}

		UserRequestBO bo = new UserRequestBO();
		bo.setUid(Integer.parseInt(uid));
		bo.setMobile(newMobile);
		bo.setArea(area);
		CommonRspBO resp = serviceCaller.call("users.changeVerifyMobile", bo, CommonRspBO.class);

		// 组装返回参数
		return new ApiResponse.ApiResponseBuilder().data(resp).build();
	}
	
	/**
	 * 检查手机是否可以验证
	 * 
	 * @param area
	 * @param mobile
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.checkVerifyMobile")
	@ResponseBody
	public ApiResponse checkVerifyMobile(String uid, String area, String mobile) throws GatewayException {
		log.info("Begin call checkVerifyMobile controller. area = {}, mobile={}, uid={}", area, mobile, uid);
		
		if (StringUtils.isEmpty(uid) || !uid.matches("\\d+")) {
			log.warn("checkVerifyMobile uid is {} ", uid);
			throw new ServiceException(ServiceError.BROWSE_DEL_UID_ISNULL);
		}
		ApiResponse apiResponse = null;

		// 发送请求到服务端
		ProfileRequestBO bo = new ProfileRequestBO();
		bo.setArea(area);
		bo.setUid(Integer.parseInt(uid));
		bo.setMobile(mobile);
		serviceCaller.call("users.checkVerifyMobile", bo, CommonRspBO.class);

		// 组装返回参数
		apiResponse = new ApiResponse.ApiResponseBuilder().build();
		return apiResponse;
	}
	
	/**
	 * 找回密码，判断code是否有效
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.checkCodeValid")
	@ResponseBody
	public ApiResponse checkCodeValid(String code) throws GatewayException {
		log.info("checkCodeValid with code={}", code);
		try {
			if (StringUtils.isEmpty(code)) {
				log.warn("checkCodeValid with empty code");
				throw new ServiceException(ServiceError.CODE_IS_EMPTY);
			}
			
			DESRequestBO desRequestBO = new DESRequestBO();
			desRequestBO.setCode(code);
			String desCode = serviceCaller.call("users.desDecrypt", desRequestBO, String.class);
			JSONObject json = JSONObject.parseObject(desCode);
			int uid = json.getIntValue("uid");
			int time = json.getIntValue("time");
			//验证邮件发送超过一天都没有打开，则验证信息无效
			if (uid == 0 || time + 86400 < DateUtil.getCurrentTimeSecond()) {
				log.warn("checkCodeValid time over with code={}, uid={}, time={}", code, uid, time);
				throw new ServiceException(ServiceError.CODE_IS_INVALID);
			}
		} catch (Exception e) {
			log.error("checkCodeValid error with code=" + code, e);
			throw new ServiceException(ServiceError.CODE_IS_INVALID);
		}
		return new ApiResponse.ApiResponseBuilder().message(Constant.DEFAULT_SUCCESS_MESSAGE).build();
	}
	
	/**
	 * 发送验证邮件到邮箱
	 * @param email
	 * @param callback
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.passport.sendVerifyEmailInfo")
	@ResponseBody
	public ApiResponse sendVerifyEmailInfo(String email, String callback) throws GatewayException {
		log.debug("Begin call sendVerifyEmailInfo controller. email={}, callback={}", email, callback);
		ApiResponse apiResponse = null;

		// 发送请求到服务端
		ProfileRequestBO bo = new ProfileRequestBO();
		bo.setEmail(email);
		bo.setCallback(callback);
		serviceCaller.call("users.sendVerifyEmailInfo", bo, CommonRspBO.class);

		// 组装返回参数
		apiResponse = new ApiResponse.ApiResponseBuilder().build();
		return apiResponse;
	}

}
