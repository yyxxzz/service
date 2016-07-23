package com.yoho.gateway.model.user.request.activity;

import com.yoho.service.model.BaseBO;

public class AddShareRequestVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8118843126166611849L;

	private int act_id;
	private int uid;
	private String url;
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
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
}
