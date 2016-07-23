package com.yoho.gateway.model.sns;

import com.yoho.service.model.BaseBO;

public class CollectBrandContReqVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4922455128946392762L;

	private String brand_id;

	private String uid;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getBrand_id() {
		return brand_id;
	}

	public void setBrand_id(String brand_id) {
		this.brand_id = brand_id;
	}

}
