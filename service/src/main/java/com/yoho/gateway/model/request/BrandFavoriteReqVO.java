package com.yoho.gateway.model.request;

import com.yoho.service.model.request.PageReqBO;

public class BrandFavoriteReqVO extends PageReqBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5871399357788199523L;

	/**
	 * 用户id
	 */
	private int uid;
	
	/**
	 * 查询多少条
	 */
	private int size;
	
	private String gender = "1,3";
	
	/**
	 * 是否显示商品
	 */
	private boolean shop_product = true;
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
	public boolean getShop_product() {
		return shop_product;
	}
	public void setShop_product(boolean shop_product) {
		this.shop_product = shop_product;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	
}
