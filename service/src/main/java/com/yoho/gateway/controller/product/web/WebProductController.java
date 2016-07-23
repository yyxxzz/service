package com.yoho.gateway.controller.product.web;


import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.product.TogetherProductRspVo;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.ProductSearchService;
import com.yoho.product.model.*;
import com.yoho.product.model.web.ProductExBo;
import com.yoho.product.request.BaseRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Random;

/**
 * Created by chenchao on 2016/2/19.
 */
@Controller
public class WebProductController {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(WebProductController.class);

    @Autowired
    private ServiceCaller serviceCaller;
    
    @Autowired
	private ProductSearchService categoryProductSearchService;
    
    /**
     * 根据产品ID查询banner
     * @param productId
     * @return
     */
    @RequestMapping(params = "method=web.productBanner.data")
    @ResponseBody
    @Cachable(expire = ExpireTime.web_productBanner_data)
    public ApiResponse queryProductBannerByPrdId(@RequestParam(value = "product_id") Integer productId ) {

        if (null == productId) {
            return new ApiResponse(404, "product_id Is Null", null);
        }
        BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
        baseRequest.setParam(productId);
        final String serviceName = "product.queryProductBanner";
        try {
            ProductBannerBo productBannerBo = serviceCaller.call(serviceName, baseRequest, ProductBannerBo.class);
            return new ApiResponse.ApiResponseBuilder().data(productBannerBo).code(200).message("product banner info").build();
        } catch (Exception e) {
            LOGGER.warn("invoke {} failed,{}", serviceName, e);
            if(e instanceof ServiceException){
                throw e;
            }
            throw new ServiceNotAvaibleException(serviceName,e);
        }
    }

    /**
     * 根据产品ID查询搭配
     * @param productId
     * @return
     */
    @RequestMapping(params = "method=web.productCollocation.list")
    @ResponseBody
    @Cachable(expire = ExpireTime.web_productCollocation_list)
    public ApiResponse queryProductCollocationByPrdId(@RequestParam(value = "product_id") Integer productId ) {

        if (null == productId) {
            return new ApiResponse(404, "product_id Is Null", null);
        }
        BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
        baseRequest.setParam(productId);
        final String serviceName = "product.queryProductCollocation";
        try {
            ProductCollocationBo[] productBannerBo = serviceCaller.call(serviceName, baseRequest, ProductCollocationBo[].class);
            return new ApiResponse.ApiResponseBuilder().data(productBannerBo).code(200).message("product collocation list").build();
        } catch (Exception e) {
            LOGGER.warn("invoke {} failed,{}", serviceName, e);
            if(e instanceof ServiceException){
                throw e;
            }
            throw new ServiceNotAvaibleException(serviceName,e);
        }
    }

    /**
     * 根据产品ID查询模特卡
     * @param productId
     * @return
     */
    @RequestMapping(params = "method=web.productModelcard.list")
    @ResponseBody
    @Cachable(expire = ExpireTime.web_productModelcard_list)
    public ApiResponse queryProductModelcardByPrdId(@RequestParam(value = "product_id") Integer productId ) {
        if (null == productId) {
            return new ApiResponse(404, "product_id Is Null", null);
        }
        BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
        baseRequest.setParam(productId);
        final String serviceName = "product.queryProductModelcard";
        try {
            ProductModelcardBo[] productBannerBo = serviceCaller.call(serviceName, baseRequest, ProductModelcardBo[].class);
            return new ApiResponse.ApiResponseBuilder().data(productBannerBo).code(200).message("product modelcard list").build();
        } catch (Exception e) {
            LOGGER.warn("invoke {} failed,{}", serviceName, e);
            if(e instanceof ServiceException){
                throw e;
            }
            throw new ServiceNotAvaibleException(serviceName,e);
        }
    }
    
    /**
     * 根据品牌ID查询banner
     * @param brandId
     * @return
     */
    @RequestMapping(params = "method=web.brand.banner")
    @ResponseBody
    @Cachable(expire = ExpireTime.web_brand_banner)
    public ApiResponse queryBrandBannerByBrandId(@RequestParam(value = "brand_id") Integer brandId ) {
    	LOGGER.info("come into method=web.brand.banner brandId="+brandId);
        if (null == brandId) {
            return new ApiResponse(404, "brand_id Is Null", null);
        }
        BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
        baseRequest.setParam(brandId);
        final String serviceName = "product.queryBrandBanner";
        BrandBannerBo brandBannerBo = serviceCaller.call(serviceName, baseRequest, BrandBannerBo.class);
        return new ApiResponse.ApiResponseBuilder().data(brandBannerBo).code(200).message("brand banner info").build();
    }
    
