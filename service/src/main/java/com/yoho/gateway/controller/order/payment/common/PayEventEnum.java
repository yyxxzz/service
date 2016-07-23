package com.yoho.gateway.controller.order.payment.common;

public enum PayEventEnum {
	INIT("INIT"),
	VER_FAILED("VER_FAILED"),
	PROCESS_FAILED("PROCESS_FAILED"),
	SUCCESS("SUCCESS");
	
	private String name;

	public String getName() {
		return name;
	}
	
	private PayEventEnum(String eventType) {
		this.name = eventType;
	}
}
