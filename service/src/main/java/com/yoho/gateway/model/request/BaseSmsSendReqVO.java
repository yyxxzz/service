package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class BaseSmsSendReqVO extends BaseBO{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8451710646914190766L;
	
	private String project;
	
	private String message;
	
	private String target;
	
	private String token;
	
	private String start_time;
	
	private String end_time;
	
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}


}
