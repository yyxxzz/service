package com.yoho.gateway.model.response;

public class ShareUserRspVO {

	private Integer uid;
	private Long accountId;
	private String accountName;
	private Integer channel;
	private String shareReqObj;
	private String createTime;
	private String updateTime;
	private Integer updateFlag;//更新标示 1 更新授权信息 0不更新授权信息


	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public Integer getUpdateFlag() {
		return updateFlag;
	}

	public void setUpdateFlag(Integer updateFlag) {
		this.updateFlag = updateFlag;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public Long getAccountId() {
		return accountId;
	}

	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public Integer getChannel() {
		return channel;
	}

	public void setChannel(Integer channel) {
		this.channel = channel;
	}

	public String getShareReqObj() {
		return shareReqObj;
	}

	public void setShareReqObj(String shareReqObj) {
		this.shareReqObj = shareReqObj;
	}
}
