package com.yoho.gateway.service.favorite.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.product.FavoriteController;
import com.yoho.gateway.model.product.ProductFavoriteSortVo;
import com.yoho.gateway.model.product.ProductFavoriteVo;
import com.yoho.gateway.model.product.PromotionVo;
import com.yoho.gateway.model.product.ShowProductFavoriteVo;
import com.yoho.gateway.model.product.WebProductFavoriteVo;
import com.yoho.gateway.service.favorite.FavoriteService;
import com.yoho.gateway.service.search.ShopService;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.CategoryBo;
import com.yoho.product.model.GoodsBo;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductPriceBo;
import com.yoho.product.model.ProductPromotionRequest;
import com.yoho.product.model.PromotionBo;
import com.yoho.product.model.PromotionWrapper;
import com.yoho.product.model.ShopsBo;
import com.yoho.product.model.ShopsBrandsBo;
import com.yoho.product.model.favorite.FavoriteBo;
import com.yoho.product.model.favorite.FavoriteProductBo;
import com.yoho.product.request.BatchBaseRequest;
import com.yoho.product.request.FavoriteReqBo;
import com.yoho.product.response.ProductFavoriteRspBo;
import com.yoho.service.model.request.PriceReductionRequest;
import com.yoho.service.model.response.BrandFavoriteRespBO;

/**
 * 收藏接口
 * @author mali
 *
 */
@Service
public class FavoriteServiceImpl implements FavoriteService {
	
	private static Logger LOGGER = LoggerFactory.getLogger(FavoriteController.class);
	
	private static final String NODE_NAME_PRODUCT_ID = "productId";

	private static final String NODE_NAME_PRICEDOWN = "priceDown";
	
	// 商品分类服务
	private static final String PRODUCT_CATEGORY_SERVICE = "product.querymiddleCategoryList";
	// 商品分类服务，根据指定id查询
	private static final String PRODUCT_CATEGORYALL_SERVICE = "product.queryCategoryByIds";
	
	private static final String SITE_URL = "http://www.yohobuy.com/";

	@Resource
	private ServiceCaller serviceCaller;
	
	@Autowired
    private CacheClient cacheClient;
	
	@Autowired
	private ShopService shopService;
	
