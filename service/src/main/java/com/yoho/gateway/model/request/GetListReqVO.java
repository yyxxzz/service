package com.yoho.gateway.model.request;

import com.yoho.service.model.request.PageReqBO;


public class GetListReqVO extends PageReqBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2343708982958158951L;

	private int uid;
	private int size;
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
}
