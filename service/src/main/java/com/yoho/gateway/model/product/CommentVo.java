package com.yoho.gateway.model.product;

import java.io.Serializable;

import com.alibaba.fastjson.annotation.JSONField;

public class CommentVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1407363454403526265L;
	
	private int id;
	
	/**
	 * 商品id
	 */
	@JSONField(name="product_id")
	private Integer productId;
	
	/**
	 * 用户id
	 */
	private int uid;
	
	/**
	 * 评论内容
	 */
	private String content;
	
	/**
	 * 转换成2015-11-09 14:13:36格式
	 */
	@JSONField(name="create_time")
	private String createTime;

	/**
	 * 尺码名字
	 */
	@JSONField(name="size_name")
	private String sizeName;
	
	/**
	 * 颜色名字
	 */
	@JSONField(name="color_name")
	private String colorName;
	
	/**
	 * 昵称
	 */
	@JSONField(name="nickname")
	private String nickName;
	
	/**
	 * 头像
	 */
	@JSONField(name="head_ico")
	private String headIcon;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getProductId() {
		return productId;
	}

	public void setProductId(Integer productId) {
		this.productId = productId;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}
}
