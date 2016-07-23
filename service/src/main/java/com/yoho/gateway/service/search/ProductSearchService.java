package com.yoho.gateway.service.search;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.model.product.TogetherProductRspVo;
import com.yoho.gateway.model.search.BigDataSearchReq;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductSearchBo;

/**
 * 搜索商品列表的接口
 * @author mali
 *
 */
public interface ProductSearchService {
	/**
	 * 根据条件搜索符合条件的商品列表数据
	 * @param req 条件对象
	 * @return 符合条件的商品列表数据
	 */
	JSONObject searchProductListByCategory(ProductSearchReq req, AsyncFuture<String[]> labelNameAsync, String shop);

	/**
	 * 根据条件搜索符合条件的奥莱潮品速递的商品列表数据
	 * @param req 条件对象
	 * @return 符合条件的奥莱潮品速递的商品列表数据
	 */
	JSONObject searchTrendCourierProductList(ProductSearchReq req);

	/**
	 * 根据条件搜索符合条件的奥莱潮品速递的商品列表数据
	 * @param req 条件对象
	 * @return 符合条件的奥莱潮品速递的商品列表数据
	 */

    /** 
     * 获得活动模板商品信息
     * @param req
     * @return
     * @see [类、类#方法、类#成员]
     */
    JSONObject searchActProduct(ProductSearchReq req);
	
	/**
	 * 根据条件搜索符合条件的商品列表数据
	 * @param req 条件对象
	 * @return 符合条件的商品列表数据
	 */
	JSONObject searchFuzzyProductList(ProductSearchReq req, String shop);
	
	/**
	 * 根据请求参数搜索相关的商品列表
	 * @param req 请求参数
	 * @return 据请求参数搜索相关的商品列表
	 */
	JSONObject searchNewProductList(ProductSearchReq req);

	/**
	 * 根据打折幅度查询商品列表
	 * @param req 请求参数
	 * @return 根据打折幅度查询商品列表
	 */
	JSONObject searchSalesProductList(ProductSearchReq req);
	
	/**
	 * top100
	 * @param req
	 * @return
	 * @throws ServiceException
	 */
	JSONObject searchTopProductList(ProductSearchReq req);
	
	/**
	 * 首页猜你喜欢
	 * @param req
	 * @return
	 */
	JSONObject searchLast7dayProductList(ProductSearchReq req);
	
	/**
	 * 根据品牌检索商品列表
	 * @param req
	 * @return
	 */
	JSONObject searchProductListByBrand(ProductSearchReq req);
	
	
	
	/**
	 * 自主品牌检索商品列表
	 * @param req
	 * @return
	 */
	JSONArray searchSelfOwnBrandProductList(ProductSearchReq req);
	
	/**
	 * 凑单商品
	 * @param req
	 * @return
	 */
	TogetherProductRspVo searchTogetherProductList(ProductSearchReq req);
	
	/**
	 * 创意生活商品列表
	 * @param req
	 * @return
	 */
	JSONArray searchLifeStyleProductList(ProductSearchReq req);
	
	/**
	 * 潮童商品列表
	 * @param req
	 * @return
	 */
	JSONObject searchKidsProductList(ProductSearchReq req);
	/**
	 * 查询sale专场断码区分类及分类尺码
	 * @return
	 */
	JSONArray searchSaleBreakingSort(ProductSearchReq req);
	
	/**
	 * 从大数据获取商品列表
	 * @return
	 */
	JSONObject searchProductListByBigData(BigDataSearchReq searchReq);
	
	/**
	 * 从大数据获取买了又买商品列表
	 * @param searchReq
	 * @return
	 */
	JSONObject searchPurchasedListByBigData(BigDataSearchReq searchReq);

	/**
	 * 构造brand或者shop
	 * @return
	 */
	void buildShopSearchParam(ProductSearchReq brandSearchReq, String shop);
	
	/**
	 * 搜索买了又买商品列表
	 */
	List<ProductSearchBo> searchPurchasedProductList(ProductSearchReq searchReq, ProductBo product);
}
