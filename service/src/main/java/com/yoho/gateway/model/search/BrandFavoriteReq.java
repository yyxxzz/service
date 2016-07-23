package com.yoho.gateway.model.search;

import java.io.Serializable;

/**
 * Created by caoyan on 2015/12/3.
 */
public class BrandFavoriteReq implements Serializable{
	
	private static final long serialVersionUID = -6433563625638337373L;
	
	private Integer uid;
	
    private Integer brandId;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

    
}
