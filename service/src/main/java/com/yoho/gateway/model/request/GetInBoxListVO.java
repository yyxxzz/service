package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class GetInBoxListVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2375481653721638022L;

	private int uid;
	private int size;
	private int page;
    private String gender;
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
}
