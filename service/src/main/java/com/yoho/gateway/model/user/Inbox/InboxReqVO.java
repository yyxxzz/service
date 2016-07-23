package com.yoho.gateway.model.user.Inbox;

import com.yoho.service.model.BaseBO;

public class InboxReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4345943107177817775L;
	
	private String uid;
	
	private String send_uid;
	
	private String verify_key;
	
	private String content;
	
	private String type;
	
	private String title;

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSend_uid() {
		return send_uid;
	}

	public void setSend_uid(String send_uid) {
		this.send_uid = send_uid;
	}

	public String getVerify_key() {
		return verify_key;
	}

	public void setVerify_key(String verify_key) {
		this.verify_key = verify_key;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
