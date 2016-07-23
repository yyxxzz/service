package com.yoho.gateway.model.user.help;

import com.yoho.service.model.BaseBO;

/**
 * 帮助中心
 * @author yoho
 *
 */
public class HtmlContentReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1526056206780725966L;
	
	private int categoryId;
	
	private String code;

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
