package com.yoho.gateway.model.product;

import java.util.List;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;


public class ConsultRspPageVO {
	private int page;
	private int page_total;
	private int total;
	private List<ConsultRspVO> list;
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
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public List<ConsultRspVO> getList() {
		return list;
	}
	public void setList(List<ConsultRspVO> list) {
		this.list = list;
	}
	
	public ConsultRspPageVO() {
	}
	
	public ConsultRspPageVO(int page, List<ConsultRspVO> list) {
		super();
		this.page = page;
		this.list = list;
	}
	public ConsultRspPageVO(int page, int page_total, int total,
			List<ConsultRspVO> list) {
		super();
		this.page = page;
		this.page_total = page_total;
		this.total = total;
		this.list = list;
	}
	public ConsultRspPageVO(int page, int total, List<ConsultRspVO> list) {
		super();
		this.page = page;
		this.total = total;
		this.list = list;
	}
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
