package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class UserHabitsVO extends BaseBO {
	private static final long serialVersionUID = 5331230949629634362L;

	private Integer uid;

	private String shopping;

	private String dress;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getShopping() {
		return shopping;
	}

	public void setShopping(String shopping) {
		this.shopping = shopping;
	}

	public String getDress() {
		return dress;
	}

	public void setDress(String dress) {
		this.dress = dress;
	}

}
