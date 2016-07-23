package com.yoho.gateway.model.sns;

import com.yoho.service.model.BaseBO;

public class ArticleContentReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6999563723433992918L;
	
	private String article_id;
	
	private String uid;

	public String getArticle_id() {
		return article_id;
	}

	public void setArticle_id(String article_id) {
		this.article_id = article_id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

}
