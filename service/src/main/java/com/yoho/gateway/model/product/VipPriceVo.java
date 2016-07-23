package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * @author xieyong
 *
 */
public class VipPriceVo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2377733912666764274L;
	/**
	 * 会员等级名称
	 */
	@JSONField(name="caption")
	private String vipTitle;
	/**
	 * vip价格
	 */
	@JSONField(name="price")
	private String vipPrice;
	
	public String getVipTitle() {
		return vipTitle;
	}
	public void setVipTitle(String vipTitle) {
		this.vipTitle = vipTitle;
	}
	public String getVipPrice() {
		return vipPrice;
	}
	public void setVipPrice(String vipPrice) {
		this.vipPrice = vipPrice;
	}
	
	
}
