package com.yoho.gateway.controller.product.web;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.product.BrandFolderVo;
import com.yoho.gateway.model.product.BrandSeriesVo;
import com.yoho.gateway.model.product.ShopsVo;
import com.yoho.gateway.model.product.WebBrandVo;
import com.yoho.gateway.service.search.ShopService;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.BrandFolderBo;
import com.yoho.product.model.BrandSeriesBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.BatchBaseRequest;
import com.yoho.product.request.WebBrandRequest;

/**
 * 品牌 Created by caoyan
 */
@Controller
public class WebBrandController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WebBrandController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	@Autowired
	private ShopService shopService;

	/**
	 * 根据品牌域名获取信息
	 * 
	 * @param domain
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=web.brand.byDomain")
	@ResponseBody
	@Cachable(expire = ExpireTime.web_brand_byDomain)
	public ApiResponse getBrandByDomain(@RequestParam(value = "domain") String domain) throws ServiceException {
		LOGGER.info("Begin call web.brand.byDomain. with param domain is {}", domain);
		if (StringUtils.isEmpty(domain)) {
			return new ApiResponse(404, "brand domain is null", null);
		}

		BaseRequest<String> req = new BaseRequest<String>();
		req.setParam(domain);
		BrandBo brandInfo = serviceCaller.call("product.queryBrandByDomain", req, BrandBo.class);
		if (null == brandInfo) {
			LOGGER.warn("call product.queryBrandByDomain with domain is {}, brand is not exists", domain);
			return new ApiResponse(405, "brand is not exists", null);
		}
		WebBrandVo brand = buildWebBrandVo(brandInfo);
		// 判断该品牌是否是无店铺（老的品牌页）、还是在1个单品店里、还是在多个多品店里
		buildWebShopBrand(brand);
		LOGGER.debug("call web.brand.byDomain with param is {}, with result is {}", domain, brandInfo);
		LOGGER.info("getBrandByDomain call success, domain is:{}, brand is:{}", domain, brand);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("brand info").data(brand).build();
		return response;
	}
    
    private void buildWebShopBrand(WebBrandVo brand) {
    	List<ShopsVo> shopVoList = getShopList(brand.getBrandId());
    	if(CollectionUtils.isEmpty(shopVoList)){
    		brand.setType("0");
    		return;
    	}
    	
    	// 判断是否存在单品店
    	for (ShopsVo shopsVo : shopVoList) {
			if(shopsVo.getMultBrandShopType()!=null && "1".equals(shopsVo.getMultBrandShopType())){
				brand.setType("2");
				brand.setShopId(String.valueOf(shopsVo.getShopsId()));
				brand.setShopTemplateType(shopsVo.getShopTemplateType());
				return;
			}
		}

		// 无单品店有多品店
		brand.setType("1");
	}

	/**
	 * 获取品牌分类
	 * 
	 * @param brandId
	 * @param status
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=web.brand.folder")
	@ResponseBody
	@Cachable(expire = ExpireTime.web_brand_folder)
	public ApiResponse getBrandFolder(@RequestParam(value = "brand_id", required = true) Integer brandId, @RequestParam(value = "status", required = false, defaultValue = "1") Integer status)
			throws ServiceException {
		LOGGER.info("Begin call method=web.brand.folder. with param brandId is {},status is {}", brandId, status);
		if (null == brandId || brandId.intValue() < 1) {
			return new ApiResponse(404, "brandId is null", null);
		}
		WebBrandRequest req = new WebBrandRequest();
		req.setBrandId(brandId);
		req.setStatus(status);

		BrandFolderBo[] boArr = serviceCaller.call("product.queryBrandFolder", req, BrandFolderBo[].class);
		if (null == boArr || boArr.length == 0) {
			LOGGER.warn("call product.queryBrandFolder is null with brandId is {}", brandId);
			return new ApiResponse(500, "brand folder is null", null);
		}
		List<BrandFolderVo> voList = buildBrandFolderVoList(boArr);
		LOGGER.debug("call web.brand.folder with result is {}", voList);
		LOGGER.info("getBrandFolder call success brandId is:{},status is :{}.",brandId, status);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("brand folder").data(voList).build();
		return response;
	}

	/**
	 * 获取品牌系列
	 * 
	 * @param brandId
	 * @param status
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=web.brand.series")
	@ResponseBody
	@Cachable(expire = ExpireTime.web_brand_series)
	public ApiResponse getBrandSeries(@RequestParam(value = "brand_id", required = true) Integer brandId, @RequestParam(value = "status", required = false, defaultValue = "1") Integer status)
			throws ServiceException {
		LOGGER.info("Begin call method=web.brand.series. with param brandId is {},status is {}", brandId, status);
		if (null == brandId || brandId.intValue() < 1) {
			return new ApiResponse(404, "brandId is null", null);
		}
		WebBrandRequest req = new WebBrandRequest();
		req.setBrandId(brandId);
		req.setStatus(status);
		BrandSeriesBo[] boArr = serviceCaller.call("product.queryBrandSeries", req, BrandSeriesBo[].class);
		if (null == boArr || boArr.length == 0) {
			LOGGER.warn("call product.queryBrandSeries is null with brandId is {}", brandId);
			return new ApiResponse(500, "brand series is null", null);
		}
		List<BrandSeriesVo> voList = buildBrandSeriesVoList(boArr);
		LOGGER.debug("call web.brand.series with result is {}", voList);
		LOGGER.info("getBrandSeries call success.brandId is {},status is {}", brandId, status);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("brand series").data(voList).build();
		return response;
	}

	@RequestMapping(params = "method=web.brand.info")
	@ResponseBody
	public ApiResponse getBrandByIds(@RequestParam(value = "ids", required = true) List<Integer> ids) throws ServiceException {
		LOGGER.info("Begin call method=web.brand.info. with param brandIds is {}", ids);
		if (CollectionUtils.isEmpty(ids)) {
			return new ApiResponse(404, "brandIds is null", null);
		}

		BatchBaseRequest<Integer> req = new BatchBaseRequest<Integer>();
		req.setParams(ids);

		BrandBo[] boArr = serviceCaller.call("product.queryBrandByIds", req, BrandBo[].class);
		if (null == boArr || boArr.length == 0) {
			LOGGER.warn("call product.queryBrandByIds is null with ids is {}", ids);
			return new ApiResponse(500, "brand is null", null);
		}
		JSONObject result = buildWebBrandDetailVoList(boArr);
		LOGGER.debug("call web.brand.info with result is {}", result);
		LOGGER.info("getBrandByIds call success ids is:{}.",ids);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("brand info").data(result).build();
		return response;
	}

	private JSONObject buildWebBrandDetailVoList(BrandBo[] boArr) {
		JSONObject obj = new JSONObject();
		for (BrandBo bo : boArr) {
			WebBrandVo vo = new WebBrandVo();
			vo.setBrandId(bo.getId());
			vo.setBrandName(bo.getBrandName());
			vo.setBrandDomain(bo.getBrandDomain());
			vo.setBrandIntro(bo.getBrandIntro());
			vo.setBrandBanner(bo.getBrandBanner());
			obj.put(String.valueOf(bo.getId()), vo);
		}

		return obj;
	}

	private List<BrandSeriesVo> buildBrandSeriesVoList(BrandSeriesBo[] boArr) {
		List<BrandSeriesVo> voList = Lists.newArrayList();
		for (BrandSeriesBo bo : boArr) {
			BrandSeriesVo vo = new BrandSeriesVo();
			vo.setId(bo.getId());
			vo.setBrandId(bo.getBrandId());
			vo.setSeriesName(bo.getSeriesName());
			vo.setOrderBy(bo.getOrderBy());
			vo.setStatus(bo.getStatus());
			vo.setSeriesBanner(null == bo.getSeriesBanner() ? "" : ImagesHelper.template(bo.getSeriesBanner(), "brandBanner", 1).split("\\?")[0]);
			voList.add(vo);
		}

		return voList;
	}

	private List<BrandFolderVo> buildBrandFolderVoList(BrandFolderBo[] boArr) {
		List<BrandFolderVo> voList = Lists.newArrayList();
		for (BrandFolderBo bo : boArr) {
			BrandFolderVo vo = new BrandFolderVo();
			vo.setId(bo.getId());
			vo.setBrandId(bo.getBrandId());
			vo.setBrandSortName(bo.getBrandSortName());
			vo.setParentId(bo.getParentId());
			vo.setOrderBy(bo.getOrderBy());
			vo.setStatus(bo.getStatus());
			vo.setBrandSortIco(null == bo.getBrandSortIco() ? "" : ImagesHelper.template(bo.getBrandSortIco(), "brandBanner", 1).split("\\?")[0]);
			voList.add(vo);
		}

		return voList;
	}

	private WebBrandVo buildWebBrandVo(BrandBo brandInfo) {
		WebBrandVo brand = new WebBrandVo();
		brand.setBrandId(brandInfo.getId());
		brand.setBrandName(brandInfo.getBrandName());
		brand.setBrandNameCn(brandInfo.getBrandNameCn());
		brand.setBrandNameEn(brandInfo.getBrandNameEn());
		brand.setBrandAlif(brandInfo.getBrandAlif());
		brand.setBrandDomain(brandInfo.getBrandDomain());
		brand.setBrandBanner(getImageUrl(brandInfo.getBrandBanner(), "brandBanner"));
		brand.setBrandIco(brandInfo.getBrandIco().replace("quality/80", "quality/90"));
		brand.setBrandIntro(brandInfo.getBrandIntro());
		brand.setStaticContentCode(brandInfo.getStaticContentCode());

		return brand;
	}

	private String getImageUrl(String fileName, String bucket) {
		String imageUrl = ImagesHelper.template2(fileName, bucket);

		return imageUrl.split("\\?")[0] + "?imageMogr2/thumbnail/{width}x{height}/extent/{width}x{height}/background/d2hpdGU=/position/center/quality/90";
	}

	/**
	 * 根据品牌id查询店铺列表
	 *
	 * @param id
	 */
	private List<ShopsVo> getShopList(Integer id) {
		List<ShopsVo> shopsVoList = shopService.getShopListByBrandId(id);
		return shopsVoList;
	}
}
