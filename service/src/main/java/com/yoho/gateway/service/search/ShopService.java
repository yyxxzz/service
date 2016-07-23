package com.yoho.gateway.service.search;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.model.product.ShopsVo;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.ShopsBo;
import com.yoho.product.model.ShopsBrandsBo;

/**
 * 店铺相关的接口
 * @author wangshusheng
 *
 */
public interface ShopService {
	/**
	 * 根据品牌id查询包含该品牌的店铺列表
	 * @param brandId 品牌id
	 * @return 匹配的店铺列表
	 */
	List<ShopsVo> getShopListByBrandId(Integer brandId);
	
	/**
	 * 根据品牌id查询包含该品牌的店铺列表
	 * @param brandId 品牌id
	 * @return 匹配的店铺列表
	 */
	List<BrandBo> getBrandListByShopId(Integer shopId);
	
	/**
	 * 根据品牌id列表查询包含的品类列表
	 * @param brandId 品牌id
	 * @return 匹配的店铺列表
	 */
	Object searchSortByBrandId(ProductSearchReq req);

	/**
	 * 判断该品牌id是否是无店铺（老的品牌页）、还是在1个单品店里、还是在多个多品店里
	 * @param brandId 品牌id
	 * @return 
	 */
	void processShopList(JSONObject data, Integer brandId, Integer page);

	Map<Integer, List<ShopsBrandsBo>> queryAllShopBrandList(List<Integer> brandIds);

	/**
	 * 根据品牌id查询包含该品牌的店铺列表
	 * @param brandId 品牌id
	 * @return 匹配的店铺列表
	 */
	List<ShopsBo> getShopBoList(List<Integer> shopIds);
}
