package com.yoho.gateway.controller.search;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONArray;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.utils.ResultHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.ProductSearchService;
import com.yoho.product.model.QuerySalesCategoryReq;


/**
 * 分类搜索商品控制器
 * @author mali
 *
 */
@Controller
public class CategoryProductSearchController {
	private static final Logger LOGGER = LoggerFactory.getLogger(CategoryProductSearchController.class);
	
	@Resource
	ServiceCaller service;
	
	@Autowired
	private ProductSearchService categoryProductSearchService;
	
	/**
	 * 根据分类的信息查询当前分类下的商品列表
	 * client_type 手机客户端类型 （android   iphone）
	 * userId      登陆用户的标识
	 *
	 * @param type       页面的标签字符串  “new” “price” “discount”
	 * @param limit 	  每页展示条数，默认10条
	 * @param sort       分类的Id
	 * @param page        当前页
	 * @param yhChannel  gender性别   1代表男  2代表女 3代表通用           假如传值1   则需要查询1,3     假如查询2 则需要查询2,3
	 * @param order       排序信息  s_t_desc
	 * @param p_d         打折区间，以逗号分隔
	 * @param color       颜色ID
	 * @param standard    规格
	 * @param categoryId  	 运营二级分类ID
	 * @param subCategoryId    运营三级分类ID
	 * @param brand		     品牌ID
	 * @param shop		     店铺ID
	 * @param price		     价格区间，以逗号分隔
	 * @param size		     尺寸ID，以逗号分隔
	 * @param gender	     性别，以逗号分隔
	 * @param outlets	     outlets=1 奥莱商品 outlets=2非奥莱商品
	 * @return 某分类下的商品列表
	 */
	@RequestMapping(params = "method=app.search.category")
	@ResponseBody
	@Cachable(needMD5=true, expire = ExpireTime.app_search_category)
	public ApiResponse queryProductListByCategory(
			@RequestParam(value = "type", required = false)String type, 
			@RequestParam(value = "limit", required = false)Integer limit, 
			@RequestParam(value = "sort", required = false)String sort,
			@RequestParam(value = "page", required = false)Integer page, 
			@RequestParam(value = "yh_channel", required = false)String yhChannel, 
			@RequestParam(value = "order", required = false)String order, 
			@RequestParam(value = "p_d", required = false) String p_d,
			@RequestParam(value = "color", required = false) String color,
			@RequestParam(value = "brand", required = false) String brand,
			@RequestParam(value = "shop_id", required = false) String shop,
			@RequestParam(value = "price", required = false) String price,
			@RequestParam(value = "size", required = false) String size,
			@RequestParam(value = "msort", required = false) String msort,
			@RequestParam(value = "misort", required = false) String misort,
			@RequestParam(value = "standard", required = false) String[] standard,
			@RequestParam(value = "categoryId", required = false)String categoryId,
			@RequestParam(value = "subCategoryId", required = false)String subCategoryId,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "age_level", required = false) String ageLevel,
			@RequestParam(value = "outlets", required = false) Integer outlets,
            @RequestParam(value = "app_version", required = false) String appVersion,
            @RequestParam(value = "client_type", required = false) String clientType) {
		LOGGER.info("queryProductListByCategory method=app.search.category, type:{},limit:{},sort:{},page:{},yh_channel:{},order:{},p_d:{},color:{},brand:{},price:{},size:{},gender:{},msort:{},misort:{},standard:{},categoryId:{},subCategoryId:{},outlets:{}",
				new Object[]{ type, limit, sort, page, yhChannel, order, p_d, color, brand, price, size, gender, msort,misort,standard,categoryId,subCategoryId,outlets});
		
		// 异步请求获取标签（商品参数）名称列表
		AsyncFuture<String[]> labelNameAsync = null;
		Integer category = null;
		if (StringUtils.isNotBlank(subCategoryId)) {
			category = new Integer(subCategoryId.indexOf(",") == -1 ? subCategoryId : subCategoryId.substring(0, subCategoryId.indexOf(",")));
		} else if (StringUtils.isNotBlank(categoryId)) {
			category = new Integer(categoryId.indexOf(",") == -1 ? categoryId : categoryId.substring(0, categoryId.indexOf(",")));
		}
		if (null != category) {
			QuerySalesCategoryReq req = new QuerySalesCategoryReq();
			req.setCategoryId(category);
			labelNameAsync = service.asyncCall("product.queryLabelNameListByCategoryId", req, String[].class);
		}

		// 组装参数
		ProductSearchReq req = new ProductSearchReq().setType(type).setLimit(limit).setSort(sort).setPage(page)
				.setYhChannel(yhChannel).setOrder(order).setBrand(brand).setColor(color).setGender(gender).setPd(p_d)
				.setParameter(standard).setPrice(price).setSize(size)
				.setMsort(msort).setMisort(misort).setCategoryId(categoryId).setClientType(clientType)
				.setSubCategoryId(subCategoryId).setOutlets(outlets).setAppVersion(appVersion).setAgeLevel(ageLevel)
				.setSearchFrom("search.category");
		
		JSONObject data = categoryProductSearchService.searchProductListByCategory(req, labelNameAsync, shop);

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
        ResultHandler.genderAdaptor(req,data);
		LOGGER.info("The time consuming of method=app.search.category req is {}", req);
		return new ApiResponse.ApiResponseBuilder().code(200).message("Category Product List.").data(data).build();
	}


	/**
	 * 奥莱潮品速递的商品列表 ，按照库存+销量由高到低排序（order=storageNum:desc,sales_num:desc），
	 * 过滤无货商品（stocknumber=1），固定30个
	 * @param yhChannel   假如传值1   则需要查询1,3     假如查询2  则需要查询2,3
	 * @param order       排序信息  s_s_desc s_n_desc
	 * @param gender	  性别，以逗号分隔 1代表男  2代表女  3代表通用
	 * @param stocknumber   库存  如 “stocknumber=2”，则过滤出库存量>=2的商品
	 * @param limit 	  每页展示条数，默认10条
	 * @param outlets	     outlets=1 奥莱商品 outlets=2非奥莱商品
	 */
	@RequestMapping(params = "method=app.search.trend")
	@ResponseBody
	@Cachable(needMD5=true, expire = ExpireTime.app_search_trend)
	public ApiResponse queryProductListByCategory(
			@RequestParam(value = "yh_channel", required = false)String yhChannel,
			@RequestParam(value = "order", required = false,defaultValue ="s_s_desc,s_n_desc" )String order,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "stocknumber", required = false,defaultValue = "1") Integer stocknumber,
			@RequestParam(value = "limit", required = false,defaultValue ="30" )Integer limit,
			@RequestParam(value = "outlets", required = false,defaultValue ="1") Integer outlets,
            @RequestParam(value = "app_version", required = false) String appVersion,
            @RequestParam(value = "client_type", required = false) String clientType) {

		LOGGER.info("outlets trend courier product list method=app.search.trend,yh_channel:{},order:{}, gender:{},stocknumber:{},limit:{},outlets:{}",
				 yhChannel, order,  gender,stocknumber,limit, outlets);
		// 组装参数
		ProductSearchReq req = new ProductSearchReq().setYhChannel(yhChannel).setOrder(order).setClientType(clientType)
                .setGender(gender).setStocknumber(stocknumber).setLimit(limit).setOutlets(outlets)
                .setAppVersion(appVersion).setSearchFrom("search.trend");

		JSONObject data = categoryProductSearchService.searchTrendCourierProductList(req);

		LOGGER.info("The time consuming of method=app.search.trend req is {}", req);
		return new ApiResponse.ApiResponseBuilder().code(200).message("outlets trend courier product list").data(data).build();
	}
}
