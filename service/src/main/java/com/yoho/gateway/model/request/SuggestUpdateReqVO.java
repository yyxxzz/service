package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class SuggestUpdateReqVO extends BaseBO {

	private static final long serialVersionUID = 6186024255836794889L;

	private String uid = "";
	private String udid;
	private int suggest_id;
	private byte is_reliable;
	private byte suggest_type = 2;
	private String client_type = "";
	private String os_version = "";
	private String app_version = "";
	private String image = "";
	private String content;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public int getSuggest_id() {
		return suggest_id;
	}

	public void setSuggest_id(int suggest_id) {
		this.suggest_id = suggest_id;
	}

	public byte getIs_reliable() {
		return is_reliable;
	}

	public void setIs_reliable(byte is_reliable) {
		this.is_reliable = is_reliable;
	}

	public byte getSuggest_type() {
		return suggest_type;
	}

	public void setSuggest_type(byte suggest_type) {
		this.suggest_type = suggest_type;
	}

	public String getClient_type() {
		return client_type;
	}

	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}

	public String getOs_version() {
		return os_version;
	}

	public void setOs_version(String os_version) {
		this.os_version = os_version;
	}

	public String getApp_version() {
		return app_version;
	}

	public void setApp_version(String app_version) {
		this.app_version = app_version;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
