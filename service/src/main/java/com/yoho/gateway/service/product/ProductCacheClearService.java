package com.yoho.gateway.service.product;

import java.util.List;


/**
 * 描述:清理商品缓存
 */
public interface ProductCacheClearService {

    /**
     * 根据skn清理商品缓存
     *
     * @param productSkn
     * @return
     */
	void clearProductCacheBySkn(Integer productSkn);

	/**
     * 根据skn批量清理商品缓存
     *
     * @param productSkn
     * @return
     */
	void clearBatchProductCacheBySkn(List<Integer> productSkns);

}
