package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class YohoCoinLogVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1307222867556610171L;

	// 用户UID
	private int uid;
	// 每页记录数
	private int limit;
	// 页数
	private int page;
	
	// 查询类型（0-所有；1-收入；2-支出）
	private int queryType = 0;
	
	private String beginTime;
	private String endTime;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

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

	public int getQueryType() {
		return queryType;
	}

	public void setQueryType(int queryType) {
		this.queryType = queryType;
	}

	public String getBeginTime() {
		return beginTime;
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	
}
