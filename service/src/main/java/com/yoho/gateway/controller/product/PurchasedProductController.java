package com.yoho.gateway.controller.product;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.search.BigDataSearchReq;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.service.search.ProductSearchService;
import com.yoho.gateway.utils.ListUtils;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductSearchBo;
import com.yoho.product.request.BaseRequest;
/**
 * 
 * @author Xuxin Li created by 2016/01/21
 *
 */
@Controller
public class PurchasedProductController {
	
	private Logger logger = LoggerFactory.getLogger(PurchasedProductController.class);
	@Autowired
	private ServiceCaller serviceCaller;
	
	@Autowired
	private ProductSearchService productSearchService;
	
	// 默认条数
	private final static Integer DEFAULT_PURCHASEDSIZE = 2;
	
	//查询时brandID不能为空
	private final static int PRODUCTSKN_IS_NULL_CODE = 500;
	private final static int REQUEST_SUCCESS_CODE = 200;
	private final static String PRODUCTSKN_IS_NULL_MSG = "productSkn must be not null!";
	private final static String REQUEST_SUCCESS_MSG = "app.product.purchased query successed!";
	
	/**
	 * 买了再买（4.8版本开始弃用）
	 * @param productSkn
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.product.purchased")
	@ResponseBody
	@Cachable(expire= ExpireTime.app_product_purchased)
	public ApiResponse getPurchasedProductList(@RequestParam(value="productSkn", required=false) Integer productSkn) throws GatewayException {
		logger.info("start method=app.product.purchased param productSkn is {}", productSkn);
		//校验参数productSkn不能为空，或者为0
		if(productSkn==null||productSkn < 1){
			logger.warn("Parameter productSkn is {}", productSkn);
			throw new GatewayException(PRODUCTSKN_IS_NULL_CODE, PRODUCTSKN_IS_NULL_MSG);
		}
		
		BaseRequest<Integer> req = new BaseRequest<Integer>();
		req.setParam(productSkn);
		ProductBo product = serviceCaller.call("product.queryBaseProduct", req, ProductBo.class);
		if(null == product){
			return new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message(REQUEST_SUCCESS_MSG).data(null).build();
		}
		
		ProductSearchReq searchReq = new ProductSearchReq();
		searchReq.setBrand(String.valueOf(product.getBrandId()))
		         .setViewNum(100)
		         .setStocknumber(SearchConstants.IndexNameConstant.DEFAULT_STOCKNUMBER)
		         .setOrder("shelve_time:desc")
		         .setSearchFrom("product.purchased");

		List<ProductSearchBo> data = productSearchService.searchPurchasedProductList(searchReq, product);
		
		return new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message(REQUEST_SUCCESS_MSG).data(data).build();
	}
	
	/**
	 * 买了再买功能（大数据推荐功能，4.8版本开始使用）
	 * @param productSkn
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.recommend.purchased")
	@ResponseBody
	@Cachable(expire= ExpireTime.app_product_purchased)
	public ApiResponse getRecommendPurchasedList(@RequestParam(value = "productSkn", required = false) Integer productSkn,
			@RequestParam(value = "udid", required = false) String udid,
			@RequestParam(value = "rec_pos", required = false) String recPos,
			@RequestParam(value = "limit", required = false) Integer limit) throws GatewayException {
		
		logger.info("getRecommendPurchasedList method=app.recommend.purchased productSkn is{}: udid is:{}, recPos is:{}, limit is:{}", productSkn, udid, recPos, limit);
		
		//校验参数productSkn不能为空，或者为0
		if(productSkn==null||productSkn < 1){
			logger.warn("Parameter productSkn is {}", productSkn);
			throw new GatewayException(PRODUCTSKN_IS_NULL_CODE, PRODUCTSKN_IS_NULL_MSG);
		}		
		if (null == udid) {
			return new ApiResponse.ApiResponseBuilder().code(404).message("udid Is Null").data(null).build();
		}
		if(null == recPos){
			recPos = "100005";
		}
		//rec id
		UUID recId = UUID.randomUUID();
		BigDataSearchReq searchReq = new BigDataSearchReq();
		limit = null == limit ? DEFAULT_PURCHASEDSIZE : limit;
		searchReq.setPage(1).setLimit(limit);
		searchReq.setUdid(udid);
		searchReq.setRecPos(recPos);
		searchReq.setProductSkn(productSkn);
		JSONObject data = productSearchService.searchPurchasedListByBigData(searchReq);
		if(null==data){
			//找不到商品，返回一个默认的
			logger.info("can not find any rec products by: productSkn:{} udid:{} rec_pos:{} limit:{}", productSkn, udid, recPos, limit);
			return new ApiResponse.ApiResponseBuilder().code(200).message("Product_list").data(this.defaultRecDatas(recId.toString())).build();
		}
		JSONArray jsonArray = data.getJSONArray("product_list");
		if(jsonArray == null || jsonArray.size() == 0){
            //找不到商品，返回一个默认的
			logger.info("can not find any rec products by: productSkn:{} udid:{} rec_pos:{} limit:{}", productSkn, udid, recPos, limit);
			return new ApiResponse.ApiResponseBuilder().code(200).message("Product_list").data(this.defaultRecDatas(recId.toString())).build();
		}

		if (jsonArray.size() > limit) { 
			jsonArray = new JSONArray(ListUtils.getSubList(jsonArray, 0, limit));
		}

		data.put("product_list", jsonArray);
		data.remove("page");
		data.remove("page_total");
		data.remove("total");

		return new ApiResponse.ApiResponseBuilder().code(200).message("Product List.").data(data).build();
	}
	
	private JSONObject defaultRecDatas(String recId){
		//找不到商品，返回一个默认的
		JSONObject default_data = new JSONObject();
		JSONArray  products = new JSONArray();
		default_data.put("rec_id", recId);
		default_data.put("product_list", products);
		return default_data;
 	}

}
