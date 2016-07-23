package com.yoho.gateway.model.user.sign;

import java.util.List;

public class UserSignInfoRspVO {
	private String uid;// 用户Id
	private String pushFlag;// 是否开启推送0:关闭 1：开启
	private String totalYohoCoinNum;// 有货币的总数
	private String todayKey;// 今天的日期
	private String todaySigned;// 今天是否已经签到
	private String todayCanGainYohoCoinNum;// 今天签到可领取的由货币数量
	private String tomorrowCanGainYohoCoinNum;// 今天签到可领取的由货币数量
	private String constantDay;// 用户已经连续签到几天,今天已签到的也算
	private List<UserSignInfoVO> signInfoList;// 用户签到的轨迹数据

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPushFlag() {
		return pushFlag;
	}

	public void setPushFlag(String pushFlag) {
		this.pushFlag = pushFlag;
	}

	public String getTotalYohoCoinNum() {
		return totalYohoCoinNum;
	}

	public void setTotalYohoCoinNum(String totalYohoCoinNum) {
		this.totalYohoCoinNum = totalYohoCoinNum;
	}

	public String getTodayKey() {
		return todayKey;
	}

	public void setTodayKey(String todayKey) {
		this.todayKey = todayKey;
	}

	public String getTodaySigned() {
		return todaySigned;
	}

	public void setTodaySigned(String todaySigned) {
		this.todaySigned = todaySigned;
	}

	public String getTodayCanGainYohoCoinNum() {
		return todayCanGainYohoCoinNum;
	}

	public void setTodayCanGainYohoCoinNum(String todayCanGainYohoCoinNum) {
		this.todayCanGainYohoCoinNum = todayCanGainYohoCoinNum;
	}

	public String getTomorrowCanGainYohoCoinNum() {
		return tomorrowCanGainYohoCoinNum;
	}

	public void setTomorrowCanGainYohoCoinNum(String tomorrowCanGainYohoCoinNum) {
		this.tomorrowCanGainYohoCoinNum = tomorrowCanGainYohoCoinNum;
	}

	public String getConstantDay() {
		return constantDay;
	}

	public void setConstantDay(String constantDay) {
		this.constantDay = constantDay;
	}

	public List<UserSignInfoVO> getSignInfoList() {
		return signInfoList;
	}

	public void setSignInfoList(List<UserSignInfoVO> signInfoList) {
		this.signInfoList = signInfoList;
	}

}
