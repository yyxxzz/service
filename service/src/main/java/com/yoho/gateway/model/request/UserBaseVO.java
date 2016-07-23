package com.yoho.gateway.model.request;

import com.yoho.service.model.BaseBO;

public class UserBaseVO extends BaseBO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -355563728908502930L;

	/**
	 * 用户ID，修改时的主键
	 */
	private Integer uid;

	/**
	 * 用户名称
	 */
	private String username;

	/**
	 * 用户昵称
	 */
	private String nick_name;

	/**
	 * 用户性别
	 */
	private String gender;

	/**
	 * 用户生日
	 */
	private String birthday;

	/**
	 * TODO
	 */
	private String charId;

	/**
	 * 用户头像的url
	 */
	private String headIco;

	/**
	 * TODO
	 */
	private Byte income;

	/**
	 * TODO
	 */
	private Byte profession;

	/**
	 * 用户身高
	 */
	private Integer height;

	/**
	 * 用户体重
	 */
	private Integer weight;

	/**
	 * 用户的vip等级
	 */
	private Integer vipLevel;

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNick_name() {
		return nick_name;
	}

	public void setNick_name(String nick_name) {
		this.nick_name = nick_name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getCharId() {
		return charId;
	}

	public void setCharId(String charId) {
		this.charId = charId;
	}

	public String getHeadIco() {
		return headIco;
	}

	public void setHeadIco(String headIco) {
		this.headIco = headIco;
	}

	public Byte getIncome() {
		return income;
	}

	public void setIncome(Byte income) {
		this.income = income;
	}

	public Byte getProfession() {
		return profession;
	}

	public void setProfession(Byte profession) {
		this.profession = profession;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Integer getVipLevel() {
		return vipLevel;
	}

	public void setVipLevel(Integer vipLevel) {
		this.vipLevel = vipLevel;
	}

}
