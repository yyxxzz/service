package com.yoho.gateway.controller.search;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.service.search.ProductSearchService;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 模糊搜索商品控制器
 * @author mali
 *
 */
@Controller
public class PuoductFuzzySearchController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PuoductFuzzySearchController.class);
	
	@Autowired
	private ProductSearchService categoryProductSearchService;

	/**
	 * 
	 * @param query   	关键词
	 * @param limit		每页显示条数默认20
	 * @param page		当前页数  从1开始
	 * @param yhChannel	渠道
	 * @param order		排序
	 * @param gender	性别
	 * 		yh_channel  gender  模糊搜索时为空
	 * @return 首页模糊搜索的商品列表，支持按商品skn、品牌搜索；或者店铺首页搜索
	 */
	@RequestMapping(params = "method=app.search.li")
	@ResponseBody
	@Cachable(needMD5=true, expire = ExpireTime.app_search_li)
	public ApiResponse fuzzyQueryProductList(@RequestParam(value = "query", required = false)String query,
			@RequestParam(value = "limit", required = false)Integer limit, 
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "yh_channel", required = false)String yhChannel, 
			@RequestParam(value = "order", required = false)String order, 
			@RequestParam(value = "msort", required = false) String msort,
			@RequestParam(value = "misort", required = false) String misort,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "shop_id", required = false) String shop,
			@RequestParam(value = "price", required = false) String price,
			@RequestParam(value = "promotion", required = false) String promotion,
			@RequestParam(value = "size", required = false) String size,
			@RequestParam(value = "sort", required = false)String sort,
			@RequestParam(value = "color", required = false) String color,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "p_d", required = false) String p_d,
			@RequestParam(value = "age_level", required = false) String ageLevel,
			@RequestParam(value = "outlets", required = false) Integer outlets,
            @RequestParam(value = "app_version", required = false) String appVersion,
            @RequestParam(value = "client_type", required = false) String clientType) {
		LOGGER.info("fuzzyQueryProductList method=app.search.li .query:{}, limit:{}, page:{}, yh_channel:{}, order:{}, gender:{},msort:{},misort:{},brand:{},shop:{},price:{},promotion:{},outlets:{},ageLevel:{}",
				new Object[]{query, limit, page, yhChannel, order, gender, msort,misort,brand,shop,price,promotion,outlets,ageLevel});
		// 获取处理过的关键词，且记录搜索记录
		query = getQuery(query);
		// 组装参数
		ProductSearchReq req = new ProductSearchReq().setLimit(limit).setPage(page).setYhChannel(yhChannel).setOrder(order).setClientType(clientType)
				.setGender(gender).setMsort(msort).setMisort(misort).setPrice(price).setPromotion(promotion).setSize(size).setFrom(from)
				.setSort(sort).setColor(color).setPd(p_d).setQuery(query).setBrand(brand).setShop(shop).setOutlets(outlets).setAppVersion(appVersion)
                // 该接口不需要查出全球购商品
//                .setIncludeGlobal("N")
                .setAgeLevel(ageLevel).setSearchFrom("search.li");
		
		// 调用搜索接口、搜索结果进行处理
		JSONObject data = categoryProductSearchService.searchFuzzyProductList(req, shop);

		if(outlets!=null&&outlets==1){
			// 如果是奥莱，只保留即将售罄的标签
			JSONArray jsonProductList = data.getJSONArray(SearchConstants.NodeConstants.KEY_PRODUCT_LIST);
			for (int i = 0; i < jsonProductList.size(); i++) {
				JSONObject jsonProduct = jsonProductList.getJSONObject(i);
				// 这里获取到的是值，不是引用，要进行覆盖
				JSONArray jsonTags = jsonProduct.getJSONArray("tags");
				if (!jsonTags.isEmpty()) {
					// 只保留这个，其他的都不要 jsonTags.remove("is_soon_sold_out");
					jsonTags.remove("is_limited");
					jsonTags.remove("is_yohood");
					jsonTags.remove("is_new");
					jsonTags.remove("is_discount");
				}
				jsonProduct.put("tags", jsonTags);
			}
		}
		
		return new ApiResponse.ApiResponseBuilder().code(200).message("Search List.").data(data).build();
	}

	// 封装搜索接口参数为对象形式
	private String getQuery(String query) {
		//不为空才会去做处理
		if(StringUtils.isNotBlank(query)){
			// 关键词进行HTML预定字符串转换(如<转成&lt等)
			query = StringEscapeUtils.escapeHtml3(query);
		}
		return query;
	}
}
