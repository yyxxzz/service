package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class RelatedMobileVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4445972938281438755L;
	
	private String mobile;
	
	private String area;
	
	private String openId;
	
	private String source_type;
	
	private String shopping_key;

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public String getSource_type() {
		return source_type;
	}

	public void setSource_type(String source_type) {
		this.source_type = source_type;
	}

	public String getShopping_key() {
		return shopping_key;
	}

	public void setShopping_key(String shopping_key) {
		this.shopping_key = shopping_key;
	}
	

}
