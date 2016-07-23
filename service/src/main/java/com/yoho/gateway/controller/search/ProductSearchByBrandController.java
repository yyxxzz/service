package com.yoho.gateway.controller.search;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.service.product.GlobalSheepTagsFilterService;
import com.yoho.gateway.service.search.ProductBrandSearchService;
import com.yoho.gateway.service.search.ProductSearchService;
import com.yoho.gateway.service.search.ShopService;

/**
 * Created by sailing on 2015/11/19.
 */
@Controller
public class ProductSearchByBrandController {

    private final Logger logger = LoggerFactory.getLogger(ProductSearchByBrandController.class);
    @Autowired
    private ProductBrandSearchService productBrandSearchService;

    @Autowired
    private ProductSearchService productSearchService;

    @Autowired
    private ShopService shopService;

	@Autowired
	private GlobalSheepTagsFilterService globalSheepTagsFilterService;
	/**
	 * 根据频道查询其下所有品牌 搜索引擎查询上架商品，追溯其品牌
	 * 4.5版本开始不用该接口
	 * @param channel
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.brand.brandlist")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_brand_brandlist)
	public ApiResponse queryBrandListByChannl(@RequestParam(value = "yh_channel", required = false) String channel) throws GatewayException {
		logger.info("[method=app.brand.brandlist] param yh_channel is {}", channel);
		JSONObject responseEntity = productBrandSearchService.queryBrandListByChannl(channel);
		return new ApiResponse(200, null == responseEntity ? "no data exsit" : "success", responseEntity);
	}
	
	/**
	 * 品牌一览表功能（4.5版本）
	 * @param channel
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.brand.newBrandList")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_brand_brandlist)
	public ApiResponse queryNewBrandListByChannel(@RequestParam(value = "yh_channel", required = false) String channel) throws GatewayException {
		logger.info("[method=app.brand.newBrandList] in  param yh_channel is {}", channel);
		Map<String, Object> responseEntity = productBrandSearchService.queryNewBrandListByChannel(channel);
		logger.debug("[method=app.brand.newBrandList] out result is {}", JSON.toJSON(responseEntity));
		return new ApiResponse(200, null == responseEntity ? "no data exsit" : "success", responseEntity);
	}

	/**
	 * 根据品牌搜索品牌下具体的商品，点击品牌进去是不区分性别的，如果品牌不存在单品店，包括多个多品店，展示多品店列表
	 *
	 * @param channel
	 * @param brand
	 * @param shop
	 *            ：表示搜索指定店铺商品
	 * @param order
	 * @param limit
	 * @param page
	 * @param gender
	 * @param color
	 * @param price
	 * @param size
	 * @param p_d
	 * @param sort
	 * @param tagsFilter
	 *            标签过滤 1:全球购羊头品牌标签过滤
	 * @return
	 */
	@RequestMapping(params = "method=app.search.brand")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_search_brand)
	public ApiResponse queryBrand(@RequestParam(value = "yh_channel", required = false) String channel, @RequestParam(value = "brand", required = false) String brand,
								  @RequestParam(value = "shop_id", required = false) String shop, @RequestParam(value = "order", required = false) String order,
								  @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit, @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
								  @RequestParam(value = "gender", required = false) String gender, @RequestParam(value = "color", required = false) String color,
								  @RequestParam(value = "price", required = false) String price, @RequestParam(value = "size", required = false) String size, @RequestParam(value = "p_d", required = false) String p_d,
								  @RequestParam(value = "sort", required = false) String sort, @RequestParam(value = "tags_filter", required = false, defaultValue = "0") Integer tagsFilter,
								  @RequestParam(value = "age_level", required = false) String ageLevel,
								  @RequestParam(value = "app_version", required = false) String appVersion,@RequestParam(value = "client_type", required = false) String clientType) {

		ProductSearchReq brandSearchReq = new ProductSearchReq().setYhChannel(channel).setBrand(brand).setShop(shop).setOrder(order).setLimit(limit).setPage(page).setGender(gender).setColor(color)
				.setPrice(price).setSize(size).setPd(p_d).setSort(sort).setAgeLevel(ageLevel).setAppVersion(appVersion).setClientType(clientType).setSearchFrom("search.brand");
		logger.info("[method=app.search.brand] params  {}", brandSearchReq);
		JSONObject responseEntity = productSearchService.searchProductListByBrand(brandSearchReq);
		// 是全球购羊头品牌过滤标签
		if (1 == tagsFilter) {
			globalSheepTagsFilterService.filterTags(responseEntity);
		}
		// 如果品牌有多个店铺中存在
		if (StringUtils.isNotEmpty(brand) && brand.split(SearchConstants.IndexNameConstant.SEPERATOR_COMMA).length==1) {
			shopService.processShopList(responseEntity, Integer.valueOf(brand), brandSearchReq.getPage());
		}

		return new ApiResponse(200, null == responseEntity ? "no data exsit" : "success", responseEntity);
	}
	
	/**
	 * 根据店铺搜索店铺下具体的商品，单品店根据品牌搜索商品列表，多品店根据shop搜索商品列表
	 *
	 * @param channel
	 * @param shop：表示搜索指定店铺商品
	 * @param order
	 * @param limit
	 * @param page
	 * @param gender
	 * @param color
	 * @param price
	 * @param size
	 * @param p_d
	 * @param sort
	 * @param tagsFilter
	 *            标签过滤 1:全球购羊头品牌标签过滤
	 * @return
	 */
	@RequestMapping(params = "method=app.search.shop")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_search_shop)
	public ApiResponse queryShop(@RequestParam(value = "yh_channel", required = false) String channel, 
								  @RequestParam(value = "shop_id", required = false) String shop, 
								  @RequestParam(value = "order", required = false) String order,
								  @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit, 
								  @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
								  @RequestParam(value = "gender", required = false) String gender, 
								  @RequestParam(value = "color", required = false) String color,
								  @RequestParam(value = "price", required = false) String price, 
								  @RequestParam(value = "size", required = false) String size, 
								  @RequestParam(value = "p_d", required = false) String p_d,
								  @RequestParam(value = "sort", required = false) String sort, 
								  @RequestParam(value = "tags_filter", required = false, defaultValue = "0") Integer tagsFilter,
								  @RequestParam(value = "age_level", required = false) String ageLevel,
								  @RequestParam(value = "app_version", required = false) String appVersion,
                                  @RequestParam(value = "client_type", required = false) String clientType) {

		ProductSearchReq brandSearchReq = new ProductSearchReq().setYhChannel(channel).setOrder(order).setLimit(limit).setPage(page).setGender(gender).setColor(color)
				.setPrice(price).setSize(size).setPd(p_d).setSort(sort).setAgeLevel(ageLevel).setAppVersion(appVersion).setClientType(clientType).setSearchFrom("search.brand");

		logger.info("[method=app.search.brand] params  {}", brandSearchReq);
		
		productSearchService.buildShopSearchParam(brandSearchReq, shop);
		
		JSONObject responseEntity = productSearchService.searchProductListByBrand(brandSearchReq);
		// 是全球购羊头品牌过滤标签
		if (1 == tagsFilter) {
			globalSheepTagsFilterService.filterTags(responseEntity);
		}

		return new ApiResponse(200, null == responseEntity ? "no data exsit" : "success", responseEntity);
	}
}