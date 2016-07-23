package com.yoho.gateway.model.user.passport;

public class VipInfoVo {

	private String curTotalCost;// //总消费
	private String curYearCost;// //年内消费
	private String upgradeNeedCost;// //升级还需要的消费

	private String curYearCostPer;//
	private String upgradeNeedCostPer;

	private String vipStartTime;// VIP开始时间
	private String vipEndTime;// //结束时间
	private String vipEndDay;// vip有效日期(vipEndTime-now())
	private String vipRemainDays;// 剩余时间
	private String fitTime;// vipStartTime减去 15天

	private VipDetailInfoVo curVipInfo;
	private VipDetailInfoVo nextVipInfo;

	public String getCurTotalCost() {
		return curTotalCost;
	}

	public void setCurTotalCost(String curTotalCost) {
		this.curTotalCost = curTotalCost;
	}

	public String getCurYearCost() {
		return curYearCost;
	}

	public void setCurYearCost(String curYearCost) {
		this.curYearCost = curYearCost;
	}

	public String getUpgradeNeedCost() {
		return upgradeNeedCost;
	}

	public void setUpgradeNeedCost(String upgradeNeedCost) {
		this.upgradeNeedCost = upgradeNeedCost;
	}

	public String getCurYearCostPer() {
		return curYearCostPer;
	}

	public void setCurYearCostPer(String curYearCostPer) {
		this.curYearCostPer = curYearCostPer;
	}

	public String getUpgradeNeedCostPer() {
		return upgradeNeedCostPer;
	}

	public void setUpgradeNeedCostPer(String upgradeNeedCostPer) {
		this.upgradeNeedCostPer = upgradeNeedCostPer;
	}

	public String getVipStartTime() {
		return vipStartTime;
	}

	public void setVipStartTime(String vipStartTime) {
		this.vipStartTime = vipStartTime;
	}

	public String getVipEndTime() {
		return vipEndTime;
	}

	public void setVipEndTime(String vipEndTime) {
		this.vipEndTime = vipEndTime;
	}

	public String getVipEndDay() {
		return vipEndDay;
	}

	public void setVipEndDay(String vipEndDay) {
		this.vipEndDay = vipEndDay;
	}

	public String getVipRemainDays() {
		return vipRemainDays;
	}

	public void setVipRemainDays(String vipRemainDays) {
		this.vipRemainDays = vipRemainDays;
	}

	public String getFitTime() {
		return fitTime;
	}

	public void setFitTime(String fitTime) {
		this.fitTime = fitTime;
	}

	public VipDetailInfoVo getCurVipInfo() {
		return curVipInfo;
	}

	public void setCurVipInfo(VipDetailInfoVo curVipInfo) {
		this.curVipInfo = curVipInfo;
	}

	public VipDetailInfoVo getNextVipInfo() {
		return nextVipInfo;
	}

	public void setNextVipInfo(VipDetailInfoVo nextVipInfo) {
		this.nextVipInfo = nextVipInfo;
	}

}
