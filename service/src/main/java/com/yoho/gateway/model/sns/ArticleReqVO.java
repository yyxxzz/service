package com.yoho.gateway.model.sns;

import com.yoho.service.model.sns.model.BaseBO;

/**
 * 文章请求对象BO
 *
 * @author Created by dengxinfei on 16/2/21.
 */
public class ArticleReqVO extends BaseBO {
	private static final long serialVersionUID = -4139937465374390936L;

	// id
	private String id;
	// 文章分类ID
	private String sort_id;
	// 性别 默认 1,2,3
	private String gender;
	// 作者ID
	private String author_id;
	//
	private String tag;
	// 用户ID
	private String uid;
	// 设备ID
	private String udid;
	// 第几页
	private String page;
	// 每页显示的记录数
	private String limit;

	private String offset;

	// 指定时间
	private String dateTime;

	// 客户端类型
	private String client_type;

	public ArticleReqVO() {

	}

	public ArticleReqVO(String sort_id, String gender, String author_id, String tag, String page, String uid, String udid, String limit, String client_type) {
		this.sort_id = sort_id;
		this.gender = gender;
		this.author_id = author_id;
		this.tag = tag;
		this.page = page;
		this.uid = uid;
		this.udid = udid;
		this.limit = limit;
		this.client_type = client_type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSort_id() {
		return sort_id;
	}

	public void setSort_id(String sort_id) {
		this.sort_id = sort_id;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getAuthor_id() {
		return author_id;
	}

	public void setAuthor_id(String author_id) {
		this.author_id = author_id;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

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

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
	}

	public String getLimit() {
		return limit;
	}

	public void setLimit(String limit) {
		this.limit = limit;
	}

	public String getOffset() {
		return offset;
	}

	public void setOffset(String offset) {
		this.offset = offset;
	}

	public String getDateTime() {
		return dateTime;
	}

	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}

	public String getClient_type() {
		return client_type;
	}

	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}

	@Override
	public String toString() {
		return "ArticleReqVO{" +
				"sort_id='" + sort_id + '\'' +
				", gender='" + gender + '\'' +
				", page='" + page + '\'' +
				", client_type='" + client_type + '\'' +
				'}';
	}
}
