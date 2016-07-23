package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 商品图片信息VO
 * @author xieyong
 *
 */
public class GoodsImagesVo implements Serializable{


    /**
	 * 
	 */
	private static final long serialVersionUID = -3718268194330117796L;
	
	@JSONField(name="image_url")
	private String imageUrl;


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

    	
}
