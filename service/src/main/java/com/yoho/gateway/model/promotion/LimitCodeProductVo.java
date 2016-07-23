package com.yoho.gateway.model.promotion;

/**
 * 我的限购码，关联限定商品
 *
 * @author wangshusheng
 * @Time 2016/2/17
 */
public class LimitCodeProductVo {

	/*
	 * 限购码
	 */
	private String limitCode;
	
	/*
	 * 限售商品code
	 */
	private String limitProductCode;
	
	/**
	 * 排队关联的活动的ID
	 */
	private Integer activityId;
	
	/**
     * 关联skn
     */
	private Integer productSkn;
	
	/**
	 * 选择的sku
	 */
	private Integer productSku;
	
	/*
	 * 状态，0：失效，1：正常
	 */
	private int status;
	
	/**
     * 限定商品名称
     */
	private String productName;
	
	/**
     * 商品价格
     */
	private String price;
	
	/**
     * 图片url
     */
	private String defaultUrl;

	/**
	 * SKU时返回的颜色
	 */
	private String color_name;

	/**
	 * SKU是返回的尺码
	 */
	private String size_name;

	public String getLimitCode() {
		return limitCode;
	}

	public void setLimitCode(String limitCode) {
		this.limitCode = limitCode;
	}

	public String getLimitProductCode() {
		return limitProductCode;
	}

	public void setLimitProductCode(String limitProductCode) {
		this.limitProductCode = limitProductCode;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Integer getProductSkn() {
		return productSkn;
	}

	public void setProductSkn(Integer productSkn) {
		this.productSkn = productSkn;
	}

	@Override
	public String toString() {
		return "LimitCodeProductVo [limitCode=" + limitCode
				+ ", limitProductCode=" + limitProductCode + ", productSkn="
				+ productSkn + ", status=" + status + ", productName="
				+ productName + ", price=" + price + ", defaultUrl=" + defaultUrl
				+ "]";
	}

	public String getDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getColor_name() {
		return color_name;
	}

	public void setColor_name(String color_name) {
		this.color_name = color_name;
	}

	public String getSize_name() {
		return size_name;
	}

	public void setSize_name(String size_name) {
		this.size_name = size_name;
	}

	public Integer getProductSku() {
		return productSku;
	}

	public void setProductSku(Integer productSku) {
		this.productSku = productSku;
	}
	
}
