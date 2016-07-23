package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 评论和咨询VO
 * @author xieyong
 *
 */
public class CommentAndConsultVo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7492488081504812614L;
	
	@JSONField(name="comment_total")
	private int commentTotal;
	
	@JSONField(name="comment")
	private CommentVo commentVo;
	
	@JSONField(name="consult_total")
	private int consultTotal;
	
	@JSONField(name="consult")
	private ConsultVo consultVo;

	public Integer getCommentTotal() {
		return commentTotal;
	}

	public void setCommentTotal(int commentTotal) {
		this.commentTotal = commentTotal;
	}

	public CommentVo getCommentVo() {
		return commentVo;
	}

	public void setCommentVo(CommentVo commentVo) {
		this.commentVo = commentVo;
	}

	public Integer getConsultTotal() {
		return consultTotal;
	}

	public void setConsultTotal(int consultTotal) {
		this.consultTotal = consultTotal;
	}

	public ConsultVo getConsultVo() {
		return consultVo;
	}

	public void setConsultVo(ConsultVo consultVo) {
		this.consultVo = consultVo;
	}
}
