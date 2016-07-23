package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class SmsbindVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7000927786850827782L;

	private String uid;
	private String mobile;
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
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
}
