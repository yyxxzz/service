package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class HelpReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6232038165796861717L;

	private int id = 0;

	private String client_type = "h5";

	private short category_id = 0;

	public int getId() {
		return id;
	}

	public String getClient_type() {
		return client_type;
	}

	public short getCategory_id() {
		return category_id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}

	public void setCategory_id(short category_id) {
		this.category_id = category_id;
	}
}
