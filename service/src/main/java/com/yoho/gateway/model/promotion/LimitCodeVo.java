package com.yoho.gateway.model.promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的限购码列表
 *
 * @author wangshusheng
 * @Time 2016/2/17
 */
public class LimitCodeVo {

	/**
	 * 正常的限购码列表
	 */
	private List<LimitCodeProductVo> limitCodeProducts=new ArrayList<LimitCodeProductVo>();
	
	/**
	 * 失效的限购码列表
	 */
	private List<LimitCodeProductVo> invalidLimitCodeProducts=new ArrayList<LimitCodeProductVo>();

	public List<LimitCodeProductVo> getLimitCodeProducts() {
		return limitCodeProducts;
	}

	public void setLimitCodeProducts(List<LimitCodeProductVo> limitCodeProducts) {
		this.limitCodeProducts = limitCodeProducts;
	}

	public List<LimitCodeProductVo> getInvalidLimitCodeProducts() {
		return invalidLimitCodeProducts;
	}

	public void setInvalidLimitCodeProducts(List<LimitCodeProductVo> invalidLimitCodeProducts) {
		this.invalidLimitCodeProducts = invalidLimitCodeProducts;
	}

	@Override
	public String toString() {
		return "LimitCodeVo [limitCodeProducts=" + limitCodeProducts
				+ ", invalidLimitCodeProducts=" + invalidLimitCodeProducts
				+ "]";
	}
	
}
