package com.yoho.gateway.model.browse;

import java.util.List;

import com.yoho.service.model.BaseBO;

public class ProductBrowseRespVO extends BaseBO {
	private static final long serialVersionUID = -6217006724256289633L;

	// 商品信息列表
	private List<ProductVO> product_list;
	// 商品二级分类列表
	private List<CategoryVO> category_list;
	// 总页数
	private int page_total;
	// 总数
	private int total;
	// 当前页记录数
	private int page_size;
	// 当前页数
	private int page;

	public ProductBrowseRespVO() {
	}

	public ProductBrowseRespVO(List<ProductVO> product_list, List<CategoryVO> category_list, int page_total, int total, int page_size, int page) {
		this.product_list = product_list;
		this.category_list = category_list;
		this.page_total = page_total;
		this.total = total;
		this.page_size = page_size;
		this.page = page;
	}

	public List<ProductVO> getProduct_list() {
		return product_list;
	}

	public void setProduct_list(List<ProductVO> product_list) {
		this.product_list = product_list;
	}

	public List<CategoryVO> getCategory_list() {
		return category_list;
	}

	public void setCategory_list(List<CategoryVO> category_list) {
		this.category_list = category_list;
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

	public int getPage_size() {
		return page_size;
	}

	public void setPage_size(int page_size) {
		this.page_size = page_size;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

}
