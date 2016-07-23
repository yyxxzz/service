package com.yoho.gateway.service.search;

/**
 * 查询分组分类信息列表接口
 * @author mali
 *
 */
public interface SortService {
	/**
     * 查询分类信息
     * @param 搜索来源
     * @param param
     * @return  分类集合的JSON对象
     */
	Object getSortList(String searchFrom,String param);
}
