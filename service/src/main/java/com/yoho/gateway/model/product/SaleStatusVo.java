package com.yoho.gateway.model.product;

import java.io.Serializable;

public class SaleStatusVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3478420703241874101L;

	private int saleStatus;
	
	private String limitProdutSku;
	
	public SaleStatusVo(){
	}
	
	public SaleStatusVo(int saleStatus){
		this.saleStatus = saleStatus;
	}
	
	public SaleStatusVo(int saleStatus, String limitProdutSku){
		this.saleStatus = saleStatus;
		this.limitProdutSku = limitProdutSku;
	}

	public int getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(int saleStatus) {
		this.saleStatus = saleStatus;
	}

	public String getLimitProdutSku() {
		return limitProdutSku;
	}

	public void setLimitProdutSku(String limitProdutSku) {
		this.limitProdutSku = limitProdutSku;
	}
}
