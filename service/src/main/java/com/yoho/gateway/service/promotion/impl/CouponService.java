package com.yoho.gateway.service.promotion.impl;

import com.yoho.gateway.utils.DateUtil;
import com.yoho.service.model.promotion.UserCouponBo;
import com.yoho.service.model.promotion.UserCouponLi;
import com.yoho.service.model.promotion.UserCouponLiBo;
import com.yoho.service.model.promotion.UserCouponListBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author lijian
 * 2016-1-19 10:20:18
 *
 */
@Service
public class CouponService {

	static Logger logger = LoggerFactory.getLogger(CouponService.class);

	private final static String COUPONS_DEFAULT_PIC = "http://feature.yoho.cn/coupon_unusable.png";

	//装配结果数据
	public UserCouponLi convertUserCouponList(UserCouponListBO userCouponListBO) {
		UserCouponLi userCouponLi=new UserCouponLi();
		if(userCouponListBO==null||userCouponListBO.getCouponList()==null){
			return userCouponLi;
		}
		userCouponLi.setTotal(userCouponListBO.getTotal());

		List<UserCouponLiBo> info=new ArrayList<UserCouponLiBo>();
		if(userCouponListBO!=null&&userCouponListBO.getCouponList()!=null){
			List<UserCouponBo>couponList=userCouponListBO.getCouponList();
			for (UserCouponBo userCouponBo : couponList) {
				UserCouponLiBo coupon=new UserCouponLiBo();
				coupon.setCoupon_id(userCouponBo.getCouponId());
				coupon.setId(userCouponBo.getId());
				coupon.setCoupon_name(userCouponBo.getCouponDetailInfomation());
				coupon.setCoupon_pic(StringUtils.isBlank(userCouponBo.getCouponImageUrl())==true?COUPONS_DEFAULT_PIC:userCouponBo.getCouponImageUrl());

				//优惠券过期时间
				if(userCouponBo.getOverTime() == null){
					logger.warn("coupons over time is null.{}", userCouponBo);
					coupon.setIs_overtime("Y");
				}else {
					coupon.setIs_overtime(userCouponBo.getOverTime() < DateUtil.getCurrentTimeSecond() == true ? "Y" : "N");
				}
				coupon.setOrder_code(userCouponBo.getOrderCode());
				coupon.setMoney(userCouponBo.getCouponValue());
				coupon.setCouponValidity(userCouponBo.getCouponValidity());
				info.add(coupon);
			}
		}
		userCouponLi.setInfo(info);
		return userCouponLi;
	}

}
