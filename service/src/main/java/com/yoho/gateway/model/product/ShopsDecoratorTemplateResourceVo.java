package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class ShopsDecoratorTemplateResourceVo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8247518307450998296L;
	@JSONField(name="resource_id")
	private Integer id;

	@JSONField(name="decorator_id")
    private Integer shopsDecoratorId;
    @JSONField(name="template_id")
    private Integer shopsDecoratorTemplateId;

    @JSONField(name="resource_name")
    private String resourceName;

    private Integer createTime;

    private Integer updateTime;
    @JSONField(name="resource_data")
    private String resourceData;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getShopsDecoratorId() {
        return shopsDecoratorId;
    }

    public void setShopsDecoratorId(Integer shopsDecoratorId) {
        this.shopsDecoratorId = shopsDecoratorId;
    }

    public Integer getShopsDecoratorTemplateId() {
        return shopsDecoratorTemplateId;
    }

    public void setShopsDecoratorTemplateId(Integer shopsDecoratorTemplateId) {
        this.shopsDecoratorTemplateId = shopsDecoratorTemplateId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName == null ? null : resourceName.trim();
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
    }

    public String getResourceData() {
        return resourceData;
    }

    public void setResourceData(String resourceData) {
        this.resourceData = resourceData == null ? null : resourceData.trim();
    }

	@Override
	public String toString() {
		return "ShopsDecoratorTemplateResourceBo [id=" + id
				+ ", shopsDecoratorId=" + shopsDecoratorId
				+ ", shopsDecoratorTemplateId=" + shopsDecoratorTemplateId
				+ ", resourceName=" + resourceName + ", createTime="
				+ createTime + ", updateTime=" + updateTime + ", resourceData="
				+ resourceData + "]";
	}
    
}