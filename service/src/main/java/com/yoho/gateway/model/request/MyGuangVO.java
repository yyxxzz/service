package com.yoho.gateway.model.request;

import com.yoho.service.model.request.PageReqBO;

public class MyGuangVO extends PageReqBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 31575654606711378L;

	/**
	 * 用户ID
	 */
	private int uid;

	private String udid;

	/**
	 * 文章ID
	 */
	private String article_id;

	private String client_type;

	private String app_version;

	public String getUdid() {
		return udid;
	}

	public String getClient_type() {
		return client_type;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}

	public String getArticle_id() {
		return article_id;
	}

	public void setArticle_id(String article_id) {
		this.article_id = article_id;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	@Override
	public String toString() {
		return "MyGuangVO{uid=" + uid   + ", client_type='" + client_type + "', app_version='" + app_version + "', page=" + this.getPage()
				+ ", limit=" + this.getLimit() + "}";
	}

}
