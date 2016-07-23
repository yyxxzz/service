package com.yoho.gateway.service.search;

import java.util.Map;

import com.yoho.gateway.model.search.SimpleBrandInfoVo;
import com.yoho.product.model.BrandBo;

/**
 * 品牌相关的接口
 * @author mali
 *
 */
public interface BrandService {
	/**
	 * 查询与关键词匹配的品牌ID
	 * @param query 关键词
	 * @return 匹配的品牌信息，匹配不上返回NUll
	 */
	SimpleBrandInfoVo getBrandInfoByQuery(String query);
	
	/**
	 * 查询所有的品牌列表
	 * @return
	 */
	Map<Integer, BrandBo> queryAllBrandList();
}
