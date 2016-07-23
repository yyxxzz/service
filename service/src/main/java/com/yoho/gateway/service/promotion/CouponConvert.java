package com.yoho.gateway.service.promotion;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.google.common.collect.Lists;
import com.yoho.gateway.model.promotion.ProductCouponsVo;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.service.model.promotion.response.ProductCouponsInfo;
import com.yoho.service.model.promotion.response.ProductCouponsRsp;

/**
 * 优惠券信息转换器
 * @author yoho
 *
 */
public class CouponConvert {
	/**
	 * BO -- > VO
	 * @param rsp
	 * @return
	 */
	public static final ProductCouponsVo convert(ProductCouponsRsp rsp) {
		ProductCouponsVo vo = new ProductCouponsVo();
		if (null != rsp) {
			List<ProductCouponsInfo> productCouponsList = rsp.getProductCouponsList();
			if (CollectionUtils.isNotEmpty(productCouponsList)) {
				List<com.yoho.gateway.model.promotion.ProductCouponsInfo> couponList 
					= new ArrayList<com.yoho.gateway.model.promotion.ProductCouponsInfo>(productCouponsList.size());
				
				com.yoho.gateway.model.promotion.ProductCouponsInfo info;
				for (ProductCouponsInfo productCouponsInfo : productCouponsList) {
					info = new com.yoho.gateway.model.promotion.ProductCouponsInfo();
					
					info.setCouponAmount(null != productCouponsInfo.getCouponAmount() ? productCouponsInfo.getCouponAmount().intValue() : 0);
					info.setCouponName(productCouponsInfo.getCouponName());
					info.setEndTime(DateUtil.getDateStrBySecond(productCouponsInfo.getEndTime(), "yyyy.MM.dd"));
					info.setId(productCouponsInfo.getId());
					info.setReceiveFlag(productCouponsInfo.getReceiveFlag());
					info.setStartTime(DateUtil.getDateStrBySecond(productCouponsInfo.getStartTime(), "yyyy.MM.dd"));
					
					couponList.add(info);
				}
				
				vo.setCouponList(couponList);
			}
		}else {
			vo.setCouponList(Lists.newArrayList());
		}
		
		return vo;
	}
}
