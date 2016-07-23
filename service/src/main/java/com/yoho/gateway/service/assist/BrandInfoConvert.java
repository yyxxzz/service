package com.yoho.gateway.service.assist;

import com.yoho.gateway.model.search.SimpleBrandInfoVo;
import com.yoho.product.model.SimpleBrandInfo;

/**
 * 品牌的Bo到Vo的转换器
 * @author mali
 *
 */
public interface BrandInfoConvert {
	/**
	 * 品牌的Bo到Vo的转换
	 * @param info Bo 为空则返回NULL
	 * @return Vo
	 */
	SimpleBrandInfoVo convertVo(SimpleBrandInfo info);
}
