package com.yoho.gateway.model.request;

import org.apache.commons.lang.StringUtils;

import com.yoho.service.model.BaseBO;

/**
 * 修改绑定手机号
 * @author zhengqiang
 *
 */
public class ChangeBindVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6007915137392131926L;
	/**
	 * 
	 */
	
	private String client_type;
	private String mobile;
	private String uid;
	private String area;
	private String code;
	private String open_id;
	private String nickname;
	private String realname;
	private String email;
	private String source_type;
	private String password;
	
	public void notEmpty() {
		this.client_type = StringUtils.defaultString(this.client_type);
		this.mobile = StringUtils.defaultString(this.mobile);
		this.area = StringUtils.defaultString(this.area);
		this.open_id = StringUtils.defaultString(this.open_id);
		this.nickname = StringUtils.defaultString(this.nickname);
		this.realname = StringUtils.defaultString(this.realname);
		this.email = StringUtils.defaultString(this.email);
		this.source_type = StringUtils.defaultString(this.source_type);
		this.password = StringUtils.defaultString(this.password);
		this.uid = StringUtils.defaultString(this.uid);
		this.code = StringUtils.defaultString(this.code);
	}

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

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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
	
}
