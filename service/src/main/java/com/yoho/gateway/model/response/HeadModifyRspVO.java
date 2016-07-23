package com.yoho.gateway.model.response;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class HeadModifyRspVO {

	/**
	 * 图像模式,头像: yhb-head
	 */
	private String bucket;

	/**
	 * 图片的相对路径 /2015/11/12****
	 */
	private String image_path;

	/**
	 * 图片的绝对路径 http://*******
	 */
	private String image_url;
	
	public HeadModifyRspVO(){
		
	}
	
	public HeadModifyRspVO(String bucket, String image_path, String image_url){
		this.bucket = bucket;
		this.image_path = image_path;
		this.image_url = image_url;
	}
	

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	public String getBucket() {
		return bucket;
	}

	public void setBucket(String bucket) {
		this.bucket = bucket;
	}

	public String getImage_path() {
		return image_path;
	}

	public void setImage_path(String image_path) {
		this.image_path = image_path;
	}

	public String getImage_url() {
		return image_url;
	}

	public void setImage_url(String image_url) {
		this.image_url = image_url;
	}

}
