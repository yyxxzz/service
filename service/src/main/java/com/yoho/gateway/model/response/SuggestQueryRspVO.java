package com.yoho.gateway.model.response;

import com.yoho.service.model.BaseBO;

public class SuggestQueryRspVO extends BaseBO {

	private static final long serialVersionUID = -4776286737677117248L;

	private Integer id;

	private Byte suggest_type;

	private Integer create_time;

	private Byte status;

	private String client_type;

	private Integer update_time;

	private String cover_image;

	private Byte has_image;

	private Integer order_by;

	private Integer reliable;

	private Integer unreliable;

	private Integer suggest_id;

	private String content;

	private String reply_content;

	private String cover_image_url;
	private String image;
	private Byte is_reliable;
	private String filter_content;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Byte getSuggest_type() {
		return suggest_type;
	}

	public void setSuggest_type(Byte suggest_type) {
		this.suggest_type = suggest_type;
	}

	public Integer getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Integer create_time) {
		this.create_time = create_time;
	}

	public Byte getStatus() {
		return status;
	}

	public void setStatus(Byte status) {
		this.status = status;
	}

	public String getClient_type() {
		return client_type;
	}

	public void setClient_type(String client_type) {
		this.client_type = client_type;
	}

	public Integer getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(Integer update_time) {
		this.update_time = update_time;
	}

	public String getCover_image() {
		return cover_image;
	}

	public void setCover_image(String cover_image) {
		this.cover_image = cover_image;
	}

	public Byte getHas_image() {
		return has_image;
	}

	public void setHas_image(Byte has_image) {
		this.has_image = has_image;
	}

	public Integer getOrder_by() {
		return order_by;
	}

	public void setOrder_by(Integer order_by) {
		this.order_by = order_by;
	}

	public Integer getReliable() {
		return reliable;
	}

	public void setReliable(Integer reliable) {
		this.reliable = reliable;
	}

	public Integer getUnreliable() {
		return unreliable;
	}

	public void setUnreliable(Integer unreliable) {
		this.unreliable = unreliable;
	}

	public Integer getSuggest_id() {
		return suggest_id;
	}

	public void setSuggest_id(Integer suggest_id) {
		this.suggest_id = suggest_id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReply_content() {
		return reply_content;
	}

	public void setReply_content(String reply_content) {
		this.reply_content = reply_content;
	}

	public String getCover_image_url() {
		return cover_image_url;
	}

	public void setCover_image_url(String cover_image_url) {
		this.cover_image_url = cover_image_url;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Byte getIs_reliable() {
		return is_reliable;
	}

	public void setIs_reliable(Byte is_reliable) {
		this.is_reliable = is_reliable;
	}

	public String getFilter_content() {
		return filter_content;
	}

	public void setFilter_content(String filter_content) {
		this.filter_content = filter_content;
	}

}