	/**
	 * 查询收藏的商品
	 * @param favoriteReqBO
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryFavoriteProductList(FavoriteReqBo favoriteReqBO) {
		int uid = favoriteReqBO.getUid();
		if (uid <= 0) {
			LOGGER.warn("get product error because uid is null with param is {}", favoriteReqBO);
			throw new ServiceException(ServiceError.UID_MUST_NOT_NULL);
		}
		
		ProductFavoriteRspBo<JSONObject> result = serviceCaller.call("product.getFavoriteList", favoriteReqBO, ProductFavoriteRspBo.class);
		
		List<ProductFavoriteVo> responseObjList = new ArrayList<ProductFavoriteVo>();
		
		List<Integer> productIds = null;		
		try {
			productIds = getProductIdList(result.getList(), responseObjList);  // 获取收藏商品Id的列表    且将productId  price_down 属性塞到返回的收藏商品信息的属性中
		} catch (Exception e) {
			LOGGER.warn("getProductIdList find wrong. result = " + result, e);
		}
		List<ProductFavoriteSortVo> productSortInfoList = null;
		if (CollectionUtils.isNotEmpty(productIds)) {
			//调用批量接口，查询，商品，价格库存等信息
			FavoriteProductBo[] productList = searchProductsInfoByIds(productIds);
			
			//设置值
			Set<Integer> sortSet = setData(responseObjList, productList);       // 收藏商品的所属二级分类列表
			
			if (CollectionUtils.isNotEmpty(sortSet)) {
				productSortInfoList = getProductSortInfoList(sortSet);
			}
		}
		
		// 组装返回信息
		return buildVoResult(result, responseObjList, favoriteReqBO, productSortInfoList);
	}
	
	/**
	 * 查询收藏的商品（web端使用）
	 * @param favoriteReqBO
	 * @return
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, Object> queryWebFavoriteProductList(FavoriteReqBo favoriteReqBO) {
		int uid = favoriteReqBO.getUid();
		if (uid <= 0) {
			LOGGER.warn("get product error because uid is null with param is {}", favoriteReqBO);
			throw new ServiceException(ServiceError.UID_MUST_NOT_NULL);
		}
		
		ProductFavoriteRspBo<JSONObject> result = serviceCaller.call("product.getFavoriteList", favoriteReqBO, ProductFavoriteRspBo.class);
		
		List<ProductFavoriteVo> responseObjList = new ArrayList<ProductFavoriteVo>();
		List<WebProductFavoriteVo> rspObjList = new ArrayList<WebProductFavoriteVo>();
		
		List<Integer> productIds = null;		
		try {
			productIds = getProductIdList(result.getList(), responseObjList);  // 获取收藏商品Id的列表    且将productId  price_down 属性塞到返回的收藏商品信息的属性中
		} catch (Exception e) {
			LOGGER.warn("getProductIdList find wrong. result = " + result, e);
		}
		List<ProductFavoriteSortVo> productSortInfoList = null;
		if (CollectionUtils.isNotEmpty(productIds)) {
			//调用批量接口，查询，商品，价格库存等信息
			FavoriteProductBo[] productList = searchWebProductsInfoByIds(productIds);
			// 搜索结果顺序不正确，按照productId入参顺序排序
			productList = sortByProductId(productIds, productList);
			
			//查询用户收藏时的商品价格
			FavoriteBo[] favBoArray = serviceCaller.call("product.getFavoriteListByUid", favoriteReqBO, FavoriteBo[].class);
			Map<Integer, String> favPriceMap = convertToFavMap(favBoArray);
			
			PriceReductionRequest userReq = new PriceReductionRequest();
			userReq.setUid(uid);
			Integer[] reduceProductIdArr = serviceCaller.call("users.getProductIdListByUid", userReq, Integer[].class);
			List<Integer> reduceProductIdList = Arrays.asList(reduceProductIdArr);
			
			//设置值
			Map<Integer, Integer> sortMap = setWebData(productList, rspObjList, favPriceMap, reduceProductIdList);       // 收藏商品的所属二级分类列表
			
			if (MapUtils.isNotEmpty(sortMap)) {
				productSortInfoList = getWebProductSortInfoList(sortMap);
			}
		}
		
		// 组装返回信息
		return buildWebVoResult(result, rspObjList, favoriteReqBO, productSortInfoList);
	}
	
	private FavoriteProductBo[] sortByProductId(List<Integer> productIds,
			FavoriteProductBo[] productList) {
		List<FavoriteProductBo> newProductList = new ArrayList<FavoriteProductBo>();
		for (Integer productId : productIds) {
			for (FavoriteProductBo favoriteProductBo : productList) {
				if(productId.equals(favoriteProductBo.getProductBo().getId())){
					newProductList.add(favoriteProductBo);
					break;
				}
			}
		}
		
		FavoriteProductBo[] newProductArray = new FavoriteProductBo[newProductList.size()];
		for (int i = 0; i < newProductList.size(); i++) {
			newProductArray[i] = newProductList.get(i);
		}
		return newProductArray;
	}

	private Map<Integer, String> convertToFavMap(FavoriteBo[] favBoArray){
		Map<Integer, String> map = new HashMap<Integer, String>();
		for(FavoriteBo favBo : favBoArray){
			map.put(favBo.getProductId(), String.valueOf(favBo.getOldPrice()));
		}
		
		return map;
	}
	
	private Map<Integer, FavoriteBo> convertToFavoriteBoMap(FavoriteBo[] favBoArray){
		Map<Integer, FavoriteBo> map = new HashMap<Integer, FavoriteBo>();
		for(FavoriteBo favBo : favBoArray){
			map.put(favBo.getProductId(), favBo);
		}
		
		return map;
	}

	private List<ProductFavoriteSortVo> getProductSortInfoList(Set<Integer> sortSet) {
		Map<Integer, CategoryBo> allMidderSortInfo = queryAllMidderSortInfo();
		List<ProductFavoriteSortVo> result = new ArrayList<ProductFavoriteSortVo>(sortSet.size());			// 通过Id查询分类信息
		for (Integer item : sortSet) {
			CategoryBo categoryBo = allMidderSortInfo.get(item);
			if (null != categoryBo) {
				ProductFavoriteSortVo vo = new ProductFavoriteSortVo();
				vo.setCategory_id(categoryBo.getCategoryId());
				vo.setCategory_name(categoryBo.getCategoryName());
				result.add(vo);
			}
		}
		return result;
	}
	
	private List<ProductFavoriteSortVo> getWebProductSortInfoList(Map<Integer, Integer> sortMap) {
		Map<Integer, CategoryBo> allMidderSortInfo = queryAllMidderSortInfo();
		List<ProductFavoriteSortVo> result = new ArrayList<ProductFavoriteSortVo>(sortMap.size());			// 通过Id查询分类信息
		for (Integer key : sortMap.keySet()){
			CategoryBo categoryBo = allMidderSortInfo.get(key);
			if (null != categoryBo) {
				ProductFavoriteSortVo vo = new ProductFavoriteSortVo();
				vo.setCategory_id(categoryBo.getCategoryId());
				vo.setCategory_name(categoryBo.getCategoryName());
				vo.setNum(sortMap.get(key));
				result.add(vo);
			}
		}
		return result;
	}

	private Map<Integer, CategoryBo> queryAllMidderSortInfo() {
		
		final String key="yh:gw:allMidderSortInfo";
		
		CategoryBo[] categoryBoList=cacheClient.get(key, CategoryBo[].class);
		
		if(null==categoryBoList )
		{
			categoryBoList = serviceCaller.asyncCall(PRODUCT_CATEGORY_SERVICE, "", CategoryBo[].class).get();
			
			if(null!=categoryBoList && categoryBoList.length>0)
			{	
				//缓存十分钟
				cacheClient.set(key, 600, categoryBoList);
			}
		}
		Map<Integer, CategoryBo> categoryBoMap = new HashMap<Integer, CategoryBo>();
		if (null != categoryBoList && categoryBoList.length>0) 
		{
			for (CategoryBo categoryBo : categoryBoList) {
				if (null != categoryBo) {
					categoryBoMap.put(categoryBo.getCategoryId(), categoryBo);
				}
			}
		}
		return categoryBoMap;
	}
	
	private Map<Integer, CategoryBo> queryAllSortInfo(Set<Integer> sortSet) {
		BatchBaseRequest<Integer> req = new BatchBaseRequest<Integer>();
		req.setParams(Lists.newArrayList(sortSet));
		AsyncFuture<CategoryBo[]> categoryBoAsync = serviceCaller.asyncCall(PRODUCT_CATEGORYALL_SERVICE, req, CategoryBo[].class);		// 查询所有的二级商品分类    键值为sortId
		Map<Integer, CategoryBo> categoryBoMap = new HashMap<Integer, CategoryBo>();
		if (null != categoryBoAsync) {
			CategoryBo[] categoryBoArr = categoryBoAsync.get();
			if (null != categoryBoArr && 0 < categoryBoArr.length) {
				for (CategoryBo categoryBo : categoryBoArr) {
					if (null != categoryBo) {
						categoryBoMap.put(categoryBo.getCategoryId(), categoryBo);
					}
				}
			}
		}
		return categoryBoMap;
	}
	/**
	 * 设置值
	 * @param responseObjList
	 * @param productList
	 * @return 返回商品所属分类的Id列表
	 */
	private Set<Integer> setData(List<ProductFavoriteVo> responseObjList, FavoriteProductBo[] productList) {
		Set<Integer> sortSet = Sets.newHashSet();
		if (productList == null || productList.length == 0) {
			return sortSet;
		}
		ProductBo productBo = null;
		GoodsBo goodsBo = null;
		ProductPriceBo productPriceBo = null;
		int storageNum = 0;
		for (FavoriteProductBo product : productList) {
			productBo = product.getProductBo();
			goodsBo = product.getGoodsBo();
			if (goodsBo == null || StringUtils.isEmpty(goodsBo.getColorImage())) {
				continue;
			}
			productPriceBo = product.getProductPriceBo();
			storageNum = product.getStorageNum() == null ? 0 : product.getStorageNum();
			for (ProductFavoriteVo target : responseObjList) {
				if (target.getProduct_id() == productBo.getId()) {
					if (storageNum <= 0) {
						target.setPrice_down(0);
					}
					target.setProduct_name(productBo.getProductName());
					target.setAttribute(productBo.getAttribute() == null ? 0 : productBo.getAttribute());
					target.setProduct_skn(productBo.getErpProductId() == null ? 0 : productBo.getErpProductId());
					target.setSales_price(productPriceBo.getFormatSalesPrice());
					target.setMarket_price(productPriceBo.getFormatMarketPrice());
					target.setStatus(productBo.getStatus() == null ? 0 : productBo.getStatus());
					target.setStorage(storageNum);
					target.setImage(StringUtils.isEmpty(goodsBo.getColorImage()) ? "" : ImagesHelper.getImageUrl(goodsBo.getColorImage(), 160, 200, 1, "goodsimg"));
					target.setGoodsId(goodsBo.getId() == null ? 0 : goodsBo.getId());
					target.setCnAlphabet(productBo.getCnAlphabet());
					target.setCategory_id(productBo.getMaxSortId());
					
					sortSet.add(productBo.getMaxSortId());
					break;
				}
			}
		}
		
		return sortSet;
	}
	
