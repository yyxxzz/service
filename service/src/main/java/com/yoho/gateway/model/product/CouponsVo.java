package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class CouponsVo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8254921762738338917L;

	@JSONField(name = "coupon_id")
	private int couponId;

	@JSONField(name = "coupon_name")
	private String couponName;

	@JSONField(name = "coupon_pic")
	private String couponPic;

	@JSONField(name = "money")
	private String money;

	@JSONField(name = "couponValidity")
	private String couponValidity;

	@JSONField(name = "status")
	private Integer status;

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public int getCouponId() {
		return couponId;
	}

	public void setCouponId(int couponId) {
		this.couponId = couponId;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public String getCouponPic() {
		return couponPic;
	}

	public void setCouponPic(String couponPic) {
		this.couponPic = couponPic;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getCouponValidity() {
		return couponValidity;
	}

	public void setCouponValidity(String couponValidity) {
		this.couponValidity = couponValidity;
	}

}
