package com.yoho.gateway.model.response;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class UserCouponsRspVO {
	/**
	 * 用户ID
	 */
	private int id;

	/**
	 * 优惠券id
	 */
	private int coupon_id;

	/**
	 * 优惠券名称
	 */
	private String coupon_name;

	/**
	 * 优惠券图片
	 */
	private String coupon_pic;

	/**
	 * 优惠券金额
	 */
	private int money;

	/**
	 * 优惠券有效期
	 */
	private String couponValidity;

	/**
	 * TODO
	 */
	private String order_code;

	/**
	 * 是否超时
	 */
	private String is_overtime;

	public UserCouponsRspVO() {

	}

	public UserCouponsRspVO(int id, int coupon_id, String coupon_name,
			String coupon_pic, int money, String couponValidity,
			String order_code) {
		this.id = id;
		this.coupon_id = coupon_id;
		this.coupon_name = coupon_name;
		this.coupon_pic = coupon_pic;
		this.money = money;
		this.couponValidity = couponValidity;
		this.order_code = order_code;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCoupon_id() {
		return coupon_id;
	}

	public void setCoupon_id(int coupon_id) {
		this.coupon_id = coupon_id;
	}

	public String getCoupon_name() {
		return coupon_name;
	}

	public void setCoupon_name(String coupon_name) {
		this.coupon_name = coupon_name;
	}

	public String getCoupon_pic() {
		return coupon_pic;
	}

	public void setCoupon_pic(String coupon_pic) {
		this.coupon_pic = coupon_pic;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		this.money = money;
	}

	public String getCouponValidity() {
		return couponValidity;
	}

	public void setCouponValidity(String couponValidity) {
		this.couponValidity = couponValidity;
	}

	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}

	public String getIs_overtime() {
		return is_overtime;
	}

	public void setIs_overtime(String is_overtime) {
		this.is_overtime = is_overtime;
	}
}
