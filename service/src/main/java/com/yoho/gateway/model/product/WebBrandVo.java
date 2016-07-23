package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * PC品牌 VO
 * @author caoyan
 *
 */
public class WebBrandVo implements Serializable{
	
	private static final long serialVersionUID = 5889754642766714834L;
	
	@JSONField(name="id")
	private Integer brandId;
	
	@JSONField(name="brand_name")
	private String brandName;
	
	@JSONField(name="brand_name_cn")
	private String brandNameCn;
	
	@JSONField(name="brand_name_en")
	private String brandNameEn;
	
	@JSONField(name="brandAlif")
	private String brandAlif;
	
	@JSONField(name="brand_domain")
	private String brandDomain;
	
	@JSONField(name="brand_ico")
    private String brandIco;
	
	@JSONField(name="brand_banner")
    private String brandBanner;
	
	@JSONField(name="brand_intro")
	private String brandIntro;
	
	@JSONField(name="static_content_code")
	private String staticContentCode;

	@JSONField(name="shop_id")
	private String shopId;
	
	/**
	 * 单个品牌店铺信息处理，无店铺、有单品店、无单品店有多品店
	 * 对应不同的跳转枚举值：
	 * 无店铺：0--->品牌页
	 * 无单品店有多品店：1--->搜索页
	 * 有单品店：2--->店铺页面
	 */
	@JSONField(name="type")
	private String type;
	
	/**
	 * 店铺模板类型，1：基础模板模板，2：标准模板
	 */
	@JSONField(name="shop_template_type")
	private String shopTemplateType;
	
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

	public String getBrandNameCn() {
		return brandNameCn;
	}

	public void setBrandNameCn(String brandNameCn) {
		this.brandNameCn = brandNameCn;
	}

	public String getBrandNameEn() {
		return brandNameEn;
	}

	public void setBrandNameEn(String brandNameEn) {
		this.brandNameEn = brandNameEn;
	}

	public String getBrandAlif() {
		return brandAlif;
	}

	public void setBrandAlif(String brandAlif) {
		this.brandAlif = brandAlif;
	}
	
	public String getBrandDomain() {
		return brandDomain;
	}

	public void setBrandDomain(String brandDomain) {
		this.brandDomain = brandDomain;
	}

	public String getBrandBanner() {
		return brandBanner;
	}

	public void setBrandBanner(String brandBanner) {
		this.brandBanner = brandBanner;
	}

	public String getBrandIntro() {
		return brandIntro;
	}

	public void setBrandIntro(String brandIntro) {
		this.brandIntro = brandIntro;
	}

	public String getStaticContentCode() {
		return staticContentCode;
	}

	public void setStaticContentCode(String staticContentCode) {
		this.staticContentCode = staticContentCode;
	}

	public String getShopId() {
		return shopId;
	}

	public void setShopId(String shopId) {
		this.shopId = shopId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getShopTemplateType() {
		return shopTemplateType;
	}

	public void setShopTemplateType(String shopTemplateType) {
		this.shopTemplateType = shopTemplateType;
	}
	
}
