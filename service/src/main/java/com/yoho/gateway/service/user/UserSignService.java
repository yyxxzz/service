package com.yoho.gateway.service.user;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.user.sign.UserSignInfoRspVO;
import com.yoho.gateway.model.user.sign.UserSignInfoVO;
import com.yoho.gateway.model.user.sign.UserSignRspVO;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.users.request.sign.UserSignReqBO;
import com.yoho.service.model.users.response.sign.UserSignInfo;
import com.yoho.service.model.users.response.sign.UserSignInfoRspBO;
import com.yoho.service.model.users.response.sign.UserSignRspBO;

@Service
public class UserSignService {

	private static final Logger logger = LoggerFactory.getLogger(UserSignService.class);

	@Resource
	private ServiceCaller serviceCaller;

	private UserSignInfoRspBO queryUserSignInfo(Integer uid) {
		UserSignReqBO userSignReqBO = new UserSignReqBO(uid);
		UserSignInfoRspBO userSignInfoRspBO = serviceCaller.call("users.getUserSignInfo", userSignReqBO, UserSignInfoRspBO.class);
		return userSignInfoRspBO;
	}

	/**
	 * 获取用户签到信息接口
	 * 
	 * @param uid
	 * @return
	 */
	public UserSignInfoRspVO getUserSignInfo(Integer uid) {
		logger.info("Enter gateway UserSignService.getUserSignInfo. param uid is {}", uid);
		UserSignInfoRspBO userSignInfoRspBO = this.queryUserSignInfo(uid);
		return this.fillUserSignInfoRspVOFromBO(new UserSignInfoRspVO(), userSignInfoRspBO);
	}

	/**
	 * 用户签到接口
	 * 
	 * @param uid
	 * @return
	 */
	public UserSignRspVO userSign(Integer uid) {
		logger.info("Enter gateway UserSignService.userSign. param uid is {}", uid);
		UserSignReqBO userSignReqBO = new UserSignReqBO(uid);
		UserSignRspBO userSignRspBO = serviceCaller.call("users.userSign", userSignReqBO, UserSignRspBO.class);

		UserSignRspVO vo = new UserSignRspVO();
		vo.setUid(String.valueOf(userSignRspBO.getUid()));
		vo.setGainYohoCoinNum(String.valueOf(userSignRspBO.getGainYohoCoinNum()));
		vo.setTotalYohoCoinNum(String.valueOf(userSignRspBO.getTotalYohoCoinNum()));

		// 签到成功再查一下签到信息（app要求）
		UserSignInfoRspBO userSignInfoRspBO = this.queryUserSignInfo(uid);
		this.fillUserSignInfoRspVOFromBO(vo, userSignInfoRspBO);

		return vo;

	}

	/**
	 * 修改签到的推送标识位
	 * 
	 * @param uid
	 * @param pushFlag
	 * @return
	 */
	public CommonRspBO changeUserSignPushFlag(Integer uid, Integer pushFlag) {
		UserSignReqBO userSignReqBO = new UserSignReqBO(uid, pushFlag);
		CommonRspBO commonRspBO = serviceCaller.call("users.changeUserSignPushFlag", userSignReqBO, CommonRspBO.class);
		return commonRspBO;
	}

	private UserSignInfoRspVO fillUserSignInfoRspVOFromBO(UserSignInfoRspVO vo, UserSignInfoRspBO bo) {
		vo.setUid(String.valueOf(bo.getUid()));
		vo.setPushFlag(String.valueOf(bo.getPushFlag()));
		vo.setTotalYohoCoinNum(String.valueOf(bo.getTotalYohoCoinNum()));
		vo.setTodayKey(String.valueOf(bo.getTodayKey()));
		vo.setTodaySigned(String.valueOf(bo.isTodaySigned()));
		vo.setTodayCanGainYohoCoinNum(String.valueOf(bo.getTodayCanGainYohoCoinNum()));
		vo.setTomorrowCanGainYohoCoinNum(String.valueOf(bo.getTomorrowCanGainYohoCoinNum()));
		vo.setConstantDay(String.valueOf(bo.getConstantDay()));

		List<UserSignInfoVO> vos = new ArrayList<UserSignInfoVO>();
		for (UserSignInfo userSignInfo : bo.getSignInfoList()) {
			UserSignInfoVO userSignInfoVO = new UserSignInfoVO();
			if (userSignInfo.isToday()) {
				userSignInfoVO.setDateKey("Today");
			} else {
				userSignInfoVO.setDateKey(userSignInfo.getDateKey());
			}
			userSignInfoVO.setConstantDay(String.valueOf(userSignInfo.getConstantDay()));
			userSignInfoVO.setYohoCoinNum(String.valueOf(userSignInfo.getYohoCoinNum()));
			userSignInfoVO.setSigned(String.valueOf(userSignInfo.isSigned()));
			userSignInfoVO.setToday(String.valueOf(userSignInfo.isToday()));
			vos.add(userSignInfoVO);
		}
		vo.setSignInfoList(vos);
		return vo;
	}
	
}
