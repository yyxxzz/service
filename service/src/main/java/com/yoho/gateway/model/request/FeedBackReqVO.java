package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class FeedBackReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3789720248298312524L;
	
	private int feedback_id;
	
	private int question_id;
	
	private String solution;
	
	private String answer;
	
	private int uid;
	
	public int getFeedback_id() {
		return feedback_id;
	}

	public void setFeedback_id(int feedback_id) {
		this.feedback_id = feedback_id;
	}

	public int getQuestion_id() {
		return question_id;
	}

	public void setQuestion_id(int question_id) {
		this.question_id = question_id;
	}

	public String getSolution() {
		return solution;
	}

	public void setSolution(String solution) {
		this.solution = solution;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getREMOTE_ADDR() {
		return REMOTE_ADDR;
	}

	public void setREMOTE_ADDR(String rEMOTE_ADDR) {
		REMOTE_ADDR = rEMOTE_ADDR;
	}

	private String REMOTE_ADDR;

	
	
}
