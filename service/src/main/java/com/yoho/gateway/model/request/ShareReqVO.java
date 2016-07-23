package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

/**
 * 分享信息请求
 * @author lijian
 *
 */
public class ShareReqVO extends BaseBO {


	private static final long serialVersionUID = 6701650906019870078L;
	
	private Integer uid;
	private String authInfo; //第三方用户信息
	private Integer authType;// 1微信2 weibo
	private Integer contentType;// 1文字2 图文
	private String content; ///用户信息


	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getContentType() {
		return contentType;
	}

	public void setContentType(Integer contentType) {
		this.contentType = contentType;
	}

	public Integer getAuthType() {
		return authType;
	}

	public void setAuthType(Integer authType) {
		this.authType = authType;
	}

	public String getAuthInfo() {
		return authInfo;
	}

	public void setAuthInfo(String authInfo) {
		this.authInfo = authInfo;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

}
