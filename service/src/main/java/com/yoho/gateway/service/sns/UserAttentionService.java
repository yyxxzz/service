package com.yoho.gateway.service.sns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.search.NewProductSearchController;
import com.yoho.gateway.service.search.ProductSearchService;
import com.yoho.product.model.GoodsBo;
import com.yoho.product.model.search.ProductSearchBo;
import com.yoho.product.request.BrandProductsReqBO;
import com.yoho.product.response.BrandProductsRspBO;
import com.yoho.service.model.sns.request.AttentionReqBO;
import com.yoho.service.model.sns.response.AttentionBrandRspBO;
import com.yoho.service.model.sns.response.AttentionRspBO;

@Service
public class UserAttentionService {

	@Autowired
	private ServiceCaller serviceCaller;
	@Autowired
	private ProductSearchService productSearchService;
	@Autowired
	private NewProductSearchController newProductSearchController;
	@Autowired
	private IMakeUrlService makeUrlService;

	private static final String BRAND_TYPE_NEW = "new";
	private static final String BRAND_TYPE_SALE = "sale";
	private static final String BRAND_TYPE_RECOMMEND = "recommend";
	private static final String BRAND_TYPE_ACTIVITY = "activity";

	private static final String PRODUCT_URL_FORMATTER = "http://item.m.yohobuy.com/product/pro_%s_%s/%s.html";

	private static final int BATCH_SELECT_COUNT = 3;

	private final Logger logger = LoggerFactory.getLogger(UserAttentionService.class);

	private String ATTENTION_URL = "sns.getAttentionList";
	private String BRANDPRODUCTS_URL = "product.queryBrandProducts";

	public JSONObject getAttentionList(int page, String gender, String clientType, Integer uid) {
		// 0)参数处理
		if (StringUtils.isEmpty(gender)) {
			gender = "1,3";
		}
		if (StringUtils.isBlank(clientType)) {
			clientType = "h5";
		}

		// 1）构造请求对象
		AttentionReqBO attentionReqBO = new AttentionReqBO();
		attentionReqBO.setUid(uid);
		attentionReqBO.setGender(gender);
		attentionReqBO.setClientType(clientType);
		attentionReqBO.setPage(page);
		attentionReqBO.setLimit(10);

		// 2）调用逛的服务请求部分数据
		JSONObject resultJSONObject = new JSONObject();
		AttentionRspBO attentionRspBO = serviceCaller.call(ATTENTION_URL, attentionReqBO, AttentionRspBO.class);
		resultJSONObject.put("page", attentionRspBO.getPage());
		resultJSONObject.put("total", attentionRspBO.getTotal());
		resultJSONObject.put("total_page", attentionRspBO.getTotal_page());

		// 3）查询品牌相关的商品
		List<AttentionBrandRspBO> brandRspBOList = attentionRspBO.getList();
		List<Integer> new_brandIds = new ArrayList<Integer>();
		List<Integer> sale_brandIds = new ArrayList<Integer>();
		List<Integer> recommend_brandIds = new ArrayList<Integer>();
		for (AttentionBrandRspBO attentionBrandRspBO : brandRspBOList) {
			Integer brandId = Integer.valueOf(attentionBrandRspBO.getBrand_id());
			String brandType = attentionBrandRspBO.getBrand_type();
			if (brandType.equals(BRAND_TYPE_NEW)) {
				new_brandIds.add(brandId);
			} else if (brandType.equals(BRAND_TYPE_SALE)) {
				sale_brandIds.add(brandId);
			} else if (brandType.equals(BRAND_TYPE_RECOMMEND)) {
				recommend_brandIds.add(brandId);
			}
		}
		Map<String, BrandProductsRspBO> newBrandProductsMap = this.getNewProductsByBrandIds(new_brandIds, gender, BATCH_SELECT_COUNT);
		Map<String, BrandProductsRspBO> saleBrandProductsMap = this.getSaleProductsByBrandIds(sale_brandIds, gender, BATCH_SELECT_COUNT);
		Map<String, BrandProductsRspBO> recommendBrandProductsMap = this.getRecommendProductsByBrandIds(recommend_brandIds, gender, BATCH_SELECT_COUNT);

		// 4)处理返回结果
		JSONArray brandWithProductList = new JSONArray();
		for (AttentionBrandRspBO attentionBrandRspBO : brandRspBOList) {
			String brandId = attentionBrandRspBO.getBrand_id();
			JSONObject brandWithProduct = new JSONObject();
			brandWithProduct.put("brand_id", brandId);
			brandWithProduct.put("brand_name", attentionBrandRspBO.getBrand_name());
			brandWithProduct.put("brand_img", attentionBrandRspBO.getBrand_img());
			brandWithProduct.put("date", attentionBrandRspBO.getDate());
			brandWithProduct.put("url", attentionBrandRspBO.getUrl());
			brandWithProduct.put("is_fav", attentionBrandRspBO.getIs_fav());
			brandWithProduct.put("brand_type", attentionBrandRspBO.getBrand_type());

			// 处理product/img部分
			String brand_type = attentionBrandRspBO.getBrand_type();
			BrandProductsRspBO brandProductsRspBO = null;
			switch (brand_type) {
			case BRAND_TYPE_NEW:
				brandProductsRspBO = newBrandProductsMap.get(brandId);
				brandWithProduct.put("new_product_num", brandProductsRspBO == null ? 0 : brandProductsRspBO.getCount());
				brandWithProduct.put("product", this.getProductsResponse(brandProductsRspBO, clientType));
				break;
			case BRAND_TYPE_SALE:
				brandProductsRspBO = saleBrandProductsMap.get(brandId);
				brandWithProduct.put("product", this.getProductsResponse(brandProductsRspBO, clientType));
				brandWithProduct.put("discount", attentionBrandRspBO.getDiscount());
				break;
			case BRAND_TYPE_RECOMMEND:
				brandProductsRspBO = recommendBrandProductsMap.get(brandId);
				brandWithProduct.put("product", this.getProductsResponse(brandProductsRspBO, clientType));
				break;
			case BRAND_TYPE_ACTIVITY:
				brandWithProduct.put("img", attentionBrandRspBO.getData());
				brandWithProduct.put("activity_name", attentionBrandRspBO.getActivity_name());
				break;
			default:
				break;
			}
			brandWithProductList.add(brandWithProduct);
		}
		resultJSONObject.put("list", brandWithProductList);
		return resultJSONObject;
	}

