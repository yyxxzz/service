package com.yoho.gateway.model.browse;

import com.yoho.service.model.BaseBO;

public class CategoryVO extends BaseBO {
	private static final long serialVersionUID = -5860260542545493714L;

	// 分类ID
	private int category_id;
	// 分类名称
	private String category_name;

	public CategoryVO() {
	}

	public CategoryVO(int category_id) {
		this.category_id = category_id;
	}

	public CategoryVO(int category_id, String category_name) {
		this.category_id = category_id;
		this.category_name = category_name;
	}

	public int getCategory_id() {
		return category_id;
	}

	public void setCategory_id(int category_id) {
		this.category_id = category_id;
	}

	public String getCategory_name() {
		return category_name;
	}

	public void setCategory_name(String category_name) {
		this.category_name = category_name;
	}

	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}
		if (this == obj) {
			return true;
		}
		if (obj instanceof CategoryVO) {
			return category_id == ((CategoryVO) obj).getCategory_id();
		}
		return false;
	}

	public int hashCode() {
		return category_id;
	}

}
