package com.yoho.gateway.model.promotion;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 优惠券返回字段
 * @author MALI
 *
 */
public class ProductCouponsVo {
	/**
	 * 
	 */
	@JSONField(name = "coupon_List")
	List<ProductCouponsInfo> couponList;

	public List<ProductCouponsInfo> getCouponList() {
		return couponList;
	}

	public void setCouponList(List<ProductCouponsInfo> couponList) {
		this.couponList = couponList;
	}

	@Override
	public String toString() {
		return "ProductCouponsVo [couponList=" + couponList + "]";
	}
}
