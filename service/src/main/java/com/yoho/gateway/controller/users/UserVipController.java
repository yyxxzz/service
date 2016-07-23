package com.yoho.gateway.controller.users;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.netflix.config.DynamicLongProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.CacheFactory;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.helper.CacheKeyEnum;
import com.yoho.gateway.model.request.UserVipVO;
import com.yoho.service.model.request.UserVipReqBO;
import com.yoho.service.model.vip.PremiumScop;
import com.yoho.service.model.vip.VipInfo;


@Controller
public class UserVipController {

	private Logger logger = LoggerFactory.getLogger(UserVipController.class);

	@Resource
	private ServiceCaller serviceCaller;
	
	//用户id不能为空
	private final static int UID_NOT_NULL_CODE = 421;
	private final static String UID_NOT_NULL_MSG = "uid不能为空";

	// 获取VIP权益成功的code和message
	private static final int VIP_NULLUID_CODE = 404;
	private static final String VIP_NULLUID_MSG = "uid不能为空.";

	// 获取VIP权益成功的code和message
	private static final int VIP_SUCCESS_CODE = 200;
	private static final String VIP_SUCCESS_MSG = "vip.";

	// 获取VIP权益成功的code和message
	private static final int GETPRIVILEGE_SUCCESS_CODE = 200;
	private static final String GETPRIVILEGE_SUCCESS_MSG = "privilege";

	// 获取用户VIP服务
	private final static String USER_VIP_DETAIL_SERVICE = "users.getVipDetailInfo";

	// 获取用户VIP权益服务
	private final static String VIP_PRIVILEGE_SERVICE = "users.getVipPrivilege";
	
	//15天的秒数
	private static final Integer SECONDSOF15DAYS = 15 * 24 * 60 * 60;
	
	@Resource
	CacheFactory cacheFactory;

	@RequestMapping(params = "method=app.passport.vip")
	@ResponseBody
	public ApiResponse vip(UserVipVO userVipVO) throws GatewayException {
		logger.debug("Begin call vip gateway. Param uid is {}", userVipVO.getUid());
		
		//(1)判断用户uid是否为空
    	if(userVipVO !=null && userVipVO.getUid() < 1){
    		logger.warn("uid is null. uid={}", userVipVO.getUid());
    		throw new GatewayException(UID_NOT_NULL_CODE, UID_NOT_NULL_MSG);
    	}
    	
    	VipInfo vipInfo = null;
    	// 从redis缓存中获取用户vip信息，吃掉异常
    	try{
    		vipInfo = cacheFactory.getRedisValueCache().get(CacheKeyEnum.MY_VIP, userVipVO.getUid(), VipInfo.class);
    	}catch(Exception e){
    		logger.warn("get user from redis error. uid={}", userVipVO.getUid());
    	}
    			
		try {
			
			if (vipInfo == null) {
				// (1)调用获取vip信息服务接口
				UserVipReqBO userVipReqBO = new UserVipReqBO();
				BeanUtils.copyProperties(userVipVO, userVipReqBO);
				vipInfo = serviceCaller.call(USER_VIP_DETAIL_SERVICE, userVipVO, VipInfo.class);
				
				// 保存用户vip信息至redis缓存,吃掉异常
				try{
					DynamicLongProperty expire = DynamicPropertyFactory.getInstance().getLongProperty(CacheKeyEnum.MY_VIP.getExpireKey(), 24);
					cacheFactory.getRedisValueCache().set(CacheKeyEnum.MY_VIP, userVipVO.getUid(), vipInfo, expire.get(), TimeUnit.HOURS);
				}catch(Exception e){
					logger.warn("set user to redis error. uid={}", userVipVO.getUid());
				}
			}

			// (2)组装返回参数
			JSONObject jsonObject = new JSONObject();
			List<PremiumScop> premiumScops = vipInfo.getCurVipInfo().getPremiumScop();
			if ("ipad".equalsIgnoreCase(userVipVO.getClient_type())) {
				List<String> enjoy_preferential = new ArrayList<String>();
				for (PremiumScop premiumScop : premiumScops) {
					enjoy_preferential.add(premiumScop.getTitle());
				}
				jsonObject.put("enjoy_preferential", enjoy_preferential);
			} else {
				jsonObject.put("enjoy_preferential", premiumScops);
			}
			
			//(3)计算vip条件达成时间
			Integer reachTime = 0;
			String startTime = vipInfo.getStartTime();
			if(null != startTime && !startTime.equals("0")){
				reachTime = Integer.valueOf(startTime) - SECONDSOF15DAYS;
			}
			
			jsonObject.put("current_vip_title", vipInfo.getCurVipInfo().getTitle());
			jsonObject.put("current_vip_level", vipInfo.getCurVipInfo().getCurLevel());
			jsonObject.put("next_vip_title", null == vipInfo.getNextVipInfo() ? "" : vipInfo.getNextVipInfo().getTitle());
			jsonObject.put("next_vip_level", null == vipInfo.getNextVipInfo() ? "" : vipInfo.getNextVipInfo().getCurLevel());
			jsonObject.put("next_need_cost", null == vipInfo.getNextVipInfo() ? "" : vipInfo.getNextVipInfo().getNeedCost());
			jsonObject.put("upgrade_need_cost", null == vipInfo.getUpgradeNeedCost() ? "" : vipInfo.getUpgradeNeedCost());
			jsonObject.put("current_total_cost", vipInfo.getCostInfo().getTotal());
			jsonObject.put("current_year_cost", vipInfo.getCostInfo().getYearTotal());
			jsonObject.put("vip_reach_time", reachTime.toString());
			jsonObject.put("vip_start_time", vipInfo.getStartTime());
			jsonObject.put("vip_end_time", vipInfo.getEndTime());

			return new ApiResponse.ApiResponseBuilder().code(VIP_SUCCESS_CODE).message(VIP_SUCCESS_MSG).data(jsonObject).build();
		} catch (ServiceException e) {
			if (ServiceError.UID_IS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(VIP_NULLUID_CODE).message(VIP_NULLUID_MSG).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}
		

		
	}


	@RequestMapping(params = "method=app.Passport.vip")
	@ResponseBody
	public ApiResponse getVip(UserVipVO userVipVO) throws GatewayException {
		return vip(userVipVO);
	}

	/**
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "method=app.passport.getPrivilege")
	@ResponseBody
	@Cachable
	public ApiResponse getPrivilege() {
		logger.debug("Begin call getPrivilege gateway.");

		// (1)调用查询优惠券列表信息服务
		List<PremiumScop> result = serviceCaller.call(VIP_PRIVILEGE_SERVICE, "", List.class);

		// (2)组装返回参数
		return new ApiResponse.ApiResponseBuilder().code(GETPRIVILEGE_SUCCESS_CODE).message(GETPRIVILEGE_SUCCESS_MSG).data(result).build();
	}
}
