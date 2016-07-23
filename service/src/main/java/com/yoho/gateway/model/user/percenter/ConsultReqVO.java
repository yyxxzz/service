package com.yoho.gateway.model.user.percenter;

import com.yoho.service.model.request.PageReqBO;

/**
 * 购买咨询
 * @author yoho
 *
 */
public class ConsultReqVO extends PageReqBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6568385739850517874L;
	
	private int uid;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

}