	private Map<Integer, Integer> setWebData(FavoriteProductBo[] productList, 
			List<WebProductFavoriteVo> webRspObjList, Map<Integer, String> favMap, List<Integer> reduceProductIdList) {
		Map<Integer, Integer> sortMap = Maps.newHashMap();
		if (productList == null || productList.length == 0) {
			return sortMap;
		}
		ProductBo productBo = null;
		GoodsBo goodsBo = null;
		ProductPriceBo productPriceBo = null;
		//获取促销信息
		Map<Integer, List<PromotionVo>> promotionMap = getPromotionInfo(productList);
		
		for (FavoriteProductBo product : productList) {
			productBo = product.getProductBo();
			goodsBo = product.getGoodsBo();
			if (goodsBo == null || StringUtils.isEmpty(goodsBo.getColorImage())) {
				continue;
			}
			productPriceBo = product.getProductPriceBo();
			WebProductFavoriteVo webRspVo = new WebProductFavoriteVo();
			List<PromotionVo> promotionVoList = new ArrayList<PromotionVo>();
			if(promotionMap.keySet().contains(productBo.getErpProductId())){
				promotionVoList = promotionMap.get(productBo.getErpProductId());
			}
					
			webRspVo.setProduct_id(productBo.getId());
			webRspVo.setProduct_name(productBo.getProductName());
			webRspVo.setAttribute(productBo.getAttribute() == null ? 0 : productBo.getAttribute());
			webRspVo.setProduct_skn(productBo.getErpProductId() == null ? 0 : productBo.getErpProductId());
			if(StringUtils.isNotEmpty(productPriceBo.getFormatSalesPrice())){
				webRspVo.setSales_price(String.format("%.2f", Double.valueOf(productPriceBo.getFormatSalesPrice())));
			}
			if(StringUtils.isNotEmpty(productPriceBo.getFormatMarketPrice())){
				webRspVo.setMarket_price(String.format("%.2f", Double.valueOf(productPriceBo.getFormatMarketPrice())));
			}
			
			webRspVo.setStatus(productBo.getStatus() == null ? 0 : productBo.getStatus());
			webRspVo.setStorage(product.getStorageNum() == null ? 0 : product.getStorageNum());
			webRspVo.setImage(StringUtils.isEmpty(goodsBo.getColorImage()) ? "" : ImagesHelper.getImageUrl(goodsBo.getColorImage(), 160, 200, 1, "goodsimg"));
			webRspVo.setGoodsId(goodsBo.getId() == null ? 0 : goodsBo.getId());
			webRspVo.setCnAlphabet(productBo.getCnAlphabet());
			webRspVo.setCategory_id(productBo.getMaxSortId());
			webRspVo.setPromotionList(promotionVoList);
			webRspVo.setFavPrice(favMap.get(productBo.getId().intValue()));
			//是否订阅降价提醒
			String isSubscribeFlag = CollectionUtils.isEmpty(reduceProductIdList) ? "N" : reduceProductIdList.contains(productBo.getId()) ? "Y" : "N";
			webRspVo.setIsSubscribeReduction(isSubscribeFlag);
			webRspVo.setProductUrl(SITE_URL + "product/pro_" + productBo.getId() + "_" + goodsBo.getId() + "/" + productBo.getCnAlphabet() + ".html");
			//TODO  为啥会空指针
			if(StringUtils.isBlank(webRspVo.getSales_price())||StringUtils.isBlank(webRspVo.getFavPrice()))
			{
				webRspVo.setIsPriceDown("N");
			}else
			{
				webRspVo.setIsPriceDown(Float.parseFloat(webRspVo.getSales_price()) < Float.parseFloat(webRspVo.getFavPrice()) ? "Y" : "N");
			}
			webRspVo.setIsJoinPromotion(CollectionUtils.isEmpty(promotionVoList) ? "N" : "Y");
					
			webRspObjList.add(webRspVo);
					
			Integer productNum = sortMap.get(productBo.getMaxSortId());
			if(null != productNum){
				sortMap.put(productBo.getMaxSortId(), productNum + 1);
			}else{
				sortMap.put(productBo.getMaxSortId(), 1);
			}
		}	
		return sortMap;
	}
	
