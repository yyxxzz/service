package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class UserContactsVO extends BaseBO {
	private static final long serialVersionUID = 7565780715788194166L;

	private Integer uid;

	private String qq;

	private String msn;

	private String mobile;

	private String phone;

	private Integer area_code;

	private String full_address;

	private String zip_code;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getMsn() {
		return msn;
	}

	public void setMsn(String msn) {
		this.msn = msn;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Integer getArea_code() {
		return area_code;
	}

	public void setArea_code(Integer area_code) {
		this.area_code = area_code;
	}

	public String getFull_address() {
		return full_address;
	}

	public void setFull_address(String full_address) {
		this.full_address = full_address;
	}

	public String getZip_code() {
		return zip_code;
	}

	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
	}

}
