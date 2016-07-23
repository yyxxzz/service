package com.yoho.gateway.model.bigdata;

import com.yoho.gateway.model.PageRequestBase;

public class StorageStatisticsVO extends PageRequestBase{
	
	private Integer productSkn;
	
	private String factory_code;
	
	private Integer productSku;
	
	private Integer shopId;
	
	private Integer brandId;
	
	private Integer maxSortId;
	
	private Integer middleSortId;
	
	private Integer smallSortId;

	public Integer getProductSkn() {
		return productSkn;
	}

	public void setProductSkn(Integer productSkn) {
		this.productSkn = productSkn;
	}

	public String getFactory_code() {
		return factory_code;
	}

	public void setFactory_code(String factory_code) {
		this.factory_code = factory_code;
	}

	public Integer getProductSku() {
		return productSku;
	}

	public void setProductSku(Integer productSku) {
		this.productSku = productSku;
	}

	public Integer getShopId() {
		return shopId;
	}

	public void setShopId(Integer shopId) {
		this.shopId = shopId;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public Integer getMaxSortId() {
		return maxSortId;
	}

	public void setMaxSortId(Integer maxSortId) {
		this.maxSortId = maxSortId;
	}

	public Integer getMiddleSortId() {
		return middleSortId;
	}

	public void setMiddleSortId(Integer middleSortId) {
		this.middleSortId = middleSortId;
	}

	public Integer getSmallSortId() {
		return smallSortId;
	}

	public void setSmallSortId(Integer smallSortId) {
		this.smallSortId = smallSortId;
	}

	@Override
	public String toString() {
		return "StorageStatisticsVO{" +
				"productSkn=" + productSkn +
				", factory_code='" + factory_code + '\'' +
				", productSku=" + productSku +
				", shopId=" + shopId +
				", brandId=" + brandId +
				", maxSortId=" + maxSortId +
				", middleSortId=" + middleSortId +
				", smallSortId=" + smallSortId +
				'}';
	}
}
