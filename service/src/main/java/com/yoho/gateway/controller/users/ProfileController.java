package com.yoho.gateway.controller.users;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.CacheFactory;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.helper.CacheKeyEnum;
import com.yoho.gateway.helper.MobileHelper;
import com.yoho.service.model.profile.UserProfileBO;
import com.yoho.service.model.request.ProfileRequestBO;
import com.yoho.service.model.response.ProfileInfoRsp;


@Controller
public class ProfileController {

	@Resource
	private ServiceCaller service;
	
	@Resource
	CacheFactory cacheFactory;
	
	//用户id不能为空
	private final static int UID_NOT_NULL_CODE = 421;
	private final static String UID_NOT_NULL_MSG = "uid不能为空";
	
	//用户id不能为空
	private final static int SUCCESS_CODE = 200;
	private final static String SUCCESS_MSG = "请求成功";

	
	//个人信息地址url
	private final static String PROFILE_URL = "users.getUserprofile";
	
	private Logger logger = LoggerFactory.getLogger(ProfileController.class);
	
    @RequestMapping(params = "method=app.passport.profile")
    @ResponseBody
	public ApiResponse profile(@RequestParam("uid") int uid) throws GatewayException{
    	logger.debug("Begin call profile controller. uid = {}", uid);
    	ApiResponse apiResponse = null;
    	//(1)判断用户uid是否为空
    	if(uid < 1){
    		logger.warn("uid is null. uid={}", uid);
    		throw new GatewayException(UID_NOT_NULL_CODE, UID_NOT_NULL_MSG);
    	}
    	
    	// // 从redis缓存中获取profile信息,吃掉异常
    	try{
    		ProfileInfoRsp profileFromRedis = cacheFactory.getRedisValueCache().get(CacheKeyEnum.MY_PROFILE, uid, ProfileInfoRsp.class);
    		if (null != profileFromRedis) {
				// 生日券的时候发送验证码需要明文的手机号码
//				profileFromRedis.setMobile(MobileHelper.coverMobile(profileFromRedis.getMobile()));
    			return new ApiResponse.ApiResponseBuilder().data(profileFromRedis).code(SUCCESS_CODE).message(SUCCESS_MSG).build();
    		}
    	}catch(Exception e){
    		logger.warn("get profile from redis error. uid={}", uid);
    	}
    	
    	//(2)发送请求到服务端，请求用户的个人信息
    	UserProfileBO userProfileBO = new UserProfileBO(uid);
    	ProfileInfoRsp profileInfoRsp = service.call(PROFILE_URL, userProfileBO, ProfileInfoRsp.class);
    	
    	// 保存profile信息至redis缓存,吃掉异常
    	try{
    		DynamicLongProperty expire = DynamicPropertyFactory.getInstance().getLongProperty(CacheKeyEnum.MY_PROFILE.getExpireKey(), 24);
    		cacheFactory.getRedisValueCache().set(CacheKeyEnum.MY_PROFILE, uid, profileInfoRsp, expire.get(), TimeUnit.HOURS);
    	}catch(Exception e){
    		logger.warn("set profile from redis error. uid={}", uid);
    	}
    	
    	// 手机号码中间四位用 * 隐藏
//    	if(profileInfoRsp != null){
//    		profileInfoRsp.setMobile(MobileHelper.coverMobile(profileInfoRsp.getMobile()));
//    	}
    	
    	//(3)组装返回参数
    	apiResponse =  new ApiResponse.ApiResponseBuilder().data(profileInfoRsp).code(SUCCESS_CODE).message(SUCCESS_MSG).build();
		return apiResponse;
    }
    
    /**
     * 根据邮箱查询用户信息
     * @return
     */
    @RequestMapping(params = "method=app.passport.getProfileByEmail")
    @ResponseBody
	public ApiResponse getProfileByEmail(@RequestParam("email") String email) throws GatewayException{
    	logger.debug("Begin call getProfileByEmail controller. email = {}", email);
    	ApiResponse apiResponse = null;
    	
    	ProfileRequestBO bo = new ProfileRequestBO();
		bo.setEmail(email);
		bo.setCheckSSO(true);
		ProfileInfoRsp result = service.call("users.getUserprofileByEmailOrMobile", bo, ProfileInfoRsp.class);
		if (result == null || result.getUid() == 0) {
			logger.warn("getProfileByEmail error reason USER_NOT_EXISTS with email is {}", email);
			throw new ServiceException(ServiceError.USER_NOT_EXISTS);
		}
    	//组装返回参数
		
    	apiResponse =  new ApiResponse.ApiResponseBuilder().data(covertToJSON(result)).code(SUCCESS_CODE).message(SUCCESS_MSG).build();
		return apiResponse;
    }
    
    /**
     * 根据手机号查询用户信息
     * @return
     */
    @RequestMapping(params = "method=app.passport.getProfileByMobile")
    @ResponseBody
	public ApiResponse getProfileByMobile(String mobile, @RequestParam(name="area", defaultValue="86")String area) throws GatewayException{
    	logger.debug("Begin call getProfileByMobile controller. mobile = {}, area={}", mobile, area);
    	ApiResponse apiResponse = null;
    	
    	ProfileRequestBO bo = new ProfileRequestBO();
		bo.setMobile(mobile);
		bo.setArea(area);
		bo.setCheckSSO(true);
		ProfileInfoRsp result = service.call("users.getUserprofileByEmailOrMobile", bo, ProfileInfoRsp.class);
		if (result == null || result.getUid() == 0) {
			logger.warn("getProfileByMobile error reason USER_NOT_EXISTS with mobile is {}, area={}", mobile, area);
			throw new ServiceException(ServiceError.USER_NOT_EXISTS);
		}
    	//组装返回参数
    	apiResponse =  new ApiResponse.ApiResponseBuilder().data(covertToJSON(result)).code(SUCCESS_CODE).message(SUCCESS_MSG).build();
		return apiResponse;
    }
    
    private JSONObject covertToJSON(ProfileInfoRsp profile) {
    	if (profile == null) {
    		return null;
    	}
    	JSONObject json = new JSONObject();
    	json.put("email", profile.getEmail());
    	json.put("mobile", profile.getMobile());
    	json.put("uid", profile.getUid());
    	json.put("nickname", profile.getNickname());
    	json.put("head_ico", profile.getHead_ico());
    	json.put("profile_name", profile.getProfile_name());
    	return json;
    }
}
