package com.yoho.gateway.model.product;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class ConsultRspVO {

	/**
	 * 咨询信息id
	 */
	private int id;

	/**
	 * 咨询 询问信息
	 */
	private String ask;

	/**
	 * 咨询 回答信息
	 */
	private String answer;

	/**
	 * 咨询，询问时间
	 */
	private String ask_time;

	/**
	 * 咨询，回答时间
	 */
	private String answer_time;
	/**
	 * 咨询 询问信息
	 */
	private String like;

	/**
	 * 咨询 回答信息
	 */
	private String is_like;

	/**
	 * 咨询，询问时间
	 */
	private String useful;

	/**
	 * 咨询，回答时间
	 */
	private String is_useful;
	

	public String getLike() {
		return like;
	}

	public void setLike(String like) {
		this.like = like;
	}

	public String getIs_like() {
		return is_like;
	}

	public void setIs_like(String is_like) {
		this.is_like = is_like;
	}

	public String getUseful() {
		return useful;
	}

	public void setUseful(String useful) {
		this.useful = useful;
	}

	public String getIs_useful() {
		return is_useful;
	}

	public void setIs_useful(String is_useful) {
		this.is_useful = is_useful;
	}

	/**
	 * 产品咨询总数
	 */
	private int total;

	public ConsultRspVO() {

	}

	public ConsultRspVO(int id, String ask, String answer, String ask_time,
			String answer_time,  String like, String is_like, String useful,
			String is_useful,int total) {
		this.id = id;
		this.ask = ask;
		this.answer = answer;
		this.ask_time = ask_time;
		this.answer_time = answer_time;
		this.like=like;
		this.is_like=is_like;
		this.useful=useful;
		this.is_useful=is_useful;
		this.total = total;
	}

	public ConsultRspVO(int id, String ask, String answer, String ask_time,
			String answer_time, int total) {
		super();
		this.id = id;
		this.ask = ask;
		this.answer = answer;
		this.ask_time = ask_time;
		this.answer_time = answer_time;
		this.total = total;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getAsk() {
		return ask;
	}

	public void setAsk(String ask) {
		this.ask = ask;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getAsk_time() {
		return ask_time;
	}

	public void setAsk_time(String ask_time) {
		this.ask_time = ask_time;
	}

	public String getAnswer_time() {
		return answer_time;
	}

	public void setAnswer_time(String answer_time) {
		this.answer_time = answer_time;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public ConsultRspVO(int id, String ask, String answer, String ask_time,
			String answer_time) {
		super();
		this.id = id;
		this.ask = ask;
		this.answer = answer;
		this.ask_time = ask_time;
		this.answer_time = answer_time;
	}

}
