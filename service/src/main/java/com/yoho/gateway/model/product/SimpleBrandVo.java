package com.yoho.gateway.model.product;

import java.io.Serializable;

/**
 * 品牌 VO 供pc使用
 * @author caoyan
 *
 */
public class SimpleBrandVo implements Serializable{
	
	private static final long serialVersionUID = 5889754642766714834L;
	
	private Integer id;
	
	private String name;
	
    private String image;

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

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}
    
}
