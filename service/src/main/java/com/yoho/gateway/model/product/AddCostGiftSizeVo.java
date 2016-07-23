package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class AddCostGiftSizeVo  implements Serializable {
	private static final long serialVersionUID = 8267587291317523112L;
	
	@JSONField(name="product_sku")
	private Integer productSku;

	@JSONField(name="storage_number")
	private Integer storageNum;

	@JSONField(name="size_name")
	private String sizeName;

	@JSONField(name="size_num")
	private String sizeNum;

	@JSONField(name="size_id")
	private Integer sizeId;

	@JSONField(name="order_by")
	private Integer orderBy;

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public Integer getProductSku() {
		return productSku;
	}

	public void setProductSku(Integer productSku) {
		this.productSku = productSku;
	}

	public Integer getStorageNum() {
		return storageNum;
	}

	public void setStorageNum(Integer storageNum) {
		this.storageNum = storageNum;
	}

	public String getSizeNum() {
		return sizeNum;
	}

	public void setSizeNum(String sizeNum) {
		this.sizeNum = sizeNum;
	}

	public Integer getSizeId() {
		return sizeId;
	}

	public void setSizeId(Integer sizeId) {
		this.sizeId = sizeId;
	}

	public Integer getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

}
