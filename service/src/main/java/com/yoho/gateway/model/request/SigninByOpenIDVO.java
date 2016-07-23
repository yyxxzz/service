package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class SigninByOpenIDVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6036365953484411778L;

	/**
	 * openId
	 */
	String openId;
	
	/**
	 * 客户端类型
	 */
	String client_type;
	
	/**
	 * 名称
	 */
	String nickname;
	
	/**
	 * replace_id
	 */
	String replace_id;
	
	/**
	 * 来源类型
	 */
	String source_type;
	
	/**
	 * 新的token
	 */
	private String unionId;
	
	private String shopping_key;
	public String getShopping_key() {
		return shopping_key;
	}
	public void setShopping_key(String shopping_key) {
		this.shopping_key = shopping_key;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
	public String getClient_type() {
		return client_type;
	}
	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getReplace_id() {
		return replace_id;
	}
	public void setReplace_id(String replace_id) {
		this.replace_id = replace_id;
	}
	public String getSource_type() {
		return source_type;
	}
	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}
	public String getUnionId() {
		return unionId;
	}
	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}
	
}
