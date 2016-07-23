package com.yoho.gateway.model.user.request.activity;

import com.yoho.service.model.BaseBO;

public class GetShareRequestVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2115253958956848628L;

	private int act_id;
	private int uid;
	public int getAct_id() {
		return act_id;
	}
	public void setAct_id(int act_id) {
		this.act_id = act_id;
	}
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
}
