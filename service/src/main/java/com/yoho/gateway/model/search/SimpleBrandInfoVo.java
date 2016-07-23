package com.yoho.gateway.model.search;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 品牌的简单信息对象Vo
 * @author mali
 *
 */
public class SimpleBrandInfoVo implements Serializable{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -2392645015283429049L;
	
	/**
	 * 主键
	 */
	@JSONField(name = "id")
	private Integer id;
	
	/**
	 * 品牌名称
	 */
	@JSONField(name = "brand_name")
    private String brandName;

    /**
     * 区域
     */
	@JSONField(name = "brand_domain")
    private String brandDomain;
    
    /**
     * 品牌LOGO
     */
	@JSONField(name = "brand_ico")
    private String brandIco;

    /**
     * 品牌图标
     */
    @JSONField(name = "brand_banner")
    private String brandBanner;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandDomain() {
		return brandDomain;
	}

	public void setBrandDomain(String brandDomain) {
		this.brandDomain = brandDomain;
	}

	public String getBrandIco() {
		return brandIco;
	}

	public void setBrandIco(String brandIco) {
		this.brandIco = brandIco;
	}

	public String getBrandBanner() {
		return brandBanner;
	}

	public void setBrandBanner(String brandBanner) {
		this.brandBanner = brandBanner;
	}

	@Override
	public String toString() {
		return "SimpleBrandInfo [id=" + id + ", brandName=" + brandName
				+ ", brandDomain=" + brandDomain + ", brandIco=" + brandIco
				+ ", brandBanner=" + brandBanner + "]";
	}
}
