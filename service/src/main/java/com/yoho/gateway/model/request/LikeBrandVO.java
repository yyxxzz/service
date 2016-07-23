package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class LikeBrandVO extends BaseBO {
	private static final long serialVersionUID = -6522943980445639208L;

	private Integer uid;

	private String brand;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

}
