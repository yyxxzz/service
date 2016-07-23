package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 凑单商品价格VO
 * @author caoyan
 *
 */
public class TogetherProductPriceVo implements Serializable{
	
	private static final long serialVersionUID = 8467587291317523119L;
	
	@JSONField(name="market_price")
    private String marketPrice;
	
	@JSONField(name="sales_price")
    private String salesPrice;

	public String getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(String marketPrice) {
		this.marketPrice = marketPrice;
	}

	public String getSalesPrice() {
		return salesPrice;
	}

	public void setSalesPrice(String salesPrice) {
		this.salesPrice = salesPrice;
	}

}
