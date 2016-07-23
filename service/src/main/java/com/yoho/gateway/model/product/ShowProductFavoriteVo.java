package com.yoho.gateway.model.product;

import java.util.Map;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * pc端收藏商品的Vo对象
 * @author wangshusheng
 *
 */
public class ShowProductFavoriteVo{
	
	@JSONField(name="skn")
	private String skn;
	
	@JSONField(name="product_id")
	private String product_id;
	
	@JSONField(name="product_name")
	private String product_name;
	
	@JSONField(name="brand_id")
	private String brandId;
	
	@JSONField(name="brand_name")
	private String brandName;
	
	@JSONField(name="max_sort_id")
	private String maxSortId;
	
	@JSONField(name="middle_sort_id")
	private String middleSortId;
	
	@JSONField(name="small_sort_id")
	private String smallSortId;
	
	@JSONField(name="url")
	private String productUrl;
	
	/**
	 * 收藏商品记录的id
	 */
	@JSONField(name="id")
	private String favorId;

	@JSONField(name="uid")
	private String uid;
	
	@JSONField(name="status")
	private String status;
	/**
	 * 收藏商品记录的时间
	 */
	@JSONField(name="create_time")
	private String createTime;

	@JSONField(name="image")
	private Map<String,String> imageMap;
	
	@JSONField(name="price")
	private Map<String,String> priceMap;
	
	@JSONField(name="product_thumb")
	private String productThumb;
	
	public String getSkn() {
		return skn;
	}

	public void setSkn(String skn) {
		this.skn = skn;
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

	public String getMaxSortId() {
		return maxSortId;
	}

	public void setMaxSortId(String maxSortId) {
		this.maxSortId = maxSortId;
	}

	public String getMiddleSortId() {
		return middleSortId;
	}

	public void setMiddleSortId(String middleSortId) {
		this.middleSortId = middleSortId;
	}

	public String getSmallSortId() {
		return smallSortId;
	}

	public void setSmallSortId(String smallSortId) {
		this.smallSortId = smallSortId;
	}

	public String getProductUrl() {
		return productUrl;
	}

	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}

	public String getFavorId() {
		return favorId;
	}

	public void setFavorId(String favorId) {
		this.favorId = favorId;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Map<String, String> getImageMap() {
		return imageMap;
	}

	public void setImageMap(Map<String, String> imageMap) {
		this.imageMap = imageMap;
	}

	public String getProductThumb() {
		return productThumb;
	}

	public void setProductThumb(String productThumb) {
		this.productThumb = productThumb;
	}

	public Map<String, String> getPriceMap() {
		return priceMap;
	}

	public void setPriceMap(Map<String, String> priceMap) {
		this.priceMap = priceMap;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ShowProductFavoriteVo [skn=" + skn + ", product_id="
				+ product_id + ", product_name=" + product_name + ", brandId="
				+ brandId + ", brandName=" + brandName + ", maxSortId="
				+ maxSortId + ", middleSortId=" + middleSortId
				+ ", smallSortId=" + smallSortId + ", productUrl=" + productUrl
				+ ", favorId=" + favorId + ", uid=" + uid + ", status="
				+ status + ", createTime=" + createTime + ", imageMap="
				+ imageMap + ", priceMap=" + priceMap + ", productThumb="
				+ productThumb + "]";
	}


}
