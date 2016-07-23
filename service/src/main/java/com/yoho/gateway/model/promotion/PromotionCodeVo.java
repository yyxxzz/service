package com.yoho.gateway.model.promotion;

import com.alibaba.fastjson.annotation.JSONField;

public class PromotionCodeVo {
	
	@JSONField(name = "id")
	private int id;

	@JSONField(name = "name")
	private String name;

	@JSONField(name = "code")
	private String code;

	@JSONField(name = "limit_times")
	private int limitTimes;

	@JSONField(name = "discount_type")
	private String discountType;

	@JSONField(name = "amount_at_least")
	private float amountAtLeast;

	@JSONField(name = "count_at_least")
	private int countAtLeast;

	@JSONField(name = "discount")
	private float discount;

	@JSONField(name = "discount_at_most")
	private float discountAtMost;

	@JSONField(name = "status") 
	private int status;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getLimitTimes() {
		return limitTimes;
	}

	public void setLimitTimes(int limitTimes) {
		this.limitTimes = limitTimes;
	}


	public String getDiscountType() {
		return discountType;
	}

	public void setDiscountType(String discountType) {
		this.discountType = discountType;
	}

	public float getAmountAtLeast() {
		return amountAtLeast;
	}

	public void setAmountAtLeast(float amountAtLeast) {
		this.amountAtLeast = amountAtLeast;
	}

	public int getCountAtLeast() {
		return countAtLeast;
	}

	public void setCountAtLeast(int countAtLeast) {
		this.countAtLeast = countAtLeast;
	}

	public float getDiscount() {
		return discount;
	}

	public void setDiscount(float discount) {
		this.discount = discount;
	}

	public float getDiscountAtMost() {
		return discountAtMost;
	}

	public void setDiscountAtMost(float discountAtMost) {
		this.discountAtMost = discountAtMost;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

}
