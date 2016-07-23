package com.yoho.gateway.controller.search.convert;

import java.util.Map;

import com.yoho.gateway.model.search.SalesCategoryRsp;
import com.yoho.gateway.model.search.SalesCategoryVo;
import com.yoho.product.model.SalesCategoryBo;


/**
 * 销售分类的BO转换成Vo对象接口
 * @author mali
 *
 */
public interface SalesCategoryConvert {

	/**
	 * 批量转换
	 * 销售分类的BO转换成Vo对象
	 * @param data BO
	 * @return Vo对象
	 */
	SalesCategoryRsp convert(Map<Integer, SalesCategoryBo> data);
	
	/**
	 * 单个转换
	 * @param bo
	 * @return
	 */
	//BO-->VO转换
	SalesCategoryVo convertToVO(SalesCategoryBo bo);
}
