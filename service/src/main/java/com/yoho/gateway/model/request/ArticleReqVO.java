package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

/**
 * 文章请求对象VO
 *
 */
public class ArticleReqVO extends BaseBO{
	private static final long serialVersionUID = -9213574544122740121L;

	// id
	private int id;

	/**
	 * 文章分类ID
	 */
	private int sortId;

	/**
	 * 性别 默认 1,2,3
	 */
	private String gender;

	/**
	 * 作者ID
	 */
	private int authorId;

	/**
     *
     */
	private String tag;

	/**
	 * 第几页
	 */
	private int page;

	/**
	 * 用户ID
	 */
	private int uid;

	/**
	 * 设备ID
	 */
	private String udid;

	/**
	 * 每页显示的记录数
	 */
	private int limit;
	
	/**
	 * 指定时间
	 */
	private String datetime;

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String dateTime) {
		this.datetime = dateTime;
	}

	/**
	 * 客户端类型
	 */
	private String client_Type;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getSortId() {
		return sortId;
	}

	public void setSortId(int sortId) {
		this.sortId = sortId;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public int getAuthorId() {
		return authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public String getClient_Type() {
		return client_Type;
	}

	public void setClient_Type(String client_Type) {
		this.client_Type = client_Type;
	}
}