	/**
	 * 设置值
	 * @param responseObjList
	 * @param productList
	 * @return 返回商品所属分类的Id列表
	 */
	private Set<Integer> setShowData(List<ShowProductFavoriteVo> responseObjList, FavoriteProductBo[] productList, Map<Integer, FavoriteBo> favoriteBoMap) {
		Set<Integer> sortSet = Sets.newHashSet();
		if (productList == null || productList.length == 0) {
			return sortSet;
		}
		ProductBo productBo = null;
		GoodsBo goodsBo = null;
		ProductPriceBo productPriceBo = null;
		BrandBo brandBo = null;
		for (FavoriteProductBo product : productList) {
			productBo = product.getProductBo();
			goodsBo = product.getGoodsBo();
			brandBo = productBo.getBrand();
			if (goodsBo == null || StringUtils.isEmpty(goodsBo.getColorImage())) {
				continue;
			}
			productPriceBo = product.getProductPriceBo();
			
			FavoriteBo favoriteBo = favoriteBoMap.get(productBo.getId());
			ShowProductFavoriteVo showRspVo = new ShowProductFavoriteVo();
					
			showRspVo.setProduct_id(String.valueOf(productBo.getId()));
			showRspVo.setProduct_name(productBo.getProductName());
			showRspVo.setSkn(String.valueOf(productBo.getErpProductId()));
			showRspVo.setStatus(productBo.getStatus() == null ? "0" : String.valueOf(productBo.getStatus()));
			
			showRspVo.setBrandId(productBo.getBrandId() == null ? "" : productBo.getBrandId()+"");
			showRspVo.setBrandName(brandBo == null ? "" : brandBo.getBrandName());
			showRspVo.setProductUrl(SITE_URL + "product/pro_" + productBo.getId() + "_" + goodsBo.getId() + "/" + productBo.getCnAlphabet() + ".html");
			
			if(favoriteBo!=null){
				showRspVo.setFavorId(String.valueOf(favoriteBo.getId()));
				showRspVo.setUid(String.valueOf(favoriteBo.getUid()));
				showRspVo.setCreateTime(DateUtil.getDateStrBySecond(favoriteBo.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
			}
			if(productBo.getMaxSortId()!=null){
				showRspVo.setMaxSortId(String.valueOf(productBo.getMaxSortId()));
				sortSet.add(productBo.getMaxSortId());
			}
			
			if(productBo.getMiddleSortId()!=null){
				showRspVo.setMiddleSortId(String.valueOf(productBo.getMiddleSortId()));
				sortSet.add(productBo.getMiddleSortId());
			}
			if(productBo.getSmallSortId()!=null){
				showRspVo.setSmallSortId(String.valueOf(productBo.getSmallSortId()));
				sortSet.add(productBo.getSmallSortId());
			}
			
			
			Map<String, String> imageMap = new HashMap<String, String>();
			imageMap.put("100x100", ImagesHelper.getImageUrl(goodsBo.getColorImage(), 100, 100, 1, "goodsimg"));
			imageMap.put("220x220", ImagesHelper.getImageUrl(goodsBo.getColorImage(), 220, 220, 1, "goodsimg"));
			imageMap.put("330x330", ImagesHelper.getImageUrl(goodsBo.getColorImage(), 330, 330, 1, "goodsimg"));
			imageMap.put("600x1000", ImagesHelper.getImageUrl(goodsBo.getColorImage(), 600, 1000, 1, "goodsimg"));
			showRspVo.setImageMap(imageMap);
			
			showRspVo.setProductThumb(ImagesHelper.getImageUrl(goodsBo.getColorImage(), 160, 200, 1, "goodsimg"));
			Map<String, String> priceMap = new HashMap<String, String>();
			priceMap.put("market_price", productPriceBo.getFormatMarketPrice()+"0");
			priceMap.put("sales_price", productPriceBo.getFormatSalesPrice()+"0");
			showRspVo.setPriceMap(priceMap);
			
			responseObjList.add(showRspVo);
		}	
		
		return sortSet;
	}
	
	private void setShowFavProductSortInfo(List<ShowProductFavoriteVo> responseObjList, Set<Integer> sortSet) {
		Map<Integer, CategoryBo> allSortInfo = queryAllSortInfo(sortSet);
		CategoryBo categoryBo = null;
		for (ShowProductFavoriteVo item : responseObjList) {
			if(StringUtils.isNotBlank(item.getMaxSortId())){
				categoryBo = allSortInfo.get(Integer.valueOf(item.getMaxSortId()));
				item.setMaxSortId(categoryBo.getCategoryName());
			}
			if(StringUtils.isNotBlank(item.getMiddleSortId())){
				categoryBo = allSortInfo.get(Integer.valueOf(item.getMiddleSortId()));
				item.setMiddleSortId(categoryBo.getCategoryName());
			}
			if(StringUtils.isNotBlank(item.getSmallSortId())){
				categoryBo = allSortInfo.get(Integer.valueOf(item.getSmallSortId()));
				item.setSmallSortId(categoryBo.getCategoryName());
			}
		}
	}
	
	private Map<Integer, List<PromotionVo>> getPromotionInfo(FavoriteProductBo[] productList){
		ProductPromotionRequest promotionRequest = new ProductPromotionRequest();
		List<ProductBo> productBoList = new ArrayList<ProductBo>();
		for(FavoriteProductBo product : productList){
			productBoList.add(product.getProductBo());
		}
		promotionRequest.setProductBoList(productBoList);
		PromotionWrapper[] promotionArray = serviceCaller.call("product.queryProductFitPromotionList", promotionRequest, PromotionWrapper[].class);
		
		Map<Integer, List<PromotionVo>> map = new HashMap<Integer, List<PromotionVo>>();
		for(PromotionWrapper wrapper : promotionArray){
			map.put(wrapper.getErpProductId(), convertBoToVo(wrapper.getPromotionBoList()));
		}
	
	    return map;
	}
	
	private List<PromotionVo> convertBoToVo(List<PromotionBo> boList){
		List<PromotionVo> promotionVoList = Lists.newArrayList();
		if(null != boList && boList.size() > 0){
			for(PromotionBo promotionBo : boList){
				PromotionVo promotionVo = new PromotionVo();
				promotionVo.setPromotionTitle(promotionBo.getPromotionTitle());
				promotionVo.setPromotionType(promotionBo.getPromotionType());
				promotionVoList.add(promotionVo);
			}
		}
		
		return promotionVoList;
	}
	
	private FavoriteProductBo[] searchProductsInfoByIds(List<Integer> productIds) {
		BatchBaseRequest<Integer> bathParam = new BatchBaseRequest<Integer>();
		bathParam.setParams(productIds);
		return serviceCaller.call("product.serchFavoriteProductrByProductIds", bathParam, FavoriteProductBo[].class);
	}
	
	private FavoriteProductBo[] searchWebProductsInfoByIds(List<Integer> productIds) {
		BatchBaseRequest<Integer> bathParam = new BatchBaseRequest<Integer>();
		bathParam.setParams(productIds);
		return serviceCaller.call("product.searchWebFavoriteByProductIds", bathParam, FavoriteProductBo[].class);
	}

	private Map<String, Object> buildVoResult(ProductFavoriteRspBo<JSONObject> result, List<ProductFavoriteVo> voList, 
			FavoriteReqBo favoriteReqBO, List<ProductFavoriteSortVo> productSortInfoList) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("product_list", voList);
		if (null != result) {
			map.put("page_total", result.getPage_total());
			map.put("page", result.getPage());
			map.put("total", result.getTotal());
			map.put("category_list", null == productSortInfoList ? new ArrayList<ProductFavoriteSortVo>(0) : productSortInfoList);
		} else {
			map.put("page_total", 0);
			map.put("page", favoriteReqBO.getPage());
			map.put("total", 0);
		}
		return map;
	}
	
	private Map<String, Object> buildWebVoResult(ProductFavoriteRspBo<JSONObject> result, List<WebProductFavoriteVo> voList, 
			FavoriteReqBo favoriteReqBO, List<ProductFavoriteSortVo> productSortInfoList) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("product_list", voList);
		map.put("total", null == voList ? 0 : voList.size());
		if (null != result) {
			map.put("category_list", null == productSortInfoList ? new ArrayList<ProductFavoriteSortVo>(0) : productSortInfoList);
		} 
		return map;
	}
	
