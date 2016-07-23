package com.yoho.gateway.model.product;

import java.io.Serializable;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

public class ShopsDecoratorResourceGiftVo implements Serializable{
    /**
	 * 
	 */
	private static final long serialVersionUID = 8247518307450998296L;

	private List<ShopsDecoratorTemplateResourceVo> list;
	@JSONField(name="plateform")
	private String plateform;
	@JSONField(name="template_type")
	private String templateType;
	public List<ShopsDecoratorTemplateResourceVo> getList() {
		return list;
	}
	public void setList(List<ShopsDecoratorTemplateResourceVo> list) {
		this.list = list;
	}
	public String getPlateform() {
		return plateform;
	}
	public void setPlateform(String plateform) {
		this.plateform = plateform;
	}
	public String getTemplateType() {
		return templateType;
	}
	public void setTemplateType(String templateType) {
		this.templateType = templateType;
	}
	@Override
	public String toString() {
		return "ShopsDecoratorResourceGiftBo [list=" + list + ", plateform="
				+ plateform + ", templateType=" + templateType + "]";
	}
	
    
}