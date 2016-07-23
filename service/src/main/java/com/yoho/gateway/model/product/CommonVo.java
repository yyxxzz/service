package com.yoho.gateway.model.product;


public class CommonVo {

	
	
	public CommonVo() {
		super();
	}

	public CommonVo(Integer id, String question, String answer) {
		super();
		this.id = id;
		this.question = question;
		this.answer = answer;
	}

	private Integer id;
	
	
	/**
	 * 问题
	 */
	private String question;
	
	/**
	 * 回答
	 */
	private String answer;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	
	
}
