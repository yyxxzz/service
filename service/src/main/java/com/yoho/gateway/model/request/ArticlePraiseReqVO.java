package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class ArticlePraiseReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7420099749812283414L;

	/**
	 * 文章ID
	 */
	private int article_id;
	
	/**
	 * 设备ID
	 */
	private String udid;

	public int getArticle_id() {
		return article_id;
	}

	public void setArticle_id(int articleId) {
		this.article_id = articleId;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}
}
