package com.yoho.gateway.model.resources;

import java.io.Serializable;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.annotation.JSONField;

public class SearchBannerVo implements Serializable{
	
	private static final long serialVersionUID = 5889754642766714834L;
	
	private String logo;
	
	private String title;
	
	private String url;
	
	private String subtitle;
	
	private String intro;
	
	private JSONArray keyword;
	
	@JSONField(name="templet_id")
	private String templetId;
	
	private String result;

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public JSONArray getKeyword() {
		return keyword;
	}

	public void setKeyword(JSONArray keyword) {
		this.keyword = keyword;
	}

	public String getTempletId() {
		return templetId;
	}

	public void setTempletId(String templetId) {
		this.templetId = templetId;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
