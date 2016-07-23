package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 分类信息VO
 * @author xieyong
 *
 */
public class CategoryVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4557710559871096394L;
	
	
	/**
	 * 分类ID
	 */
	@JSONField(name="category_id")
	private Integer categoryId;
	
	/**
	 * 分类名称
	 */
	@JSONField(name="category_name")
	private String  categoryName;

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}
}
