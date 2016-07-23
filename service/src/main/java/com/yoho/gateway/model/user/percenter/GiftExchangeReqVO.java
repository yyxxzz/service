package com.yoho.gateway.model.user.percenter;

import com.yoho.service.model.BaseBO;

public class GiftExchangeReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4712869451101978152L;
	
	private String giftCardCode1;
	
	private String giftCardCode2;
	
	private String giftCardCode3;
	
	private String captchaCode;
	
	private String uid;

	public String getGiftCardCode1() {
		return giftCardCode1;
	}

	public void setGiftCardCode1(String giftCardCode1) {
		this.giftCardCode1 = giftCardCode1;
	}

	public String getGiftCardCode2() {
		return giftCardCode2;
	}

	public void setGiftCardCode2(String giftCardCode2) {
		this.giftCardCode2 = giftCardCode2;
	}

	public String getGiftCardCode3() {
		return giftCardCode3;
	}

	public void setGiftCardCode3(String giftCardCode3) {
		this.giftCardCode3 = giftCardCode3;
	}

	public String getCaptchaCode() {
		return captchaCode;
	}

	public void setCaptchaCode(String captchaCode) {
		this.captchaCode = captchaCode;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	

}
