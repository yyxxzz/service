package com.yoho.gateway.model.product;

import java.util.List;

/**
 * 潮流密码
 * @author yoho
 *
 */
public class FashioncodeConfigVo {
	List<FashioncodeConfig> list;

	public List<FashioncodeConfig> getList() {
		return list;
	}

	public void setList(List<FashioncodeConfig> list) {
		this.list = list;
	}

	@Override
	public String toString() {
		return "FashioncodeConfigVo [list=" + list + "]";
	}
}
