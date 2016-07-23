package com.yoho.gateway.controller.product.convert;

import com.yoho.gateway.model.product.ProductVo;
import com.yoho.product.model.ProductBo;

/**
 * 将数据服务层返回的数据转换成VO
 * @author xieyong
 *
 */
public interface ProductConvert {
	
	ProductVo convert(ProductBo productBo);
}
