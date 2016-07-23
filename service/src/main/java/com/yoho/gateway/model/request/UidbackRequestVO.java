package com.yoho.gateway.model.request;

import lombok.Data;


@Data
public class UidbackRequestVO {

	private int ssoId;
	/**
	 * 用户邮箱
	 */
	private String email;
	/**
	 * 电话
	 */
	private String mobile;

	/**
	 * 区域
	 */
	private String areacode;
	/**
	 * 请求客户端类型：android,iphone等
	 */
	private String clientType;
	
	
}
