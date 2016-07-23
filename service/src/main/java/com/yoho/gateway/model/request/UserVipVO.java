package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class UserVipVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 68626804948395683L;

	// 用户UID
	private int uid;
	// 客户端类型
	private String client_type;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getClient_type() {
		return client_type;
	}

	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}
}
