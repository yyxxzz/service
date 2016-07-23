package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class SuggestQueryReqVO extends BaseBO {

	private static final long serialVersionUID = 1995362910226699191L;

	private String udid;

	// 开始、结束时间, 格式为：yyyy-MM-dd
	private String start_time = "";
	private String end_time = "";
	// 状态
	private byte status = -1;
	// 是否有图片
	private int has_image = -1;
	// 是否可靠
	private int is_reliable = -1;
	// 客户端类型
	private String client_type = "all";

	// 分页
	private int page = 1;
	private int limit = 10;

	public String getUdid() {
		return udid;
	}

	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public int getHas_image() {
		return has_image;
	}

	public void setHas_image(int has_image) {
		this.has_image = has_image;
	}

	public int getIs_reliable() {
		return is_reliable;
	}

	public void setIs_reliable(int is_reliable) {
		this.is_reliable = is_reliable;
	}

	public String getClient_type() {
		return client_type;
	}

	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

}
