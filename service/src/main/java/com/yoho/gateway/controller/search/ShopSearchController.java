package com.yoho.gateway.controller.search;

import java.util.ArrayList;
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

import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.product.BrandShopsVo;
import com.yoho.gateway.model.product.ShopsVo;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.ProductBrandSearchService;
import com.yoho.gateway.service.search.ShopService;
import com.yoho.product.model.BrandBo;

/**
 * Created by wss on 2016/4/13
 */
@Controller
public class ShopSearchController {

    private final Logger logger = LoggerFactory.getLogger(ShopSearchController.class);

    @Autowired
    private ShopService shopService;
    
    @Autowired
    private ProductBrandSearchService productBrandSearchService;
    
    /**
     * 获取店铺下的二级品类列表
     * @param brand_id
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.shop.getSortInfo")
    @ResponseBody
	@Cachable(expire=600)
    public ApiResponse queryBrandInfoById(@RequestParam(value = "shop_id", required = true)Integer shopId,
    		@RequestParam(value = "yh_channel", required = false) String channel,
            @RequestParam(value = "gender", required = false) String gender) {
        logger.info("[method=app.shop.getSortInfo] param shopId is {}",shopId);
        
        if(null == shopId){
        	return new ApiResponse(500, "shopId IS Null", null);
        }
        // 根据shopId查找店铺内的品牌列表（搜索如果支持根据shopId查找品类列表，该步骤可以去掉）
        List<BrandBo> brandBoList = shopService.getBrandListByShopId(shopId);
        if (CollectionUtils.isEmpty(brandBoList)){
            return new ApiResponse(500, "brands in shop IS Null", null);
        }
        
        List<Integer> brandIds = new ArrayList<Integer>();
        for (BrandBo brandBo : brandBoList) {
			brandIds.add(brandBo.getId());
		}

        // 目前品牌店铺需求，不需要区分channel和gender
        ProductSearchReq brandSearchReq = new ProductSearchReq().setBrand(StringUtils.join(brandIds, ",")).setSearchFrom("search.shop");
        Object result = shopService.searchSortByBrandId(brandSearchReq);
        return new ApiResponse.ApiResponseBuilder().code(200).message("Shop sort Intro").data(result).build();
    }
    
    /**
     * 商品详情页调用：获取指定品牌的店铺信息
	 * (1)不存在店铺或者存在单品店，返回品牌信息
	 * (2)品牌存在多个店铺中，返回多个店铺信息
     * @param brand_id
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.shop.queryShopsByBrandId")
    @ResponseBody
	@Cachable(expire=600)
    public ApiResponse queryShopsByBrandId(@RequestParam(value = "brand_id", required = true)Integer brandId) {
        logger.info("[method=app.shop.queryShopsByBrandId] param brandId is {}",brandId);
        if(null == brandId){
        	return new ApiResponse(500, "brandId IS Null", null);
        }
        List<BrandShopsVo> brandShopVoList = new ArrayList<BrandShopsVo>();
        // 根据shopId查找店铺内的品牌列表（搜索如果支持根据shopId查找品类列表，该步骤可以去掉）
        List<ShopsVo> shopsVoList = shopService.getShopListByBrandId(brandId);
        // 品牌
        BrandBo brandBo = productBrandSearchService.queryBrandById(brandId);
        // 品牌不存在店铺
        if(CollectionUtils.isEmpty(shopsVoList)){
        	if (null == brandBo){
                return new ApiResponse(500, "brandBo IS Null", null);
            }
        	BrandShopsVo brandShopVo = convertBrandBoToVo(brandBo);
        	brandShopVoList.add(brandShopVo);
        	return new ApiResponse.ApiResponseBuilder().code(200).message("product brandshop Intro").data(brandShopVoList).build();
    	}
        
    	// 判断是否存在单品店，不存在则展示多个店铺列表
        boolean flag = false;// 不存在单品店
    	for (ShopsVo shopsVo : shopsVoList) {
    		// 只要存在单品店铺，就跟线上一致
			if(shopsVo.getMultBrandShopType()!=null && "1".equals(shopsVo.getMultBrandShopType())){
				BrandShopsVo brandShopVo = convertSingleBrandShopsVo(shopsVo, brandBo);
	        	brandShopVoList.add(brandShopVo);
	        	flag = true;// 存在单品店
				break;
			}
		}
        
    	if(!flag){
    		for (ShopsVo shopsVo : shopsVoList) {
    			BrandShopsVo brandShopVo = convertBrandShopsVo(shopsVo);
    			brandShopVo.setBrandId(brandId);
	        	brandShopVoList.add(brandShopVo);
    		}
    	}
    	
        return new ApiResponse.ApiResponseBuilder().code(200).message("product brandshop Intro").data(brandShopVoList).build();
    }

    
	private BrandShopsVo convertBrandShopsVo(ShopsVo shopsVo) {
		BrandShopsVo brandShopsVo = new BrandShopsVo();
		brandShopsVo.setShopId(shopsVo.getShopsId());
		brandShopsVo.setBrandName(shopsVo.getShopName());
		brandShopsVo.setBrandDomain(shopsVo.getShopDomain());
		brandShopsVo.setBrandIco(StringUtils.isNotEmpty(shopsVo.getShopLogo())?ImagesHelper.getImageUrl(shopsVo.getShopLogo(), 100, 100, 0):null);
		brandShopsVo.setShopTemplateType(shopsVo.getShopTemplateType());
		return brandShopsVo;
	}

	private BrandShopsVo convertBrandBoToVo(BrandBo brandBo) {
		BrandShopsVo brandShopsVo = new BrandShopsVo();
		brandShopsVo.setBrandId(brandBo.getId());
		brandShopsVo.setBrandName(brandBo.getBrandName());
		brandShopsVo.setBrandDomain(brandBo.getBrandDomain());
		brandShopsVo.setBrandIco(brandBo.getBrandIco());
		return brandShopsVo;
	}

	private BrandShopsVo convertSingleBrandShopsVo(ShopsVo shopsVo, BrandBo brandBo) {
		BrandShopsVo brandShopsVo = new BrandShopsVo();
		brandShopsVo.setShopId(shopsVo.getShopsId());
		brandShopsVo.setBrandName(brandBo.getBrandName());
		brandShopsVo.setBrandDomain(shopsVo.getShopDomain());
		brandShopsVo.setBrandIco(brandBo.getBrandIco());
		brandShopsVo.setShopTemplateType(shopsVo.getShopTemplateType());
		return brandShopsVo;
	}
}
