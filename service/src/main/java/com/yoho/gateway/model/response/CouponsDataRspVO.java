package com.yoho.gateway.model.response;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class CouponsDataRspVO {

	/**
	 * 返回纪录列表
	 */
	private Object info;

	/**
	 * 返回纪录长度
	 */
	private int total;

	public CouponsDataRspVO() {

	}

	public CouponsDataRspVO(Object info, int total) {
		this.info = info;
		this.total = total;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public Object getInfo() {
		return info;
	}

	public void setInfo(Object info) {
		this.info = info;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

}
