package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class UserAddressReqVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7298690022159476029L;

	/**
	 * 主键id
	 */
	private Integer id;

	/**
	 * 用户id
	 */
	private Integer uid;

	/**
	 * 用户名
	 */
	private String addressee_name;

	/**
	 * 地址
	 */
	private String address;

	/**
	 * 地区码
	 */
	private String area_code;

	/**
	 * 邮编
	 */
	private String zip_code;

	/**
	 * 电话
	 */
	private String mobile;

	/**
	 * 手机
	 */
	private String phone;

	/**
	 * 是否为默认地址，默认是N：不是默认地址
	 */
	private String is_default;

	/**
	 * email
	 */
	private String email;

	// 分页：页数和每页记录数
	private int page;
	private int limit;

	// adressee_name 别名
	private String consignee;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getAddressee_name() {
		return addressee_name;
	}

	public void setAddressee_name(String addressee_name) {
		this.addressee_name = addressee_name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getZip_code() {
		return zip_code;
	}

	public void setZip_code(String zip_code) {
		this.zip_code = zip_code;
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

	public String getIs_default() {
		return is_default;
	}

	public void setIs_default(String is_default) {
		this.is_default = is_default;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getConsignee() {
		return consignee;
	}

	public void setConsignee(String consignee) {
		this.consignee = consignee;
	}

}