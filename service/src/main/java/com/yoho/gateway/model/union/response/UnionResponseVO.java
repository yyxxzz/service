package com.yoho.gateway.model.union.response;

import com.yoho.service.model.BaseBO;

public class UnionResponseVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3053952821035055588L;

	private boolean isSuccess;
	private String msg;
	public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
}
