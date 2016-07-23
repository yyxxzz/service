package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class ChangePwdRequestVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4076441495667121753L;

	private String mobile;
	private String area;
	private String newpwd;
	private String token;
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
	public String getNewpwd() {
		return newpwd;
	}
	public void setNewpwd(String newpwd) {
		this.newpwd = newpwd;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}