	/**
	 * 获取收藏商品Id的列表    且将productId  price_down 属性塞到返回的收藏商品信息的属性中
	 * @param productFavoriteList
	 * @param responseObjList
	 * @return
	 */
	private List<Integer> getProductIdList(List<JSONObject> productFavoriteList, List<ProductFavoriteVo> responseObjList) {
		if (CollectionUtils.isEmpty(productFavoriteList)) {
			return new ArrayList<Integer>(0);
		}
		ProductFavoriteVo resp = null;
		List<Integer> productIds = new ArrayList<Integer>();		// 组装product_id
		for (JSONObject item : productFavoriteList) {
			if (item == null) {
				continue;
			}
			Integer productId = item.getInteger(NODE_NAME_PRODUCT_ID);
			if (null == productId || 0 == productId || productIds.contains(productId)) {				// 商品编码重复
				continue;
			}
			productIds.add(productId);
			resp = new ProductFavoriteVo();
			resp.setProduct_id(productId);
			resp.setPrice_down(item.getInteger(NODE_NAME_PRICEDOWN));
			responseObjList.add(resp);
		}
		return productIds;
	}
	
	/**
	 * 获取收藏商品Id的列表
	 * @param productFavoriteList
	 * @param responseObjList
	 * @return
	 */
	private List<Integer> getProductIdListForShow(List<JSONObject> productFavoriteList) {
		if (CollectionUtils.isEmpty(productFavoriteList)) {
			return new ArrayList<Integer>(0);
		}
		List<Integer> productIds = new ArrayList<Integer>();		// 组装product_id
		for (JSONObject item : productFavoriteList) {
			if (item == null) {
				continue;
			}
			Integer productId = item.getInteger(NODE_NAME_PRODUCT_ID);
			if (null == productId || 0 == productId || productIds.contains(productId)) {				// 商品编码重复
				continue;
			}
			productIds.add(productId);
		}
		return productIds;
	}
	

