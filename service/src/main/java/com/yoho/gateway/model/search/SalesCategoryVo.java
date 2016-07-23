package com.yoho.gateway.model.search;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 查询分类的接口的vo对象
 * @author mali
 *
 */
public class SalesCategoryVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 57481905231262109L;
	
    @JSONField(name="category_name")
    private String categoryName;
    
	
    @JSONField(name="category_id")
    private String categoryId;
    
    
    @JSONField(name="node_count")
    private int nodeCount;
    
    @JSONField(name="sort_ico")
    private String icon;
    
    @JSONField(name="parent_id")
    private String parentId;
    
    @JSONField(name="relation_parameter")
    Map<String, String> relationParameterMap;
    
    @JSONField(name="sub")
    private Set<SalesCategoryVo> voSub;

	public String getCategoryName() {
		return categoryName;
	}
	
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Integer getNodeCount() {
		return nodeCount;
	}

	public void setNodeCount(Integer nodeCount) {
		this.nodeCount = nodeCount;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public Map<String, String> getRelationParameterMap() {
		return relationParameterMap;
	}

	public void setRelationParameterMap(Map<String, String> relationParameterMap) {
		this.relationParameterMap = relationParameterMap;
	}

	public Set<SalesCategoryVo> getVoSub() {
		return voSub;
	}

	public void setVoSub(Set<SalesCategoryVo> voSub) {
		this.voSub = voSub;
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((categoryName == null) ? 0 : categoryName.hashCode());
		result = prime
				* result
				+ ((relationParameterMap == null) ? 0 : relationParameterMap
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SalesCategoryVo other = (SalesCategoryVo) obj;
		if (categoryName == null) {
			if (other.categoryName != null)
				return false;
		} else if (!categoryName.equals(other.categoryName))
			return false;
		if (relationParameterMap == null) {
			if (other.relationParameterMap != null)
				return false;
		} else if (!relationParameterMap.equals(other.relationParameterMap))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SalesCategoryVo [categoryName=" + categoryName
				+ ", categoryId=" + categoryId + ", nodeCount=" + nodeCount
				+ ", icon=" + icon + ", parentId=" + parentId
				+ ", relationParameterMap=" + relationParameterMap + ", voSub="
				+ voSub + "]";
	}
}
