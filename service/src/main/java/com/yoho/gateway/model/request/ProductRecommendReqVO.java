package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class ProductRecommendReqVO extends BaseBO{

	private static final long serialVersionUID = -1488166629246730561L;

	/**
	 * 用户ID
	 */
	private int uid;
	
	private int productSkn;
	
	/**
	 * 推荐位
	 */
	private String rec_pos;
	
	/**
	 * 返回总记录数
	 */
	private int total;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getProductSkn() {
		return productSkn;
	}

	public void setProductSkn(int productSkn) {
		this.productSkn = productSkn;
	}

	public String getRec_pos() {
		return rec_pos;
	}

	public void setRec_pos(String rec_pos) {
		this.rec_pos = rec_pos;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
