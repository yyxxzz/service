package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class BindVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 388921544259543640L;

	/**
	 * 客户端类型(iphone,andriod)
	 */
	private String client_type;
	
	/**
	 * 手机号码
	 */
	private String mobile;
	
	/**
	 * 地区
	 */
	private String area;
	
	/**
	 * open_id
	 */
	private String open_id;
	
	/**
	 * 名称
	 */
	private String nickname;
	
	/**
	 * 名称
	 */
	private String realname;
	
	/**
	 * 邮箱
	 */
	private String email;
	
	/**
	 * 来源类型(qq,sina,alipay,wechat)
	 */
	private String source_type;
	
	private String shopping_key;
	
	/**
	 * 密码
	 */
	private String password;
	public String getClient_type() {
		return client_type;
	}
	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}
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
	public String getOpen_id() {
		return open_id;
	}
	public void setOpen_id(String open_id) {
		this.open_id = open_id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getRealname() {
		return realname;
	}
	public void setRealname(String realname) {
		this.realname = realname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getSource_type() {
		return source_type;
	}
	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getShopping_key() {
		return shopping_key;
	}
	public void setShopping_key(String shopping_key) {
		this.shopping_key = shopping_key;
	}
}
