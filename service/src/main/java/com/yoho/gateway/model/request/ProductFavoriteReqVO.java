package com.yoho.gateway.model.request;

import com.yoho.service.model.request.PageReqBO;


public class ProductFavoriteReqVO extends PageReqBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1488166629246730561L;

	/**
	 * 用户ID
	 */
	private int uid;
	
	/**
	 * sso用户ID
	 */
	private int sso_uid;
	
	/**
	 * 商品所属的二级分类id
	 */
	private String sortId;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getSortId() {
		return sortId;
	}

	public void setSortId(String sortId) {
		this.sortId = sortId;
	}

	@Override
	public String toString() {
		return "ProductFavoriteReqVO [uid=" + uid + ", sortId=" + sortId + "]";
	}

	public int getSso_uid() {
		return sso_uid;
	}

	public void setSso_uid(int sso_uid) {
		this.sso_uid = sso_uid;
	}
	
}
