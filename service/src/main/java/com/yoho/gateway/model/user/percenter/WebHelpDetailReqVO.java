package com.yoho.gateway.model.user.percenter;

import com.yoho.service.model.BaseBO;
import com.yoho.service.model.request.PageReqBO;

public class WebHelpDetailReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 13249908633362103L;
	
	private String category_id;
	
	private String problem;
	
	/**
	 * 每页几条
	 */
	private int limit = 10;
	
	/**
	 * 当前第几页
	 */
	private int page = 1;

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public String getProblem() {
		return problem;
	}

	public void setProblem(String problem) {
		this.problem = problem;
	}

}
