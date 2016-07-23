package com.yoho.gateway.controller.users;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.user.passport.VipDetailInfoVo;
import com.yoho.gateway.model.user.passport.VipInfoVo;
import com.yoho.service.model.promotion.UserCouponListBO;
import com.yoho.service.model.promotion.request.UserCouponListReq;
import com.yoho.service.model.request.UserPassportReqBO;
import com.yoho.service.model.response.UserPassportRspBO;
import com.yoho.service.model.vip.CostInfo;
import com.yoho.service.model.vip.PremiumScop;
import com.yoho.service.model.vip.VipInfo;
import com.yoho.service.model.vip.VipLevel;

@Controller
public class UserPassportController {

	@Resource
	ServiceCaller serviceCaller;

	private static final Logger logger = LoggerFactory.getLogger(UserPassportController.class);

	/**
	 * PC获取用户信息
	 * 
	 * @param uId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=open.passport.get")
	@ResponseBody
	public ApiResponse getUserPassport(String uid) throws GatewayException {
		logger.info("Begin call getUserPassport. with param uid is {}", uid);
		if (StringUtils.isEmpty(uid) || (!isNum(uid)) || "0".equals(uid)) {
			throw new ServiceException(ServiceError.UID_IS_NULL);
		}
		UserPassportReqBO reqBO = new UserPassportReqBO();
		reqBO.setUid(Integer.valueOf(uid));
		UserPassportRspBO serviceResponse = serviceCaller.call("users.getUserPassport", reqBO, UserPassportRspBO.class);
		int couponCount = 0;
		UserCouponListReq userCouponListReq = new UserCouponListReq();
		userCouponListReq.setType("notuse");
		userCouponListReq.setUid(Integer.valueOf(uid));
		UserCouponListBO result = serviceCaller.call("promotion.queryUserCoupons", userCouponListReq, UserCouponListBO.class);
		if (result != null && result.getTotal() != null) {
			couponCount = result.getTotal();
		}
		serviceResponse.setCouponCount(couponCount);
		logger.debug("call users.getUserPassport with param uid is {}, with result is {}", uid, serviceResponse);
		// 生成返回结果(构造PC老接口要的结果)
		JSONObject data = this.genResultData(serviceResponse);
		return new ApiResponse.ApiResponseBuilder().code(200).message("User info").data(data).build();
	}

	private boolean isNum(String s) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(s);
		return isNum.matches();
	}

	private JSONObject genResultData(UserPassportRspBO serviceResponse) {

		JSONObject data = new JSONObject();
		data.put("result", "1");
		// 生成vo的jsonObject
		JSONObject voObject = new JSONObject();
		voObject.put("uid", serviceResponse.getUid());
		voObject.put("profile_name", serviceResponse.getProfileName());
		voObject.put("head_ico", serviceResponse.getHeadIco());
		voObject.put("couponCount", serviceResponse.getCouponCount());
		voObject.put("YohocoinCount", serviceResponse.getYohocoinCount());
		voObject.put("orderCount", serviceResponse.getOrderCount());
		voObject.put("refundCount", serviceResponse.getRefundCount());
		voObject.put("messageCount", serviceResponse.getMessageCount());
		// voObject.put("message", serviceResponse.getMessage());
		voObject.put("token", "");
		voObject.put("random", System.currentTimeMillis() / 1000d);
		// VIP信息
		VipInfoVo vip = this.genVipInfoVo(serviceResponse.getVipInfo());
		voObject.put("vip", vip);
		data.put("data", voObject);
		return data;
	}

	private VipInfoVo genVipInfoVo(VipInfo vipInfo) {
		VipInfoVo vipVo = new VipInfoVo();
		if (vipInfo == null) {
			return vipVo;
		}
		vipVo.setVipStartTime(vipInfo.getStartTime());
		vipVo.setVipEndTime(vipInfo.getEndTime());
		vipVo.setVipRemainDays("" + vipInfo.getVipRemainInDays());
		vipVo.setVipEndDay(("" + vipInfo.getVipRemainInDays()));
		vipVo.setFitTime("" + this.addDay(vipVo.getVipStartTime(), -15));// vipStartTime

		// 当前VIP等级
		VipDetailInfoVo curVipInfo = this.genVipDetailInfoVo(vipInfo.getCurVipInfo());
		vipVo.setCurVipInfo(curVipInfo);

		// 下一个VIP等级
		VipDetailInfoVo nextVipInfo = this.genVipDetailInfoVo(vipInfo.getNextVipInfo());
		vipVo.setNextVipInfo(nextVipInfo);

		// 消费情况
		CostInfo costInfo = vipInfo.getCostInfo();

		// 消费总额
		double curTotalCost = costInfo == null ? 0 : this.stringToDouble(costInfo.getTotal());
		vipVo.setCurTotalCost("" + curTotalCost);

		// 年消费总额
		double curYearCost = costInfo == null ? 0 : this.stringToDouble(costInfo.getYearTotal());
		vipVo.setCurYearCost("" + curYearCost);

		// 升级所需的消费
		Double upgradeNeedCost = this.stringToDouble(vipInfo.getUpgradeNeedCost());
		vipVo.setUpgradeNeedCost(upgradeNeedCost.toString());

		// 计算百分比
		int curYearCostPer = 0;// 年消费百分比(curYearCost / nextVipInfo.needCost)
		int upgradeNeedCostPer = 0;// 升级所需的消费比（upgradeNeedCost /
		if (nextVipInfo != null) {
			double nextVipNeedCost = this.stringToDouble(nextVipInfo.getNeedCost());
			curYearCostPer = (int) (curYearCost * 100 / nextVipNeedCost);
			upgradeNeedCostPer = (int) (upgradeNeedCost * 100 / nextVipNeedCost);
		}
		vipVo.setCurYearCostPer("" + curYearCostPer);
		vipVo.setUpgradeNeedCostPer("" + upgradeNeedCostPer);
		return vipVo;
	}

	private VipDetailInfoVo genVipDetailInfoVo(VipLevel vipLevel) {
		if (vipLevel == null) {
			return null;
		}
		VipDetailInfoVo vipDetailInfoVo = new VipDetailInfoVo();
		vipDetailInfoVo.setTitle(vipLevel.getTitle());
		vipDetailInfoVo.setCurLevel(vipLevel.getCurLevel());
		vipDetailInfoVo.setNextLevel(vipLevel.getNextLevel());
		vipDetailInfoVo.setNeedCost("" + vipLevel.getNeedCost());
		vipDetailInfoVo.setCommonDiscount("" + vipLevel.getCommonDiscount());
		vipDetailInfoVo.setPromotionDiscount("" + vipLevel.getPromotionDiscount());

		List<PremiumScop> premiumScops = vipLevel.getPremiumScop();
		List<String> premiumScopIds = new ArrayList<String>();
		for (PremiumScop premiumScop : premiumScops) {
			premiumScopIds.add(String.valueOf(premiumScop.getId()));
		}
		vipDetailInfoVo.setPremiumScops(premiumScopIds);
		return vipDetailInfoVo;
	}

	private String addDay(String dateSecondStr, int day) {
		int dateSecond = Integer.valueOf(dateSecondStr);
		int date = dateSecond + day * 24 * 60 * 60;
		return String.valueOf(date);
	}

	private double stringToDouble(String value) {
		try {
			return Double.valueOf(value);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static void main(String[] args) {
		String x = "15889965239::31981::AAAAA::BBBBB";
		System.out.println(x.split("::")[0]);
	}

}
