package com.yoho.gateway.model.shops;

import lombok.Data;

@Data
public class LoginReqVO {
	/**
	 * 手机或邮箱
	 */
	private String account;
	
	/**
	 * 密码
	 */
	private String password;
	
	/**
	 * 客户端类型
	 */
	private String client_type;
	
}
