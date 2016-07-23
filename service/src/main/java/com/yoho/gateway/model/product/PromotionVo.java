package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class PromotionVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6497120502216521808L;
	
	@JSONField(name="promotion_title")
	private String promotionTitle;
	
	@JSONField(name="promotion_type")
	private String promotionType;

	public String getPromotionTitle() {
		return promotionTitle;
	}

	public void setPromotionTitle(String promotionTitle) {
		this.promotionTitle = promotionTitle;
	}

	public String getPromotionType() {
		return promotionType;
	}

	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}
}
