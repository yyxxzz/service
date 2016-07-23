package com.yoho.gateway.controller.search;

import com.yoho.gateway.utils.ResultHandler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.service.search.ProductSearchService;

/**
 * 折扣精品请求
 * @author mali
 * app.search.sales
 */
@Controller
public class SaleProductSearchController {
	/**
	 * LOG
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SaleProductSearchController.class);
	/**
	 * 折扣专场默认排序
	 */
	private static final String DEFAULT_ORDER="f_s_desc";
	/**
	 * 会员专场默认排序
	 */
	private static final String VIP_DEFAULT_ORDER="s_t_desc";
	
	private static final int BREAKING_DEFAULAT_VALUE=1;
	private static final int OUTLETS_VALUE = 4;
	
	/**
	 * PC端断码区
	 */
	private static final int PC_BREAKING_VALUE = 5;

	@Autowired
	private ProductSearchService productSearchService;
	
	/**
	 * 
	 * @param p_d 折扣
	 * @param yhChannel 频道
	 * @param limit 
	 * @param order 排序
	 * @param page 分页
	 * @param gender  性别
	 * @param sort 分类
	 * @param brand 品牌
	 * @param color 颜色
	 * @param size 尺码
	 * @param saleType 1表示断码区，2表示会员专区，3 折扣专区，4表示奥莱
	 * @param breakSize 当为断码区的时候，ios调接口不好处理，传此字段再统一处理
	 * @param breakSort 当为断码区的时候，ios调接口不好处理，传此字段再统一处理
	 * @param productPool 商品池
	 * @param price 价格
	 * @return
	 * @throws GatewayException 
	 */
	@RequestMapping(params = "method=app.search.sales")
	@ResponseBody
	@Cachable(needMD5=true, expire = ExpireTime.app_search_sales)
	public ApiResponse searchSalesProductList(@RequestParam(value = "p_d", required = false)String p_d,
			@RequestParam(value = "yh_channel", required = false)String yhChannel, 
			@RequestParam(value = "limit", required = false) Integer limit,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "page", required = false) Integer page,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "sort", required = false)String sort,
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "shop_id", required = false) String shop,
			@RequestParam(value = "color", required = false) String color,
			@RequestParam(value = "size", required = false) String size,
			@RequestParam(value = "breakSort", required = false) String breakSort,
			@RequestParam(value = "breakSize", required = false) String breakSize,
			@RequestParam(value = "saleType", required = false ) Integer saleType,
			@RequestParam(value = "productPool", required = false) String productPool,
			@RequestParam(value = "price", required = false) String price,
			@RequestParam(value = "age_level", required = false) String ageLevel,
            @RequestParam(value = "app_version", required = false) String appVersion,
            @RequestParam(value = "client_type", required = false) String clientType
			) throws GatewayException 
	{
		LOGGER.info("searchSalesProductList method=app.search.sales in. p_d:{}, yhChannel:{}, limit:{}, order:{}, page:{}, gender:{},sort:{},brand:{},color:{},size:{},price:{},productPool:{},",
				new Object[]{p_d, yhChannel, limit, order, page, gender,sort,brand,color,size,price,productPool});
		// 组装参数
		ProductSearchReq req = new ProductSearchReq().setLimit(limit)
				.setPage(page).setYhChannel(yhChannel).setGender(gender)
				.setSort(sort).setOrder(order).setBrand(brand).setShop(shop).setColor(color)
				.setSize(size).setPrice(price).setPd(p_d).setClientType(clientType)
				.setProductPool(productPool).setIsdiscount("Y").setAgeLevel(ageLevel).setAppVersion(appVersion)
				.setStocknumber(1) // 默认查询有库存的
				.setSearchFrom("search.sales");

		if (saleType == null) saleType = 0;

		switch (saleType) {
			case 1:
				req.setBreaking(SearchConstants.IndexNameConstant.SALE_DEFAULT_VALUE);
				if(StringUtils.isNotEmpty(breakSort)){
					req.setSort(breakSort);
				}
				if(StringUtils.isNotEmpty(breakSize)){
					req.setSize(breakSize);
				}
				//设置sku库存
				req.setStorageNum(1);
				break;
			case 2:
				req.setVdt(SearchConstants.IndexNameConstant.SALE_DEFAULT_VALUE);

				if(StringUtils.isEmpty(order)){
					req.setOrder(VIP_DEFAULT_ORDER);
				}
				break;
			case 3:
				req.setFilterSaleAction(SearchConstants.IndexNameConstant.SALE_DEFAULT_VALUE);
				if(StringUtils.isEmpty(order)){
					req.setOrder(DEFAULT_ORDER);
				}
				break;
			case OUTLETS_VALUE:
				// 查询奥莱
				req.setOutlets(1);
				// 没库存也要查询
				req.setStocknumber(-1);
				// 折扣，非折扣都查询
				req.setIsdiscount(null);
				// 奥莱不分频道
				req.setYhChannel(null);
				break;
			case PC_BREAKING_VALUE:
				req.setBreaking(SearchConstants.IndexNameConstant.SALE_DEFAULT_VALUE);
				if(StringUtils.isNotEmpty(sort)){
					req.setSort(sort);
				}else if(StringUtils.isNotEmpty(breakSort)){
					req.setSort(breakSort);
				}
				if(StringUtils.isNotEmpty(breakSize)){
					req.setSize(breakSize);
				}
				//设置sku库存
				req.setStorageNum(1);
				break;
		}
		// 调用检索的推荐搜索接口，查询sale列表

		JSONObject data = productSearchService.searchSalesProductList(req);

		if(data==null){
			return new ApiResponse.ApiResponseBuilder().code(200).message("Sales Product List.").data(data).build();
		}

		switch (saleType) {
			//如果是断码区，删除filter节点的size和sort节点
			case BREAKING_DEFAULAT_VALUE:
				data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER).remove(SearchConstants.NodeConstants.FILTER_KEY_SIZE);
				data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER).remove(SearchConstants.NodeConstants.KEY_GROUP_SORT);
				break;
			// 如果是奥莱，保保留即将结束标签
			case OUTLETS_VALUE:
				JSONArray jsonProductList = data.getJSONArray(SearchConstants.NodeConstants.KEY_PRODUCT_LIST);
				for (int i = 0; i < jsonProductList.size(); i++ ) {
					JSONObject jsonProduct = jsonProductList.getJSONObject(i);
					// 这里获取到的是值，不是引用，要进行覆盖
					JSONArray jsonTags = jsonProduct.getJSONArray("tags");
					if (!jsonTags.isEmpty()) {
						// 只保留这个，其他的都不要
						// jsonTags.remove("is_soon_sold_out");
						jsonTags.remove("is_limited");
						jsonTags.remove("is_yohood");
						jsonTags.remove("is_new");
						jsonTags.remove("is_discount");
					}
					jsonProduct.put("tags", jsonTags);
				}
				break;
		}
        ResultHandler.genderAdaptor(req,data);

		LOGGER.info("The time consuming of method=app.search.sales,  p_d:{}, yhChannel:{}, limit:{}, order:{}, page:{}, gender:{}, breakSort:{}, breakSize:{}, saleType:{}, productPool:{}", 
				new Object[]{p_d, yhChannel, limit, order, page, gender, breakSort, breakSort, saleType, productPool});
		return new ApiResponse.ApiResponseBuilder().code(200).message("Sales Product List.").data(data).build();
	}
}
