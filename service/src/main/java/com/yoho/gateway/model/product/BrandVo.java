package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 品牌 VO
 * @author xieyong
 *
 */
public class BrandVo implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 5889754642766714834L;

	@JSONField(name="brand_id")
	private Integer brandId;

	@JSONField(name="brand_name")
	private String brandName;

	@JSONField(name="brand_ico")
	private String brandIco;
	@JSONField(name="brand_domain")
    private String brandDomain;

	@JSONField(name="id")
    private String id;

	@JSONField(name="is_hot")
    private String isHot;

	@JSONField(name="brand_alif")
    private String brandAlif;

	public String getBrandDomain() {
		return brandDomain;
	}

	public void setBrandDomain(String brandDomain) {
		this.brandDomain = brandDomain;
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
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIsHot() {
		return isHot;
	}

	public void setIsHot(String isHot) {
		this.isHot = isHot;
	}

	public String getBrandAlif() {
		return brandAlif;
	}

	public void setBrandAlif(String brandAlif) {
		this.brandAlif = brandAlif;
	}
}