	@Override
	@SuppressWarnings("unchecked")
	public List<ShowProductFavoriteVo> queryShowFavoriteProductList(FavoriteReqBo favoriteReqBO) {
		int uid = favoriteReqBO.getUid();
		if (uid <= 0) {
			LOGGER.warn("get product error because uid is null with param is {}", favoriteReqBO);
			throw new ServiceException(ServiceError.UID_MUST_NOT_NULL);
		}
		
		ProductFavoriteRspBo<JSONObject> result = serviceCaller.call("product.getFavoriteList", favoriteReqBO, ProductFavoriteRspBo.class);
		List<ShowProductFavoriteVo> rspObjList = new ArrayList<ShowProductFavoriteVo>();
		
		List<Integer> productIds = null;		
		try {
			productIds = getProductIdListForShow(result.getList()); 
		} catch (Exception e) {
			LOGGER.warn("getProductIdList find wrong. result = " + result, e);
		}
		if (CollectionUtils.isNotEmpty(productIds)) {
			//调用批量接口，查询，商品，价格库存等信息
			FavoriteProductBo[] productList = searchProductsInfoByIds(productIds);
			//查询用户收藏时的商品价格
			FavoriteBo[] favBoArray = serviceCaller.call("product.getFavoriteListByUid", favoriteReqBO, FavoriteBo[].class);
			Map<Integer, FavoriteBo> favoriteBoMap = convertToFavoriteBoMap(favBoArray);
			//设置值
			Set<Integer> sortSet = setShowData(rspObjList, productList, favoriteBoMap);
			if (CollectionUtils.isNotEmpty(sortSet)) {
				setShowFavProductSortInfo(rspObjList, sortSet);
			}
		}
		
		return rspObjList;
	}

