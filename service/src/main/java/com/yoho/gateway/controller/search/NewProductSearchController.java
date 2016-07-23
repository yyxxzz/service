package com.yoho.gateway.controller.search;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.ProductSearchService;

/**
 * 新品到着请求
 * @author mali
 *
 */
@Controller
public class NewProductSearchController {
	/**
	 * LOG
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(NewProductSearchController.class);
	
	@Autowired
	private ProductSearchService categoryProductSearchService;
	
	@Autowired
	
	// 创意生活频道
	private final static String LIFE_CHANNEL = "4";
	
	// 自主品牌id
	@Value("${self.own.brand}")
	private String selfOwnBrandId;
	
	/**
	 * 根据dayLimit查询某一区间的商品列表，包括两部分，8个自主品牌商品+其他商品列表
	 * @param dayLimit   时间区间标识   1：代表3天之内出售的，2：代表7天之内出售的，3代表30天以内的
	 * @param yhChannel	渠道
	 * @param order		排序
	 * @param limit		每页展示条数
	 * @param page		当前页数，从1开始
	 * @param gender	性别
	 * @return 根据dayLimit查询某一区间的商品列表
	 */
	@RequestMapping(params = "method=app.search.newProduct")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_search_newProduct)
	public ApiResponse searchNewProductList(@RequestParam(value = "dayLimit", required = false)String dayLimit,
			@RequestParam(value = "yh_channel", required = false)String yhChannel, 
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "sort", required = false)String sort,
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "shop_id", required = false) String shop,
			@RequestParam(value = "color", required = false) String color,
			@RequestParam(value = "size", required = false) String size,
			@RequestParam(value = "price", required = false) String price,
			@RequestParam(value = "p_d", required = false) String p_d,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "age_level", required = false) String ageLevel,
			@RequestParam(value = "app_version", required = false) String appVersion,
            @RequestParam(value = "client_type", required = false) String clientType) {
		LOGGER.info("searchNewProductList method=app.search.newProduct in. dayLimit:{}, yhChannel:{}, limit:{}, order:{}, page:{}, gender:{},sort:{},brand:{},color:{},size:{},price:{},p_d:{},ageLevel:{}",
				new Object[]{dayLimit, yhChannel, limit, order, page, gender, sort, brand, color, size, price, p_d, ageLevel});
		String productSknStr = null;
		JSONArray selfOwnBrandProductList = null;
		// 根据sort、brand、color、size、price、gender、ageLevel筛选时不出现自主品牌的商品列表
		boolean ownBrandFlag = checkIsQueryOwnBrand(sort, brand, color, size, price, p_d, yhChannel, page, ageLevel);
		
		if (ownBrandFlag) {
			// 1. page=1时，按照品牌取最新商品接口，获取自主品牌的8个商品
			
			// 儿童频道只显示2条自主品牌 , 其他频道则显示8条自主品牌记录， 自主品牌来源于配置
			ProductSearchReq selfOwnBrandProductreq = new ProductSearchReq().setYhChannel(yhChannel).setGender(gender)
					.setLimit("3".equals(yhChannel) ? 2 : 8).setDayLimit(dayLimit).setBrand(selfOwnBrandId).setSearchFrom("search.ownBrandProduct");
			
			selfOwnBrandProductList = categoryProductSearchService.searchSelfOwnBrandProductList(selfOwnBrandProductreq);
			
			// 2. 过滤掉已获取的商品，下面接口需要排除掉已经查询到的商品
			productSknStr = getExistBrandProudtSkn(selfOwnBrandProductList);
		}
		
		// 3. 调用检索的推荐搜索接口，查询推荐列表
		limit = getLimit(selfOwnBrandProductList, limit);
		ProductSearchReq req = new ProductSearchReq().setLimit(limit).setPage(page).setYhChannel(yhChannel).setGender(gender).setSort(sort).setDayLimit(dayLimit)
			.setOrder(order).setBrand(brand).setShop(shop).setColor(color).setSize(size).setPrice(price).setPd(p_d).setNotProductSkn(productSknStr).setClientType(clientType)
                .setAgeLevel(ageLevel).setAppVersion(appVersion).setSearchFrom("search.newProduct");
		JSONObject data = categoryProductSearchService.searchNewProductList(req);
		
		if(ownBrandFlag){
			// 4. 合并两者的结果集
			data = mergeProductSearch(selfOwnBrandProductList, data);
		}
		
		LOGGER.info("The time consuming of method=app.search.newProduct dayLimit:{}, yhChannel:{}, limit:{}, order:{}, page:{}, gender:{}", 
				new Object[]{dayLimit, yhChannel, limit, order, page, gender});
		return new ApiResponse.ApiResponseBuilder().code(200).message("Search List.")
				.data(data).build();
	}
	
	// 自主品牌的条目数需要计算在列表的总条目中
	private Integer getLimit(JSONArray selfOwnBrandProductList, Integer limit) {
		if (null == selfOwnBrandProductList) {
			return limit;
		}
		if (null == limit) {
			limit = 10;
		}
		return limit - selfOwnBrandProductList.size();        // 自主品牌最多8条，分页最少10条。此处不考虑负数的场景
	}

	private boolean checkIsQueryOwnBrand(String sort, String brand, String color, String size, String price, String p_d, String yhChannel, Integer page, String ageLevel) {
		return (page == null || page == 1) && !LIFE_CHANNEL.equals(yhChannel) && StringUtils.isEmpty(sort) && 
				StringUtils.isEmpty(brand) && StringUtils.isEmpty(color) 
				&& StringUtils.isEmpty(size) && StringUtils.isEmpty(price) && StringUtils.isEmpty(p_d) && StringUtils.isEmpty(ageLevel);
	}

	private JSONObject mergeProductSearch(JSONArray selfOwnBrandProductArray,
			JSONObject data) {
		JSONArray productListArray;
		if (null == data) {
			productListArray = new JSONArray();
		} else {
			productListArray = data.getJSONArray("product_list");
		}
		int size = 0;
		if (null != selfOwnBrandProductArray) {
			size = selfOwnBrandProductArray.size();
			for (int i = 0; i < size; i++) {
				JSONObject product = selfOwnBrandProductArray.getJSONObject(i);
				productListArray.add(i, product);
			}
		}
		return data;
	}
	
	private String getExistBrandProudtSkn(JSONArray productList) {
		StringBuilder sb = new StringBuilder();
		int size = 0;
		if (null != productList) {
			size = productList.size();
			for (int i = 0; i < size; i++) {
				JSONObject product = productList.getJSONObject(i);
				sb.append(product.get("product_skn")).append(",");
			}
			if(size!=0){
				sb = new StringBuilder(sb.substring(0, sb.length() - 1));
			}
		}
		return sb.toString();
	}
}
