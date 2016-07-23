package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class ProfileRequestVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String email;
	private String area;
	private String mobile;
	private boolean checkSSO;
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public boolean isCheckSSO() {
		return checkSSO;
	}
	public void setCheckSSO(boolean checkSSO) {
		this.checkSSO = checkSSO;
	}

}
