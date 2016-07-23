package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class BackpwdByEmailRequestVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2632595850236188541L;

	private String email;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
}
