package com.yoho.gateway.controller.search;

import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.search.BigDataSearchReq;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.PreferenceSearchService;
import com.yoho.gateway.service.search.ProductSearchService;
import com.yoho.gateway.utils.ListUtils;
import com.yoho.gateway.utils.ProductUtils;
import com.yoho.product.model.html5.YourPreferProduct;

@Controller
public class PreferenceSearchController {
	
	private final Logger logger = LoggerFactory.getLogger(PreferenceSearchController.class);
	
	// 默认优选的条数
	private final static Integer DEFAULT_PREFERENCESIZE = 30;
	
    @Autowired
    private ProductSearchService productSearchService;
    
    @Autowired
    private PreferenceSearchService preferenceSearchService;
	
	/**
	 * 商品页的为你优选,优选逻辑都是同一个品牌下的商品
	 *
	 * @param yhchannel 频道
	 * @param brandId   品牌id
	 * @return
	 * @throws GatewayException
	 */
	@CrossOrigin(value = "${gateway.domain.url}")
	@RequestMapping(params = "method=app.product.preference")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_product_preference)
	public ModelAndView queryPreference(@RequestParam(value = "yhchannel", required = true) Integer yhchannel, @RequestParam(value = "brandId", required = false) Integer brandId)
			throws GatewayException {

		if (null == yhchannel) {
			logger.warn(" yhchannel Is Null");
		}
		// 商品详情页没有品牌就没有为你优选
		if (null == brandId) {
			return new ModelAndView("error", getErrorModelMap());
		}
		logger.info("queryPreference method=app.product.preference  yhchannel is:{},brandId is:{}", yhchannel, brandId);
		
		ProductSearchReq req = new ProductSearchReq().setBrand(String.valueOf(brandId)).setYhChannel(null==yhchannel?null:String.valueOf(yhchannel))
				.setSearchFrom("query.preference");
		JSONArray preferenceJsonArray = preferenceSearchService.queryPreference(req);
		if (null != preferenceJsonArray && preferenceJsonArray.size() >= DEFAULT_PREFERENCESIZE) {
			preferenceJsonArray = new JSONArray(ListUtils.getSubList(preferenceJsonArray, 0, DEFAULT_PREFERENCESIZE));
		}
		logger.info("queryPreference\"method=app.product.preference exit yhchannel is:{},brandId is:{} preferenceJsonArray size:{}", yhchannel, brandId, null == preferenceJsonArray ? 0
				: preferenceJsonArray.size());
		ModelMap model = new ModelMap();
		ModelAndView mav = null;
		List<YourPreferProduct> yourPreferProducts = buildYourPreferPrds(preferenceJsonArray);
		if (CollectionUtils.isNotEmpty(yourPreferProducts)) {
			model.addAttribute("recommendList", yourPreferProducts);
			mav = new ModelAndView("recommend-content", model);
		}
		return mav;
	}
	
	/**
	 * //app 端除去商品页的为你优选
	 *
	 * @param yhchannel 频道
	 * @return
	 */
	@RequestMapping(params = "method=app.home.preference")
	@ResponseBody
	@Cachable(expire = ExpireTime.app_product_preference)
	public ApiResponse querySortPreference(@RequestParam(value = "yh_channel", required = true) Integer yhchannel) throws GatewayException {
		if (null == yhchannel) {
			return new ApiResponse.ApiResponseBuilder().code(404).message("yh_channel Is Null").data(null).build();
		}
		logger.info("querySortPreference method=app.home.preference yhchannel is:{}", yhchannel);
		ProductSearchReq req = new ProductSearchReq().setYhChannel(null==yhchannel?null:String.valueOf(yhchannel)).setSearchFrom("query.preference");
		JSONArray jsonArray = preferenceSearchService.querySortPreference(req);
		if (CollectionUtils.isNotEmpty(jsonArray)) {
			if (jsonArray.size() > DEFAULT_PREFERENCESIZE) { // 最多返回9个
				jsonArray = new JSONArray(ListUtils.getSubList(jsonArray, 0, DEFAULT_PREFERENCESIZE));
			}
		}
		logger.info("querySortPreference method=app.home.preference exit yhchannel is:{}", yhchannel);
		return new ApiResponse.ApiResponseBuilder().code(200).message("Product List.").data(jsonArray).build();
	}
	
	/**
	 * //app 端除去商品页的为你优选新版（带推荐位功能）
	 *
	 * @param yhchannel 频道
	 * @return
	 */
	@RequestMapping(params = "method=app.home.newPreference")
	@ResponseBody
	@Cachable(expire=300,excludeL2Args={1,2})
	public ApiResponse queryNewPreference(
			@RequestParam(value = "yh_channel", required = false) String yhchannel,
			@RequestParam(value = "uid", required = false) String uid,
			@RequestParam(value = "udid", required = false) String udid,
			@RequestParam(value = "rec_pos", required = false) String recPos,
			@RequestParam(value = "limit", required = false) Integer limit)
			throws GatewayException {
		logger.info("queryNewPreference method=app.home.newPreference yhchannel is:{}, uid is{}: udid is:{}, recPos is:{}, limit is:{}", yhchannel, uid, udid, recPos, limit);
		if (StringUtils.isEmpty(udid)) {
			return new ApiResponse.ApiResponseBuilder().code(404).message("udid Is Null").data(null).build();
		}
		if(StringUtils.isEmpty(recPos)){
			recPos = getRecPos(yhchannel);
		}
		//rec id
		UUID recId = UUID.randomUUID();
		BigDataSearchReq searchReq = new BigDataSearchReq();
		limit = null == limit ? DEFAULT_PREFERENCESIZE : limit;
		searchReq.setYhChannel(yhchannel).setUserId(uid).setPage(1).setLimit(limit);//30个不分页
		searchReq.setUdid(udid);
		searchReq.setRecPos(recPos);
		JSONObject data = productSearchService.searchProductListByBigData(searchReq);
		if(null==data){
			//找不到商品，返回一个默认的
			logger.info("can not find any rec products by: yhchannel:{}, uid:{} udid:{}", yhchannel, uid, udid);
			return new ApiResponse.ApiResponseBuilder().code(200).message("Product_list").data(this.defaultRecDatas(recId.toString())).build();
		}
		JSONArray jsonArray = data.getJSONArray("product_list");
		if(jsonArray == null || jsonArray.size() == 0){
            //找不到商品，返回一个默认的
			logger.info("can not find any rec products by: yhchannel:{}, uid:{} udid:{}", yhchannel, uid, udid);
			return new ApiResponse.ApiResponseBuilder().code(200).message("Product_list").data(this.defaultRecDatas(recId.toString())).build();
		}

		if (jsonArray.size() > limit) { // 最多返回30个
			jsonArray = new JSONArray(ListUtils.getSubList(jsonArray, 0, limit));
		}

		data.put("product_list", jsonArray);
		data.remove("page");
		data.remove("page_total");
		data.remove("total");

		return new ApiResponse.ApiResponseBuilder().code(200).message("Product List.").data(data).build();
	}
	
	/**
	 * 获取推荐位
	 *
	 * @param channel
	 * @return
	 */
	private String getRecPos(String channel) {
		String recPos = "100004";
		if(StringUtils.isEmpty(channel)){
			return recPos;
		}
		if ("1".equals(channel)) {
			recPos = "100001";
		} else if ("2".equals(channel)) {
			recPos = "100002";
		}
		return recPos;
	}

	/**
	 * 为你优选所有商品
	 *
	 * @param yourPreferJsonArray
	 * @return
	 */
	private List<YourPreferProduct> buildYourPreferPrds(JSONArray yourPreferJsonArray) {
		List<YourPreferProduct> yourPreferProducts = Lists.newArrayList();
		if (yourPreferJsonArray != null) {
			int size = yourPreferJsonArray.size();
			for (int i = 0; i < size; i++) {
				JSONObject product = yourPreferJsonArray.getJSONObject(i);
				YourPreferProduct yourPreferProduct = ProductUtils.formatProduct(product, 299, 388);
				yourPreferProducts.add(yourPreferProduct);
			}
		}
		return yourPreferProducts;
	}
	/**
	 * error model
	 * @return
	 */
	private ModelMap getErrorModelMap() {
		ModelMap model = new ModelMap();
		model.addAttribute("code", "404");
		model.addAttribute("message", "no data");
		return model;
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
