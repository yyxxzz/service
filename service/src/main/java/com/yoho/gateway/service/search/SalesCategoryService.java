package com.yoho.gateway.service.search;

/**
 * 销售分类的操作接口
 * @author mali
 *
 */
public interface SalesCategoryService {
	/**
	 * 根据yh_channel查询其关联物理分类信息
	 * @param yh_channel
	 * @return 关联物理分类信息   查询部到则返回null
	 */
	String queryRelationParamter(String yh_channel);
}
