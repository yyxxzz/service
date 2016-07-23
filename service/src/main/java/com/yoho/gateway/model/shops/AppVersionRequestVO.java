package com.yoho.gateway.model.shops;

public class AppVersionRequestVO {
	
	/**
	 * 最新的app版本
	 */
	private String appVersion;
	
	/**
	 * 安装包的类型：安卓，IOS
	 */
	private String clientType;
	
	/**
	 * 描述
	 */
	private String content;
	
	/**
	 * 安装包的url地址
	 */
	private String url;
	
	/**
	 * 安装包地址是否有效，0:无效 ，1：有效
	 */
	private int status =1;

	public String getAppVersion() {
		return appVersion;
	}

	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}

	public String getClientType() {
		return clientType;
	}

	public void setClientType(String clientType) {
		this.clientType = clientType;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
