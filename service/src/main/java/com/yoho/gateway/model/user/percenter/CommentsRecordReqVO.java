package com.yoho.gateway.model.user.percenter;

import com.yoho.service.model.BaseBO;

/**
 * 评论
 * @author yoho
 *
 */
public class CommentsRecordReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1390810137424756126L;
	
	private int uid;
	
	private String isComment;

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getIsComment() {
		return isComment;
	}

	public void setIsComment(String isComment) {
		this.isComment = isComment;
	}
	
	
}
