package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class MobileCheckReqVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5496378520726869185L;

	/**
	 * 手机号码
	 */
	private String mobile;
	
	/**
	 * 地区
	 */
	private String area;
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
}
