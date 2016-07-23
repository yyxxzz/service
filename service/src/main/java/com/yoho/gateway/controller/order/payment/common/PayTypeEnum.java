package com.yoho.gateway.controller.order.payment.common;

public enum PayTypeEnum {
	ALIPAY("alipay"),
	WECAHT("wechat"),
	PCPAY("pcpay");
	
	private String name;
	
	private PayTypeEnum(String payType) {
		name = payType;
	}
	
	public String getName() {
		return name;
	}
}
