package com.yoho.gateway.model.search;


public class BigDataSearchReq extends ProductSearchReq {

	/**
	 * 设备号
	 */
	private String udid;
	
	/**
	 * 推荐位
	 */
	private String recPos;
	
	/**
	 * 商品SKN
	 */
	private Integer productSkn;

	
	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}
	
	public String getRecPos() {
		return recPos;
	}

	public void setRecPos(String recPos) {
		this.recPos = recPos;
	}

	public Integer getProductSkn() {
		return productSkn;
	}

	public void setProductSkn(Integer productSkn) {
		this.productSkn = productSkn;
	}

	@Override
	public String toString() {
		return "BigDataSearchReq [udid=" + udid + ", recPos=" + recPos
				+ ", productSkn=" + productSkn + "]";
	}
}
