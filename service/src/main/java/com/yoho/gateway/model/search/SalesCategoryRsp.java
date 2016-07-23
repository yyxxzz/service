package com.yoho.gateway.model.search;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 封装分类查询列表的对象
 * @author mali
 *
 */
public class SalesCategoryRsp implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8129265319919869584L;
	
	Map<String, List<SalesCategoryVo>> data = new LinkedHashMap<String, List<SalesCategoryVo>>();

	public Map<String, List<SalesCategoryVo>> getData() {
		return data;
	}

	public void setData(Map<String, List<SalesCategoryVo>> data) {
		this.data = data;
	}

	@Override
	public String toString() {
		return "SalesCategoryRsp [data=" + data + "]";
	}
}
