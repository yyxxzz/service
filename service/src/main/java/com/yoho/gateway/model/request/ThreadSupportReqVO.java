package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

/**
 * 第三方登录接口的请求
 * @author ping.huang
 *
 */
public class ThreadSupportReqVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6701650906019870078L;
	
	/**
	 * openId
	 */
	private String openId;
	
	/**
	 * 客户端类型
	 */
	private String client_type;
	
	/**
	 * 来源类型
	 */
	private String source_type;
	
	/**
	 * replace_id
	 */
	private String replace_id;
	
	/**
	 * 名称
	 */
	private String nickname;
	
	/**
	 * 名称
	 */
	private String realname;
	
	/**
	 * shopping_key
	 */
	private String shopping_key;
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
	public String getSource_type() {
		return source_type;
	}
	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}
	public String getReplace_id() {
		return replace_id;
	}
	public void setReplace_id(String replace_id) {
		this.replace_id = replace_id;
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
	public String getShopping_key() {
		return shopping_key;
	}
	public void setShopping_key(String shopping_key) {
		this.shopping_key = shopping_key;
	}
	

}
