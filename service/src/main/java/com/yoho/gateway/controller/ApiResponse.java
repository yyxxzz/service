package com.yoho.gateway.controller;

import java.util.ArrayList;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.yoho.core.common.utils.MD5;
import com.yoho.error.GatewayError;

public class ApiResponse{
	private static String DEFAULT_MSG = "操作成功";
	private static int DEFAULT_CODE = 200;
	private static final String MD5_SALT = "1313I923R4DFKJASDFKLAKDF;";
	
	private int code;
	private String message;
	private String md5;
	
	private String alg = "SALT_MD5";
	private Object data;
	
	public ApiResponse() {
		this(200,DEFAULT_MSG,null);
	}

	public ApiResponse(int code, String message, Object data) {
		this.code = code;
		if(StringUtils.isNotEmpty(message)){
			this.message = message;
		}
		this.data = data;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}

	public String getAlg() {
		return alg;
	}

	public void setAlg(String alg) {
		this.alg = alg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
	
	/**
	 * 构造相应内部类
	 */
	public static class ApiResponseBuilder{
		ApiResponse apiResponse;

		public ApiResponseBuilder() {
			apiResponse = new ApiResponse();
		}
		public ApiResponseBuilder code(int code){
			apiResponse.code = code;
			return this;
		}
		public ApiResponseBuilder message(String message){
			apiResponse.message =  message;
			return this;
		}
		public ApiResponseBuilder code(GatewayError gatewayError){
			apiResponse.code =  gatewayError.getCode();
			apiResponse.message = gatewayError.getMessage();
			return this;
		}
		public ApiResponseBuilder data(Object  data){
			apiResponse.data =  data;
			return this;
		}
		public ApiResponse build(){
			if(this.apiResponse.code<0){
				this.apiResponse.code =  DEFAULT_CODE;
			}
			if(StringUtils.isEmpty(apiResponse.message)){
				this.apiResponse.message = DEFAULT_MSG;
			}
			apiResponse.md5 =getMd5();
			return apiResponse;
		}
		private String getMd5(){
			if(this.apiResponse.data == null){
				this.apiResponse.data = new ArrayList<>(0);
			}
			String json = JSON.toJSONString(this.apiResponse.data);
			return MD5.md5(MD5_SALT+":"+ json);
		}
		
	}
	
	
	
}