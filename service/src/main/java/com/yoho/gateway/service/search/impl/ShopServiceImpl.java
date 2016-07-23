package com.yoho.gateway.service.search.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.product.ShopsVo;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.service.assist.SearchParam;
import com.yoho.gateway.service.search.ShopService;
import com.yoho.gateway.service.search.SortService;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.ShopsBo;
import com.yoho.product.model.ShopsBrandsBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.BatchBaseRequest;

/**
 * 店铺相关接口实现
 * @author wangshusheng
 *
 */
@Service
public class ShopServiceImpl implements ShopService {
	/**
	 * LOG
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(ShopServiceImpl.class);
	
	/**
	 * http请求工具
	 */
	@Autowired
    private ServiceCaller serviceCaller;
	

    @Autowired
    private SortService sortService;
    
	/**
	 * 根据品牌id查询包含该品牌的店铺列表
	 * @return
	 */
	@Override
	public List<ShopsVo> getShopListByBrandId(Integer brandId) {
		List<ShopsVo> shopsVoList = new ArrayList<ShopsVo>();
		if(brandId==null || brandId==0){
			return shopsVoList;
		}
		
		ShopsBo[] shopsBoList = null;
		BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
		//构造查询ProductBo请求
		baseRequest.setParam(brandId);
		try{	
			shopsBoList = serviceCaller.asyncCall("product.queryShopListByBrandId", baseRequest, ShopsBo[].class).get(3);
		}catch(Throwable e){	
			LOGGER.warn("queryShopListByBrandId failed brandId is:{}", brandId, e);
		}
		
		if(shopsBoList!=null && shopsBoList.length>0){
			for (ShopsBo shopsBo : shopsBoList) {
				ShopsVo shopsVo = new ShopsVo();
				shopsVo.setShopName(shopsBo.getShopName());
				shopsVo.setShopDomain(shopsBo.getShopDomain());
				shopsVo.setShopLogo(ImagesHelper.template2(shopsBo.getShopLogo(), "yhb-img01"));
				shopsVo.setShopsId(shopsBo.getShopsId());
				shopsVo.setMultBrandShopType(String.valueOf(shopsBo.getShopsType()));
				shopsVo.setShopTemplateType(shopsBo.getShopTemplateType());
				shopsVo.setShopsType("2");//店铺跳转类型默认为2
				shopsVoList.add(shopsVo);
			}
		}
		
		return shopsVoList;
	}

	@Override
	public List<BrandBo> getBrandListByShopId(Integer shopId) {
		BrandBo[] brandBoList = null;
		BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
		//构造查询ProductBo请求
		baseRequest.setParam(shopId);
		try{
			brandBoList = serviceCaller.asyncCall("product.queryShopsBrandsById", baseRequest, BrandBo[].class).get(3);
			if(brandBoList!=null && brandBoList.length>0){
				return Lists.newArrayList(brandBoList);
			}
		}catch(Throwable e){
			LOGGER.warn("product.queryShopsBrandsById failed shopId is:{}", shopId, e);
		}
		return Lists.newArrayList();
	}

	@Override
	public Object searchSortByBrandId(ProductSearchReq req) {
		SearchParam buildSearchParam = new SearchParam().setGender(StringUtils.isNotBlank(req.getGender()) ? ("1,2,3".equals(req.getGender()) ? null : req.getGender()) : null )
                .buildSearchParam(req, true).setSearchFrom(req.getSearchFrom());
		buildSearchParam.setNeedSmallSort();
		
		return sortService.getSortList(buildSearchParam.getSearchFrom(),buildSearchParam.toParamString());
	}

	@Override
	public void processShopList(JSONObject data, Integer brandId, Integer page) {
		// 判断该品牌是否是无店铺（老的品牌页）、还是在1个单品店里、还是在多个多品店里
		if(data==null || brandId==null){
			return;
		}
		if (null != page && 1 != page){
    		return;
    	}
		
		List<ShopsVo> shopsVoList = getShopListByBrandId(brandId);
    	if(CollectionUtils.isEmpty(shopsVoList)){
    		return;
    	}
    	// 判断是否存在单品店，不存在则展示多个店铺列表
    	for (ShopsVo shopsVo : shopsVoList) {
    		// 只要存在单品店铺，就跟线上一致
			if (shopsVo.getMultBrandShopType() != null && "1".equals(shopsVo.getMultBrandShopType())) {
				List<ShopsVo> shopVoListTemp = new ArrayList<ShopsVo>();
				shopVoListTemp.add(shopsVo);
				data.put(SearchConstants.NodeConstants.FILTER_KEY_SHOP, shopVoListTemp);
				return;
			}
		}
    	
    	// 如果是第一页查询，且关键词匹配得到了品牌信息，并且多个店铺中存在该品牌，只要存在单品店铺，就跟线上一致
    	data.put(SearchConstants.NodeConstants.FILTER_KEY_SHOP, shopsVoList);
	}

	@Override
	public Map<Integer, List<ShopsBrandsBo>> queryAllShopBrandList(List<Integer> brandIds) {
		ShopsBrandsBo[] shopsBrandBoList = null;
		BatchBaseRequest<Integer> baseRequest = new BatchBaseRequest<Integer>();
		//构造查询ProductBo请求
		baseRequest.setParams(brandIds);
		try{
			shopsBrandBoList = serviceCaller.asyncCall("product.batchQueryShopListByBrandIds", baseRequest, ShopsBrandsBo[].class).get(3);
			return buildShopBrandMap(shopsBrandBoList);
		}catch(Throwable e){
			LOGGER.warn("product.batchQueryShopListByBrandIds failed brandIds is:{}", brandIds, e);
			return Maps.newHashMap();
		}
	}

	private Map<Integer, List<ShopsBrandsBo>> buildShopBrandMap(ShopsBrandsBo[] shopsBrandBoList) {
		Map<Integer, List<ShopsBrandsBo>> shopBrandMap = new HashMap<Integer, List<ShopsBrandsBo>>();
		for (ShopsBrandsBo shopsBrandsBo : shopsBrandBoList) {
			if(shopBrandMap.containsKey(shopsBrandsBo.getBrandId())){
				List<ShopsBrandsBo> shopBrandsBoList = shopBrandMap.get(shopsBrandsBo.getBrandId());
				shopBrandsBoList.add(shopsBrandsBo);
				shopBrandMap.put(shopsBrandsBo.getBrandId(), shopBrandsBoList);
			}else{
				shopBrandMap.put(shopsBrandsBo.getBrandId(), Lists.newArrayList(shopsBrandsBo));
			}
		}
		return shopBrandMap;
	}

	@Override
	public List<ShopsBo> getShopBoList(List<Integer> shopIds) {
		if(CollectionUtils.isEmpty(shopIds)){
			return Lists.newArrayList();
		}
		ShopsBo[] ShopsBoList = null;
		BatchBaseRequest<Integer> baseRequest = new BatchBaseRequest<Integer>();
		baseRequest.setParams(shopIds);
		try{
			ShopsBoList = serviceCaller.asyncCall("product.batchGetShopsIntroByIds", baseRequest, ShopsBo[].class).get(3);
		}catch(Throwable e){
			LOGGER.warn("product.batchGetShopsIntroByIds failed shopIds is:{}", shopIds, e);
			return Lists.newArrayList();
		}
		return Lists.newArrayList(ShopsBoList);
	}
}
