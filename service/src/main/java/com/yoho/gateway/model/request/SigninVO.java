package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class SigninVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5823181510877178353L;

	/**
	 * 手机或邮箱
	 */
	private String profile;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 地区
	 */
	private String area;
	
	/**
	 * 客户端类型
	 */
	private String client_type;
	private String shopping_key;
	public String getShopping_key() {
		return shopping_key;
	}
	public void setShopping_key(String shopping_key) {
		this.shopping_key = shopping_key;
	}
	public String getProfile() {
		return profile;
	}
	public void setProfile(String profile) {
		this.profile = profile;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getClient_type() {
		return client_type;
	}
	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}
	
}
