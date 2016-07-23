package com.yoho.gateway.model.product;

public class FashioncodeConfig {
private String type;
	
	private String title;
	
	private String template;

	public FashioncodeConfig(String type, String title, String template) {
		this.type = type;
		this.title = title;
		this.template = template;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	@Override
	public String toString() {
		return "FashioncodeConfigVo [type=" + type + ", title=" + title
				+ ", template=" + template + "]";
	}
}
