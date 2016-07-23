package com.yoho.gateway.model.user.passport;

import java.util.ArrayList;
import java.util.List;

public class VipDetailInfoVo {

	private String title;
	private String curLevel;
	private String nextLevel;

	private String needCost = "";
	private String commonDiscount = "";
	private String promotionDiscount = "";

	private List<String> premiumScops = new ArrayList<String>();

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCurLevel() {
		return curLevel;
	}

	public void setCurLevel(String curLevel) {
		this.curLevel = curLevel;
	}

	public String getNextLevel() {
		return nextLevel;
	}

	public void setNextLevel(String nextLevel) {
		this.nextLevel = nextLevel;
	}

	public String getNeedCost() {
		return needCost;
	}

	public void setNeedCost(String needCost) {
		this.needCost = needCost;
	}

	public String getCommonDiscount() {
		return commonDiscount;
	}

	public void setCommonDiscount(String commonDiscount) {
		this.commonDiscount = commonDiscount;
	}

	public String getPromotionDiscount() {
		return promotionDiscount;
	}

	public void setPromotionDiscount(String promotionDiscount) {
		this.promotionDiscount = promotionDiscount;
	}

	public List<String> getPremiumScops() {
		return premiumScops;
	}

	public void setPremiumScops(List<String> premiumScops) {
		this.premiumScops = premiumScops;
	}

}
