package com.yoho.gateway.model.response;

import java.util.List;

import com.yoho.service.model.BaseBO;

public class SuggestPageRspVO extends BaseBO {

	private static final long serialVersionUID = -4630630309241914587L;

	private int unedit_count;
	private int edited_count;
	private int publish_count;

	private int total;
	private int page;
	private int page_total;

	private List<SuggestQueryRspVO> list;

	public int getUnedit_count() {
		return unedit_count;
	}

	public void setUnedit_count(int unedit_count) {
		this.unedit_count = unedit_count;
	}

	public int getEdited_count() {
		return edited_count;
	}

	public void setEdited_count(int edited_count) {
		this.edited_count = edited_count;
	}

	public int getPublish_count() {
		return publish_count;
	}

	public void setPublish_count(int publish_count) {
		this.publish_count = publish_count;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPage_total() {
		return page_total;
	}

	public void setPage_total(int page_total) {
		this.page_total = page_total;
	}

	public List<SuggestQueryRspVO> getList() {
		return list;
	}

	public void setList(List<SuggestQueryRspVO> list) {
		this.list = list;
	}

}
