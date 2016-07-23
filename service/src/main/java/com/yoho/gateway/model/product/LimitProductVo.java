package com.yoho.gateway.model.product;


import java.util.List;

import com.yoho.product.model.LimitProductAttachBo;

public class LimitProductVo {

	/**
	 * 限定商品id
	 */
	private Integer id;

	/**
	 * 限量商品code(唯一确定一个限量商品code)
	 */
	private String limitProductCode;

	/**
	 * 限定商品名称
	 */
	private String productName;

	/**
	 * 限定商品描述
	 */
	private String description;

	/**
	 * 商品价格
	 */
	private String price;

	/**
	 * 开售日期
	 */
	private String saleTime;

	/**
	 * 是否热门商品，0：非热门 1：热门
	 */
	private int hotFlag;


	/**
	 * 关联skn(该字段冗余的)
	 */
	private Integer productSkn;

	/**
	 * 是否前台展示状态，0：不展示 1：展示
	 */
	private int showFlag;

	/**
	 * 排序顺序
	 */
	private int orderBy;

	/**
	 * 批次号
	 */
	private String batchNo;

	/**
     * 封面图
     */
	private String defaultUrl;
	
	/**
	 * 关注人数
	 */
	private int reminderNum;

	/**
	 * 说明跳转路径
	 */
	private String helpUrl ="http://m.yohobuy.com/help/limitcodeColSize";

	/**
	 * 展示状态 
	 * 1.开售前 立即分享获得限购码(如果已经抢光显示限购码已经被抢光,获取限购码成功之后按钮变成即将开售，如果有限购码就直接显示即将开售)
	 * 2.开售后 如果售罄所有按钮均不展示，如果限购码被抢光显示立即购买不可点，如果有限购码，直接显示立即购买
	 * 
	 * 0.活动未开始
	 * 1.立即分享限购码
	 * 2.已经抢光
	 * 3.已经售罄
	 * 4.立即购买
	 * 5.限购码已抢光
	 * 6.即将开售
	 * 7.已获取限购码,立即购买不可点击
	 */
	
	private int showStatus;
	
	/**
	 * 销售状态 
	 * 0.开售前
	 * 1.开售后
	 */
	private int saleStatus;
	
	/**
	 * 分享URL
	 */
	private String shareUrl;
	
	/**
	 * 用户是否设置提醒
	 */
	private boolean alertFlag;
	
	/**
	 * 原始没做转换之前的开售时间
	 */
	private Integer oldSaleTime;
	
	/**
	 * 限量商品类型
	 * 1.分享(默认是分享)
	 * 2.排队
	 */
	private int limitProductType=1;
	
	/**
	 * 排队关联的活动的ID
	 */
	private Integer activityId;

	/**
	 * 排队关联的活动的状态，0未开始，3已结束，2已经参加排队，1可以参加排队
	 */
	private Integer queueType;
	
	/**
	 * 关联的SKU
	 */
	private String selectSKU;
	
	/**
	 * 是否关联了SKU
	 */
	private boolean relatedSKU;
	
	/**
	 * 关联SKU时的颜色
	 */
	private String colorName;
	
	/**
	 * 关联SKU时的尺码
	 */
	private String sizeName;

	/**
	 * 限定商品附件列表，图片或者视频
	 */
	private List<LimitProductAttachBo> attachment;

	public List<LimitProductAttachBo> getAttachment() {
		return attachment;
	}

	public void setAttachment(List<LimitProductAttachBo> attachment) {
		this.attachment = attachment;
	}

	public int getLimitProductType() {
		return limitProductType;
	}

	public void setLimitProductType(int limitProductType) {
		this.limitProductType = limitProductType;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public Integer getOldSaleTime() {
		return oldSaleTime;
	}

	public void setOldSaleTime(Integer oldSaleTime) {
		this.oldSaleTime = oldSaleTime;
	}

	public int getSaleStatus() {
		return saleStatus;
	}

	public void setSaleStatus(int saleStatus) {
		this.saleStatus = saleStatus;
	}

	public int getShowStatus() {
		return showStatus;
	}

	public void setShowStatus(int showStatus) {
		this.showStatus = showStatus;
	}

	public int getReminderNum() {
		return reminderNum;
	}

	public void setReminderNum(int reminderNum) {
		this.reminderNum = reminderNum;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getLimitProductCode() {
		return limitProductCode;
	}

	public void setLimitProductCode(String limitProductCode) {
		this.limitProductCode = limitProductCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSaleTime() {
		return saleTime;
	}

	public void setSaleTime(String saleTime) {
		this.saleTime = saleTime;
	}

	public int getHotFlag() {
		return hotFlag;
	}

	public void setHotFlag(int hotFlag) {
		this.hotFlag = hotFlag;
	}

	public String getColorName() {
		return colorName;
	}

	public void setColorName(String colorName) {
		this.colorName = colorName;
	}

	public String getSizeName() {
		return sizeName;
	}

	public void setSizeName(String sizeName) {
		this.sizeName = sizeName;
	}

	public Integer getProductSkn() {
		return productSkn;
	}

	public void setProductSkn(Integer productSkn) {
		this.productSkn = productSkn;
	}

	public int getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(int showFlag) {
		this.showFlag = showFlag;
	}

	public String getBatchNo() {
		return batchNo;
	}

	public void setBatchNo(String batchNo) {
		this.batchNo = batchNo;
	}

	public String getDefaultUrl() {
		return defaultUrl;
	}

	public void setDefaultUrl(String defaultUrl) {
		this.defaultUrl = defaultUrl;
	}

	public String getShareUrl() {
		return shareUrl;
	}

	public void setShareUrl(String shareUrl) {
		this.shareUrl = shareUrl;
	}

	public boolean getAlertFlag() {
		return alertFlag;
	}

	public void setAlertFlag(boolean alertFlag) {
		this.alertFlag = alertFlag;
	}

	public int getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(int orderBy) {
		this.orderBy = orderBy;
	}

	public Integer getQueueType() {
		return queueType;
	}

	public void setQueueType(Integer queueType) {
		this.queueType = queueType;
	}

	public String getSelectSKU() {
		return selectSKU;
	}

	public void setSelectSKU(String selectSKU) {
		this.selectSKU = selectSKU;
	}

	public boolean isRelatedSKU() {
		return relatedSKU;
	}

	public void setRelatedSKU(boolean relatedSKU) {
		this.relatedSKU = relatedSKU;
	}

	public String getHelpUrl() {
		return helpUrl;
	}

	public void setHelpUrl(String helpUrl) {
		this.helpUrl = helpUrl;
	}

	@Override
	public String toString() {
		return "LimitProductVo [id=" + id + ", limitProductCode="
				+ limitProductCode + ", productName=" + productName
				+ ", description=" + description + ", price=" + price
				+ ", saleTime=" + saleTime + ", hotFlag=" + hotFlag
				+ ", productSkn=" + productSkn + ", showFlag=" + showFlag
				+ ", orderBy=" + orderBy + ", batchNo=" + batchNo
				+ ", defaultUrl=" + defaultUrl + ", reminderNum=" + reminderNum
				+ ", showStatus=" + showStatus + ", saleStatus=" + saleStatus
				+ ", shareUrl=" + shareUrl + ", alertFlag=" + alertFlag
				+ ", oldSaleTime=" + oldSaleTime + ", limitProductType="
				+ limitProductType + ", activityId=" + activityId
				+ ", attachment=" + attachment + ",queueType=" + queueType
				+ ",selectSKU=" + selectSKU + ",relatedSKU ="+ relatedSKU
				+",colorName ="+colorName+",sizeName = "+sizeName+"]";
	}
	
}
