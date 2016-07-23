package com.yoho.gateway.model.user.request.activity;

import com.yoho.service.model.BaseBO;

public class BindAccountRequestVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5917496621685742091L;

	private int uid;
	private int type;
	private String opend_id;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getOpend_id() {
		return opend_id;
	}
	public void setOpend_id(String opend_id) {
		this.opend_id = opend_id;
	}
}
