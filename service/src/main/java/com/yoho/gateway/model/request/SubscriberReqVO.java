package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class SubscriberReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7646032974713320385L;
	
	private String email;
	
	private int uid;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

}