	/**
	 * 跳转规则约定：
	 * 无店铺：0--->品牌页 
     * 无单品店有多品店：1--->搜索页 
     * 有单品店：2--->店铺页面
	 */
	@Override
	public void getBrandOrShopForwardType(List<JSONObject> list) {
		if(CollectionUtils.isEmpty(list)){
			return;
		}
		List<Integer> shopIds = new ArrayList<Integer>();
		for (JSONObject brand : list) {
			if(StringUtils.isNotEmpty(brand.getString("brandOrShopType")) && "brand".equals(brand.getString("brandOrShopType"))){
				brand.put("type","0");
	    		continue;
			}
			if(StringUtils.isNotEmpty(brand.getString("brandOrShopType")) && "shop".equals(brand.getString("brandOrShopType"))){
				brand.put("type","2");
				if(StringUtils.isNotEmpty(brand.getString("shop_id"))){
					shopIds.add(Integer.valueOf(brand.getString("shop_id")));
				}
	    		continue;
			}
			// 默认品牌页
			brand.put("type","0");
		}
		
		List<ShopsBo> shopsBoList = shopService.getShopBoList(shopIds);
		if(CollectionUtils.isEmpty(shopsBoList)){
			return;
		}
		// 品牌列表如果是店铺，构造店铺的装修类型
		for (JSONObject brand : list) {
			for (ShopsBo shopsBo : shopsBoList) {	
				if(StringUtils.isNotEmpty(brand.getString("brandOrShopType")) 
						&& "shop".equals(brand.getString("brandOrShopType"))
						&& StringUtils.isNotEmpty(brand.getString("shop_id"))
						&& shopsBo.getShopsId().equals(Integer.valueOf(brand.getString("shop_id")))){
					brand.put("shop_template_type",shopsBo.getShopTemplateType());
				}
			}
		}
	}
}
