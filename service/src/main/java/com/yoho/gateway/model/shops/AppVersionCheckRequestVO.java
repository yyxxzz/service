package com.yoho.gateway.model.shops;

public class AppVersionCheckRequestVO {
	
	private String client_type;
	
	private String app_version;

	public String getClient_type() {
		return client_type;
	}

	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	@Override
	public String toString() {
		return "AppVersionCheckRequestVO{" +
				"client_type='" + client_type + '\'' +
				", app_version='" + app_version + '\'' +
				'}';
	}
}
