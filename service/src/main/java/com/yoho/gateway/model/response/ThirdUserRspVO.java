package com.yoho.gateway.model.response;

public class ThirdUserRspVO {

	private String uid;//第三方用户id
	private Integer type;// 1微信2 weibo
	private String tokenExpire;
	private String token;
	private String url;
	private String content;
	private String locale;
	private String platform;
	private String refresh_token;//新浪微博
	private String expires_in;
	private String access_token;
	private String remind_in;
	private String language;

	public String getRefresh_token() {
		return refresh_token;
	}

	public void setRefresh_token(String refresh_token) {
		this.refresh_token = refresh_token;
	}

	public String getExpires_in() {
		return expires_in;
	}

	public void setExpires_in(String expires_in) {
		this.expires_in = expires_in;
	}

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getRemind_in() {
		return remind_in;
	}

	public void setRemind_in(String remind_in) {
		this.remind_in = remind_in;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String platform) {
		this.platform = platform;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTokenExpire() {
		return tokenExpire;
	}

	public void setTokenExpire(String tokenExpire) {
		this.tokenExpire = tokenExpire;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
