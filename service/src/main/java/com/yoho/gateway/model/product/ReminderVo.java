package com.yoho.gateway.model.product;

public class ReminderVo {
	
	/**
	 * 价格，如果字段为空前台会显示待定
	 */
	private String price;
	
	/**
	 * 商品的skn
	 */
	private Integer productSkn;
	
	/**
	 * 发售时间
	 */
	private String saleTime;
	
	/**
	 * 默认封面图
	 */
	private String defaultUrl;
	
	/**
	 * 商品名字
	 */
	private String productName;
	
	/**
	 * 限定商品详情页连接
	 */
	private String limitProductDetailUrl;
	
	/**
	 * 提醒ID主键
	 */
	private Integer reminderId;
	
	/**
	 * 限购商品唯一code
	 */
	private String limitProductCode;
	
	private Integer order;

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

	public String getSaleTime() {
		return saleTime;
	}

	public void setSaleTime(String saleTime) {
		this.saleTime = saleTime;
	}
	

	public String getDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getLimitProductDetailUrl() {
		return limitProductDetailUrl;
	}

	public void setLimitProductDetailUrl(String limitProductDetailUrl) {
		this.limitProductDetailUrl = limitProductDetailUrl;
	}

	public Integer getReminderId() {
		return reminderId;
	}

	public void setReminderId(Integer reminderId) {
		this.reminderId = reminderId;
	}

	public String getLimitProductCode() {
		return limitProductCode;
	}

	public void setLimitProductCode(String limitProductCode) {
		this.limitProductCode = limitProductCode;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
	
}
