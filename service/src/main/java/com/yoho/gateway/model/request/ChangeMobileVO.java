package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class ChangeMobileVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6416313256219706464L;

	private String mobile;
	private String area;
	private String code;
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getArea() {
		return area;
	}
	public void setArea(String area) {
		this.area = area;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