    /**
     * 查询凑单商品
	 * @param page		当前页数  从1开始
	 * @param viewNum	每页记录数，默认6
	 * @param price		价格区间,起始价格以逗号分隔，如查询300-600元“price=300,600”
	 * @return 凑单商品列表
	 */
	@RequestMapping(params = "method=web.product.together")
	@ResponseBody
	@Cachable(expire=120)
	public ApiResponse queryTogetherProductList(@RequestParam(value = "page", required = false,defaultValue="1") Integer page ,
                                                @RequestParam(value = "app_version", required = false) String appVersion) {
		LOGGER.info("come into togetherProductList method=web.product.together");
		// 组装参数
		ProductSearchReq req = new ProductSearchReq().setPage(getPage(page)).setLimit(6).setPrice("0,100").setAppVersion(appVersion).setSearchFrom("search.togetherProduct");
		
		LOGGER.info("Method of togetherProductList out.req : {}", req);
		// 调用搜索接口、搜索结果进行处理
		TogetherProductRspVo data = categoryProductSearchService.searchTogetherProductList(req);
		
		return new ApiResponse.ApiResponseBuilder().code(200).message("cart.getProdcut").data(data).build();
	}

    /**
     * 根据产品ID查询舒适度
     * @param productId
     * @return
     */
    @RequestMapping(params = "method=web.productComfort.data")
    @ResponseBody
    @Cachable(expire = ExpireTime.web_productComfort_data)
    public ApiResponse queryProductComfort(@RequestParam(value = "product_id") Integer productId ){
        if (null == productId) {
            return new ApiResponse(404, "product_id Is Null", null);
        }
        BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
        baseRequest.setParam(productId);
        final String serviceName = "product.queryProductComfort";
        try {
            ComfortBo[] comfortBo = serviceCaller.call(serviceName, baseRequest, ComfortBo[].class);
            return new ApiResponse.ApiResponseBuilder().data(comfortBo).code(200).message("product comfort").build();
        } catch (Exception e) {
            LOGGER.warn("invoke {} failed,{}", serviceName, e);
            if(e instanceof ServiceException){
                throw e;
            }
            throw new ServiceNotAvaibleException(serviceName,e);
        }
    }

    /**
     * 模特试穿
     * @param productSkn
     * @return
     */
    @RequestMapping(params = "method=web.productModelTry.data")
    @ResponseBody
    @Cachable(expire = ExpireTime.web_productModelTry_data)
    public ApiResponse queryModelTryBySkn(@RequestParam(value = "product_skn") Integer productSkn){
        if (null == productSkn) {
            return new ApiResponse(404, "product_skn Is Null", null);
        }
        BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
        baseRequest.setParam(productSkn);
        final String serviceName = "product.queryModelTryBySkn";
        try {
            ModelTryBoWrapper modelTryBoWrapper = serviceCaller.call(serviceName, baseRequest, ModelTryBoWrapper.class);
            return new ApiResponse.ApiResponseBuilder().data(modelTryBoWrapper).code(200).message("product ModelTry").build();
        } catch (Exception e) {
            LOGGER.warn("invoke {} failed,{}", serviceName, e);
            if(e instanceof ServiceException){
                throw e;
            }
            throw new ServiceNotAvaibleException(serviceName,e);
        }
    }
    
    @RequestMapping(params = "method=web.queryProductInfoBySkuId")
    @ResponseBody
    @Cachable(expire = ExpireTime.web_queryProductInfoBySkuId)
    public ApiResponse queryProductInfoBySkuId(@RequestParam(value = "skuId") Integer skuId) throws GatewayException{
        if (null == skuId) {
            throw new GatewayException(500,"skuId can't be null");
        }
        BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
        baseRequest.setParam(skuId);
        final String serviceName = "product.queryProductInfoBySkuId";
        return new ApiResponse.ApiResponseBuilder().data(serviceCaller.call(serviceName, baseRequest, ProductExBo.class)).code(200).message("product url info").build();
    }
    
	/**
	 * 获取随机页数
	 * @return
	 */
	private Integer getPage(Integer page){
		Random rd = new Random();
		return rd.nextInt(10) + page;
	}
}
