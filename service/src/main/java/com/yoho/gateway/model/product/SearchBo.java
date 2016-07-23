package com.yoho.gateway.model.product;

import com.alibaba.fastjson.JSONObject;

public class SearchBo {
	private String message;
    private JSONObject data;
    private Integer code;
    
    
    public SearchBo(String message, JSONObject data, Integer code) {
		super();
		this.message = message;
		this.data = data;
		this.code = code;
	}
    

	public SearchBo() {
		super();
	}


	public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public JSONObject getData() {
        return data;
    }

    public void setData(JSONObject data) {
        this.data = data;
    }
}
