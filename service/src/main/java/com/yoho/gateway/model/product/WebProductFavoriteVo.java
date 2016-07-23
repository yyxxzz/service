package com.yoho.gateway.model.product;

import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * web收藏商品的Vo对象
 * @author caoyan
 *
 */
public class WebProductFavoriteVo extends ProductFavoriteVo{
	@JSONField(name="promotion_list")
	private List<PromotionVo> promotionList;
	
	@JSONField(name="fav_price")
	private String favPrice;
	
	@JSONField(name="is_subscribe_reduction")
	private String isSubscribeReduction;
	
	@JSONField(name="product_url")
	private String productUrl;
	
	@JSONField(name="is_join_promotion")
	private String isJoinPromotion;
	
	@JSONField(name="is_price_down")
	private String isPriceDown;

	public List<PromotionVo> getPromotionList() {
		return promotionList;
	}

	public void setPromotionList(List<PromotionVo> promotionList) {
		this.promotionList = promotionList;
	}

	public String getFavPrice() {
		return favPrice;
	}

	public void setFavPrice(String favPrice) {
		this.favPrice = favPrice;
	}

	public String getIsSubscribeReduction() {
		return isSubscribeReduction;
	}

	public void setIsSubscribeReduction(String isSubscribeReduction) {
		this.isSubscribeReduction = isSubscribeReduction;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public String getIsJoinPromotion() {
		return isJoinPromotion;
	}

	public void setIsJoinPromotion(String isJoinPromotion) {
		this.isJoinPromotion = isJoinPromotion;
	}

	public String getIsPriceDown() {
		return isPriceDown;
	}

	public void setIsPriceDown(String isPriceDown) {
		this.isPriceDown = isPriceDown;
	}
	
}
