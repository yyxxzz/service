package com.yoho.gateway.model.product;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class ProductGiftDetailVo implements Serializable {
	private static final long serialVersionUID = 8467587291317520119L;
    
	@JSONField(name="attribute")
	private String attribute;

	@JSONField(name="product_id")
	private Integer productId;

	@JSONField(name="product_skn")
	private Integer productSkn;

	@JSONField(name="product_name")
	private String productName;

	@JSONField(name="market_price")
	private Integer marketPrice;

	@JSONField(name="salesPrice")
	private Integer salesPrice;

	@JSONField(name="special_price")
	private Integer specialPrice;

	@JSONField(name="format_market_price")
	private String formatMarketPrice;

	@JSONField(name="format_sales_price")
	private String formatSalesPrice;

	@JSONField(name="format_special_price")
	private String formatSpecialPrice;

	@JSONField(name="goods_list")
	private List<AddCostGiftVo> goodsList;
    
	@JSONField(name="max_select_number")
	private Integer maxSelectNum;

	@JSONField(name="is_favorite")
	private String isFavorite;

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getProductSkn() {
		return productSkn;
	}

	public void setProductSkn(Integer productSkn) {
		this.productSkn = productSkn;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(Integer marketPrice) {
		this.marketPrice = marketPrice;
	}

	public Integer getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(Integer salesPrice) {
		this.salesPrice = salesPrice;
	}

	public Integer getSpecialPrice() {
		return specialPrice;
	}

	public void setSpecialPrice(Integer specialPrice) {
		this.specialPrice = specialPrice;
	}

	public String getFormatMarketPrice() {
		return formatMarketPrice;
	}

	public void setFormatMarketPrice(String formatMarketPrice) {
		this.formatMarketPrice = formatMarketPrice;
	}

	public String getFormatSalesPrice() {
		return formatSalesPrice;
	}

	public void setFormatSalesPrice(String formatSalesPrice) {
		this.formatSalesPrice = formatSalesPrice;
	}

	public String getFormatSpecialPrice() {
		return formatSpecialPrice;
	}

	public void setFormatSpecialPrice(String formatSpecialPrice) {
		this.formatSpecialPrice = formatSpecialPrice;
	}

	public List<AddCostGiftVo> getGoodsList() {
		return goodsList;
	}

	public void setGoodsList(List<AddCostGiftVo> goodsList) {
		this.goodsList = goodsList;
	}

	public Integer getMaxSelectNum() {
		return maxSelectNum;
	}

	public void setMaxSelectNum(Integer maxSelectNum) {
		this.maxSelectNum = maxSelectNum;
	}

	public String getIsFavorite() {
		return isFavorite;
	}

	public void setIsFavorite(String isFavorite) {
		this.isFavorite = isFavorite;
	}
}
