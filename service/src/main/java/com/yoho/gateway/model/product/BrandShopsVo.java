package com.yoho.gateway.model.product;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 商品详情页：品牌或店铺
 */
public class BrandShopsVo {

	/**
	 * 店铺ID
	 */
	@JSONField(name="shop_id")
	private Integer shopId;

	/**
	 * 品牌ID
	 */
	@JSONField(name="brand_id")
	private Integer brandId;

	/**
	 * 品牌、店铺名称
	 */
	@JSONField(name="brand_name")
	private String brandName;

	@JSONField(name="brand_ico")
	private String brandIco;

	@JSONField(name="brand_domain")
	private String brandDomain;
	
	/**
     * 店铺模板类型 1 基础模板  2 经典模板
     */
	@JSONField(name="shop_template_type")
    private String shopTemplateType;

	public String getShopTemplateType() {
		return shopTemplateType;
	}

	public void setShopTemplateType(String shopTemplateType) {
		this.shopTemplateType = shopTemplateType;
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

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandIco() {
		return brandIco;
	}

	public void setBrandIco(String brandIco) {
		this.brandIco = brandIco;
	}

	public String getBrandDomain() {
		return brandDomain;
	}

	public void setBrandDomain(String brandDomain) {
		this.brandDomain = brandDomain;
	}

	@Override
	public String toString() {
		return "BrandShopsVo [shopId=" + shopId + ", brandId=" + brandId
				+ ", brandName=" + brandName + ", brandIco=" + brandIco
				+ ", brandDomain=" + brandDomain + "]";
	}

}