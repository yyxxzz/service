package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * PC品牌分类 VO
 * @author caoyan
 *
 */
public class BrandFolderVo implements Serializable{
	
	private static final long serialVersionUID = 5889754642766714834L;
	
	@JSONField(name="id")
    private Integer id;
    
	@JSONField(name="brand_id")
    private Integer brandId;
    
	@JSONField(name="brand_sort_name")
    private String brandSortName;
    
	@JSONField(name="brand_sort_ico")
    private String brandSortIco;
    
	@JSONField(name="parent_id")
    private Integer parentId;
    
	@JSONField(name="order_by")
    private Integer orderBy;
    
	@JSONField(name="status")
    private Integer status;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public String getBrandSortName() {
		return brandSortName;
	}

	public void setBrandSortName(String brandSortName) {
		this.brandSortName = brandSortName;
	}

	public String getBrandSortIco() {
		return brandSortIco;
	}

	public void setBrandSortIco(String brandSortIco) {
		this.brandSortIco = brandSortIco;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Integer getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(Integer orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
    
}
