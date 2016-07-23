package com.yoho.gateway.model.promotion;

import java.math.BigDecimal;

public class ProductCouponsInfo {
	/**
	 * 金额
	 */
    private Integer couponAmount;
    
    /**
     * 优惠券id
     */
    private Integer id;
    
    /**
     * 券使用开始时间
     */
    private String startTime;

    /**
     * 卷使用结束时间
     */
    private String endTime;
    
    /**
     * 券的使用规则，名称
     */
    private String couponName;
    
    /**
     * 是否已领取   true 代表已领取，否则为未领取
     */
    private Boolean receiveFlag;

	public Integer getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(Integer couponAmount) {
		this.couponAmount = couponAmount;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public Boolean getReceiveFlag() {
		return receiveFlag;
	}

	public void setReceiveFlag(Boolean receiveFlag) {
		this.receiveFlag = receiveFlag;
	}

	@Override
	public String toString() {
		return "ProductCouponsInfo [couponAmount=" + couponAmount + ", id="
				+ id + ", startTime=" + startTime + ", endTime=" + endTime
				+ ", couponName=" + couponName + ", receiveFlag=" + receiveFlag
				+ "]";
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
}
