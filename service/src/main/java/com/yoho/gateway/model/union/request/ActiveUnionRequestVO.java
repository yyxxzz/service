package com.yoho.gateway.model.union.request;

import com.yoho.service.model.BaseBO;

public class ActiveUnionRequestVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6252557096486057208L;

	private String appid;
	private String udid;
	
	/**
	 * iso andriod
	 */
	private String apptype;
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public String getApptype() {
		return apptype;
	}
	public void setApptype(String apptype) {
		this.apptype = apptype;
	}
	
}
