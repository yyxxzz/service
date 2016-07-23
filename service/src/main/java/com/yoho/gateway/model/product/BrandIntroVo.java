package com.yoho.gateway.model.product;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 品牌介绍 VO
 * @author caoyan
 *
 */
public class BrandIntroVo implements Serializable{
	
	private static final long serialVersionUID = 5889754642766714834L;
	
	@JSONField(name="is_favorite")
	private String isFavorite;
	
	@JSONField(name="brand_id")
	private String brandId;
	
	@JSONField(name="brand_name")
    private String brandName;
	
	@JSONField(name="brand_intro")
	private String brandIntro;
	
	@JSONField(name="brand_ico")
	private String brandIco;
	
	@JSONField(name="coupons")
	private List<CouponsVo> couponsVoList;

	@JSONField(name="brand_domain")
	private String brandDomain;
	
	public String getIsFavorite() {
		return isFavorite;
	}

	public void setIsFavorite(String isFavorite) {
		this.isFavorite = isFavorite;
	}

	public String getBrandId() {
		return brandId;
	}

	public void setBrandId(String brandId) {
		this.brandId = brandId;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandIntro() {
		return brandIntro;
	}

	public void setBrandIntro(String brandIntro) {
		this.brandIntro = brandIntro;
	}

	public String getBrandIco() {
		return brandIco;
	}

	public void setBrandIco(String brandIco) {
		this.brandIco = brandIco;
	}

	public List<CouponsVo> getCouponsVoList() {
		return couponsVoList;
	}

	public void setCouponsVoList(List<CouponsVo> couponsVoList) {
		this.couponsVoList = couponsVoList;
	}

	public String getBrandDomain() {
		return brandDomain;
	}

	public void setBrandDomain(String brandDomain) {
		this.brandDomain = brandDomain;
	}
	
	

}