	/**
	 * 根据品牌Id批量查询品牌相关的推荐商品
	 * 
	 * @param brandIds
	 * @param gender
	 * @param clientType
	 * @param limit
	 * @return
	 */
	private Map<String, BrandProductsRspBO> getRecommendProductsByBrandIds(List<Integer> brandIds, String gender, int limit) {
		try {
			if (brandIds == null || brandIds.isEmpty()) {
				return new HashMap<String, BrandProductsRspBO>();
			}
			BrandProductsReqBO brandProductsReqBO = new BrandProductsReqBO();
			brandProductsReqBO.setBrandIds(brandIds);
			brandProductsReqBO.setGender(gender);
			brandProductsReqBO.setOrder("s_t_desc");
			brandProductsReqBO.setPage(1);
			brandProductsReqBO.setProductLimit(limit);
			return getBrandProducts(brandProductsReqBO);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return new HashMap<String, BrandProductsRspBO>();
		}
	}

	/**
	 * 根据品牌Id批量查询品牌相关的折扣商品
	 * 
	 * @param brandIds
	 * @param gender
	 * @param clientType
	 * @return
	 */
	private Map<String, BrandProductsRspBO> getSaleProductsByBrandIds(List<Integer> brandIds, String gender, int limit) {
		try {
			if (brandIds == null || brandIds.isEmpty()) {
				return new HashMap<String, BrandProductsRspBO>();
			}
			BrandProductsReqBO brandProductsReqBO = new BrandProductsReqBO();
			brandProductsReqBO.setBrandIds(brandIds);
			brandProductsReqBO.setGender(gender);
			brandProductsReqBO.setOrder("p_d_asc");
			brandProductsReqBO.setPage(1);
			brandProductsReqBO.setProductLimit(limit);
			return getBrandProducts(brandProductsReqBO);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return new HashMap<String, BrandProductsRspBO>();
		}
	}

	/**
	 * 根据品牌Id批量查询品牌相关的最新商品
	 * 
	 * @param brandIds
	 * @param gender
	 * @param clientType
	 * @param limit
	 * @return
	 */
	private Map<String, BrandProductsRspBO> getNewProductsByBrandIds(List<Integer> brandIds, String gender, int limit) {
		try {
			if (brandIds == null || brandIds.isEmpty()) {
				return new HashMap<String, BrandProductsRspBO>();
			}
			BrandProductsReqBO brandProductsReqBO = new BrandProductsReqBO();
			brandProductsReqBO.setBrandIds(brandIds);
			brandProductsReqBO.setGender(gender);
			brandProductsReqBO.setOrder("p_d_asc");
			brandProductsReqBO.setShelveTime("");
			brandProductsReqBO.setPage(1);
			brandProductsReqBO.setProductLimit(limit);
			return getBrandProducts(brandProductsReqBO);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
			return new HashMap<String, BrandProductsRspBO>();
		}
	}

	private JSONArray getProductsResponse(BrandProductsRspBO brandProductsRspBO, String clientType) {
		if (brandProductsRspBO == null) {
			return new JSONArray();
		}
		List<ProductSearchBo> productSearchBos = brandProductsRspBO.getProducts();
		JSONArray jsonArray = new JSONArray();
		for (ProductSearchBo productSearchBo : productSearchBos) {
			List<GoodsBo> goods = productSearchBo.getGoodsList();
			if (goods == null || goods.isEmpty()) {
				continue;
			}
			String url = String.format(PRODUCT_URL_FORMATTER, productSearchBo.getProductId(), goods.get(0).getId(), productSearchBo.getCnAlphabet());
			JSONObject productJsonObject = new JSONObject();
			productJsonObject.put("product_id", Integer.valueOf(productSearchBo.getProductId()));
			productJsonObject.put("product_title", productSearchBo.getProductName());
			productJsonObject.put("product_img", productSearchBo.getDefaultImages());
			productJsonObject.put("market_price", Double.valueOf(productSearchBo.getMarketPrice()));
			productJsonObject.put("sale_price", Double.valueOf(productSearchBo.getSalesPrice()));
			JSONObject params = new JSONObject();
			params.put("product_skn", productSearchBo.getProductSkn());
			productJsonObject.put("url", makeUrlService.makeUrl("go.productDetail", url, params, clientType));

			jsonArray.add(productJsonObject);
		}

		return jsonArray;
	}

	private Map<String, BrandProductsRspBO> getBrandProducts(BrandProductsReqBO brandProductsReqBO) {
		BrandProductsRspBO[] responses = serviceCaller.call(BRANDPRODUCTS_URL, brandProductsReqBO, BrandProductsRspBO[].class);
		Map<String, BrandProductsRspBO> results = new HashMap<String, BrandProductsRspBO>();
		for (BrandProductsRspBO brandProductsRspBO : responses) {
			results.put(brandProductsRspBO.getBrandId().toString(), brandProductsRspBO);
		}
		return results;
	}

}
