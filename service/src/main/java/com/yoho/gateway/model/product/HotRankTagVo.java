package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 热门标签，热销排行版top100
 * @author wangshusheng
 *
 */
public class HotRankTagVo implements Serializable {

	private static final long serialVersionUID = -6035351845213194139L;

	@JSONField(name="id")
	private Integer id;
	
	@JSONField(name="name")
	private String name;

	@JSONField(name="params")
	private String params;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}

	@Override
	public String toString() {
		return "HotRankTagVo [id=" + id + ", name=" + name + ", params="
				+ params + "]";
	}
	
}
