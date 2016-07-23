package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class AddCostGiftImageVo implements Serializable {

	private static final long serialVersionUID = 8267587291317523112L;
	
	@JSONField(name = "image_url")
	private String imageUrl;

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

}
