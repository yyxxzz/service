package com.yoho.gateway.model.product;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 店铺
 */
public class ShopsVo {

	/**
     * 店铺ID
     */
	@JSONField(name="shops_id")
    private Integer shopsId;

    /**
     * 店铺名称
     */
	@JSONField(name="shop_name")
    private String shopName;

    /**
     * 店铺域名
     */
	@JSONField(name="shop_domain")
    private String shopDomain;

    /**
     * 店铺logo
     */
	@JSONField(name="shop_logo")
    private String shopLogo;

    /**
     * 店铺简介
     */
	@JSONField(name="shop_intro")
    private String shopIntro;

    /**
     * 店铺模式 1 单品店  2 多品店
     */
	@JSONField(name="mult_brand_shop_type")
    private String multBrandShopType;

	/**
     * 是否收藏
     */
	@JSONField(name="is_favorite")
    private String isFavorite;
	
	/**
     * 店铺模板类型 1 基础模板  2 经典模板
     */
	@JSONField(name="shop_template_type")
    private String shopTemplateType;
	
	/**
	 * 用于跳转的枚举值type： 店铺默认为2，为了和品牌列表保持一致
	 */
	@JSONField(name="shops_type")
    private String shopsType;
	
	/**
	 * 头部图片是否展示店铺名称
	 */
	@JSONField(name="is_show_shop_name")
	private String isShowShopName;
	
    public String getMultBrandShopType() {
		return multBrandShopType;
	}



	public void setMultBrandShopType(String multBrandShopType) {
		this.multBrandShopType = multBrandShopType;
	}



	public Integer getShopsId() {
        return shopsId;
    }

    public void setShopsId(Integer shopsId) {
        this.shopsId = shopsId;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName == null ? null : shopName.trim();
    }

    public String getShopDomain() {
        return shopDomain;
    }

    public void setShopDomain(String shopDomain) {
        this.shopDomain = shopDomain == null ? null : shopDomain.trim();
    }

    public String getShopLogo() {
        return shopLogo;
    }

    public void setShopLogo(String shopLogo) {
        this.shopLogo = shopLogo == null ? null : shopLogo.trim();
    }

    public String getShopIntro() {
        return shopIntro;
    }

    public void setShopIntro(String shopIntro) {
        this.shopIntro = shopIntro == null ? null : shopIntro.trim();
    }

    public String getShopsType() {
        return shopsType;
    }

    public void setShopsType(String shopsType) {
        this.shopsType = shopsType;
    }

	public String getShopTemplateType() {
		return shopTemplateType;
	}

	public void setShopTemplateType(String shopTemplateType) {
		this.shopTemplateType = shopTemplateType;
	}

	public String getIsFavorite() {
		return isFavorite;
	}

	public void setIsFavorite(String isFavorite) {
		this.isFavorite = isFavorite;
	}
	
	public String getIsShowShopName() {
		return isShowShopName;
	}

	public void setIsShowShopName(String isShowShopName) {
		this.isShowShopName = isShowShopName;
	}



	@Override
	public String toString() {
		return "ShopsVo [shopsId=" + shopsId + ", shopName=" + shopName
				+ ", shopDomain=" + shopDomain + ", shopLogo=" + shopLogo
				+ ", shopIntro=" + shopIntro + ", multBrandShopType="
				+ multBrandShopType + ", isFavorite=" + isFavorite
				+ ", shopTemplateType=" + shopTemplateType + ", shopsType="
				+ shopsType + ", isShowShopName=" + isShowShopName + "]";
	}
}