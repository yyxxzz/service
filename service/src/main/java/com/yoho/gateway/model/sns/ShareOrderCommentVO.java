package com.yoho.gateway.model.sns;

import java.util.List;

import com.yoho.service.model.BaseBO;

public class ShareOrderCommentVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6575066003816090165L;
	
	private String orderId;
	
	private String orderCode;
	
    private String createTime;
	
    private List<ShareOrderGoodsVo> orderGoods;
    

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public List<ShareOrderGoodsVo> getOrderGoods() {
		return orderGoods;
	}

	public void setOrderGoods(List<ShareOrderGoodsVo> orderGoods) {
		this.orderGoods = orderGoods;
	}
}
