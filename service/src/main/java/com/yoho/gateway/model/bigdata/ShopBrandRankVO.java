package com.yoho.gateway.model.bigdata;

import com.yoho.gateway.model.PageRequestBase;

public class ShopBrandRankVO{
	/**
	 * 店铺ID
	 */
	private String shopId;
	
	private String brandId;
	
	private String dateId;

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getDateId() {
		return dateId;
	}

	public void setDateId(String dateId) {
		this.dateId = dateId;
	}

	@Override
	public String toString() {
		return "ShopBrandRankVO{" +
				"shopId='" + shopId + '\'' +
				", brandId='" + brandId + '\'' +
				", dateId='" + dateId + '\'' +
				'}';
	}

}
