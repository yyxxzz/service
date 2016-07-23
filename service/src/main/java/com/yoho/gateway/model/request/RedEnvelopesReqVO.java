package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

/**
 * Created by xinfei on 15/11/30.
 */
public class RedEnvelopesReqVO extends BaseBO{

    /**
	 * 
	 */
	private static final long serialVersionUID = 7040341152807387670L;
	/**
     * 用户ID
     */
    private Integer uid;

    public RedEnvelopesReqVO(){

    }

    public RedEnvelopesReqVO(Integer uid){
        this.uid = uid;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }
}
