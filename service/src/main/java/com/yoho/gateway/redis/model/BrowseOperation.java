package com.yoho.gateway.redis.model;

import com.yoho.service.model.BaseBO;

public class BrowseOperation extends BaseBO {
	private static final long serialVersionUID = 7830764199066713356L;

	private String product_skn;
	private String category_id;
	private long time;

	public BrowseOperation() {
	}

	public BrowseOperation(String product_skn, String category_id) {
		this.product_skn = product_skn;
		this.category_id = category_id;
		this.time = System.currentTimeMillis();
	}

	public BrowseOperation(String product_skn, String category_id, long time) {
		this.product_skn = product_skn;
		this.category_id = category_id;
		this.time = time;
	}

	public String getProduct_skn() {
		return product_skn;
	}

	public void setProduct_skn(String product_skn) {
		this.product_skn = product_skn;
	}

	public String getCategory_id() {
		return category_id;
	}

	public void setCategory_id(String category_id) {
		this.category_id = category_id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof BrowseOperation) {
			return this.product_skn.equals(((BrowseOperation) obj).getProduct_skn());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return product_skn.hashCode();
	}

}
