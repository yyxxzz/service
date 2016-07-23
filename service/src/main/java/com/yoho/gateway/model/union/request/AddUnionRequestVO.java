package com.yoho.gateway.model.union.request;

import com.yoho.service.model.BaseBO;

public class AddUnionRequestVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6252557096486057208L;

	private String app;
	private String udid;
	private String callbackurl;
	public String getApp() {
		return app;
	}
	public void setApp(String app) {
		this.app = app;
	}
	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}
	public String getCallbackurl() {
		return callbackurl;
	}
	public void setCallbackurl(String callbackurl) {
		this.callbackurl = callbackurl;
	}
}
