package com.yoho.gateway.service.search;


/**
 * 打折信息接口
 * @author mali
 *
 */
public interface DiscountService {

    /**
     * 查询打折信息
     * @param paramMap
     * @return 打折集合的JSON对象
     */
	Object getDiscount(String searchFrom,String dynamicParam);
}
