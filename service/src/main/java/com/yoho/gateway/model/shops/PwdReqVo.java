package com.yoho.gateway.model.shops;

import lombok.Data;

@Data
public class PwdReqVo {
	
	private String pid;

	private String oldPwd;
	
	private String newPwd;
	
	private String newPwdConfirm;
}
