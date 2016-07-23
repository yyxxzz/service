package com.yoho.gateway.model.response;

import com.yoho.service.model.BaseBO;

/**
 * 分享信息请求返回
 * @author lijian
 *
 */
public class ShareRspVO extends BaseBO {


	private static final long serialVersionUID = 6701650906019870078L;
	
	private Integer uid;
	private String authInfo; //第三方用户信息
	private Integer authType;// 1微信2 weibo
	private Integer status;//用户状态 0 不可用 1 可用

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
