/**
 *
 */
package com.yoho.gateway.controller.product.decorator;

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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.product.BrandVo;
import com.yoho.gateway.model.product.ShopsDecoratorResourceGiftVo;
import com.yoho.gateway.model.product.ShopsDecoratorTemplateResourceVo;
import com.yoho.gateway.model.product.ShopsVo;
import com.yoho.gateway.service.search.ProductBrandSearchService;
import com.yoho.gateway.service.search.ShopService;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.ShopsBo;
import com.yoho.product.model.ShopsDecoratorTemplateBo;
import com.yoho.product.model.ShopsDecoratorTemplateResourceBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.FavoriteRequest;

/**
 * 店铺装修前台接口
 * @author created by lixuxin on 04/12/2016
 *
 */
@Controller
public class ShopsDecoratorController {
	private static Logger logger = LoggerFactory.getLogger(ShopsDecoratorController.class);

    private static final String SHOP_NOT_EXIST = "店铺不存在";
    
    private static final String SHOP_BANNER_NOT_EXIST = "店铺banner不存在";
    /**
     * 店铺基本模板
     */
    private final static String SHOP_TOP_BANNER_BASE="shopTopBanner_base";
    /**
     * 店铺经典模板PC
     */
    private final static String SHOP_TOP_BANNER="shopTopBanner";
    /**
     * 店铺经典模板APP
     */
    private final static String SHOP_TOP_BANNER_APP="shopTopBanner_APP";
    
	@Autowired
	private ServiceCaller serviceCaller;

	@Autowired
	private ProductBrandSearchService productBrandSearchService;

	@Autowired
	private CacheClient cacheClient;

	@Autowired
	private ShopService shopService;

	/**
	 * 查询店铺对应的装修元素
	 * @param shopsId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.shopsdecorator.getList")
	@ResponseBody
	@Cachable(expire=ExpireTime.app_shopsdecorator_getList)
	public ApiResponse queryShopsDecoratorTemplateResource(@RequestParam(value="shop_id",defaultValue="0") Integer shopId) throws GatewayException {
		logger.info("ShopsDecoratorController==>queryShopsDecoratorTemplateResource param shop_id is {}",shopId);
		if(null==shopId||shopId<1){
			throw new GatewayException(500, "shop_id must be not null!");
		}
		BaseRequest<Integer> request=new BaseRequest<Integer>();
		request.setParam(shopId);
		ShopsDecoratorTemplateBo shopsDecoratorTemplateBo=serviceCaller.call("product.getShopsDecoratorTemplateBoById", request, ShopsDecoratorTemplateBo.class);
		logger.info("ShopsDecoratorController==>queryShopsDecoratorTemplateResource shopsDecoratorTemplateBo  is {}",shopsDecoratorTemplateBo);
		if(shopsDecoratorTemplateBo==null){
			throw new GatewayException(405," not exist data by shopId  ");
		}
		ShopsDecoratorTemplateResourceBo[] list=serviceCaller.call("product.getShopsDecoratorTemplateResourceList", request, ShopsDecoratorTemplateResourceBo[].class);
		ShopsDecoratorResourceGiftVo vo=convetShopsDecoratorTemplateBo2Vo( shopsDecoratorTemplateBo, list);
		logger.info("ShopsDecoratorController==>queryShopsDecoratorTemplateResource vo   is {}",vo);
		return new ApiResponse.ApiResponseBuilder().code(200).message("shops decorator source  data.").data(vo).build();
	}
	/**
	 * 根据店铺id查询店铺介绍
	 * @param shopsId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.shops.getIntro")
	@ResponseBody
	public ApiResponse queryShopsIntroByShopId(@RequestParam(value="shop_id",defaultValue="0") Integer shopId,@RequestParam(value="uid",defaultValue="0") Integer uid) throws GatewayException {
		logger.info("ShopsDecoratorController==>queryShopsIntroByShopId param shop_id is {}",shopId);
		if(null==shopId||shopId<1){
			throw new GatewayException(500, "shop_id must be not null!");
		}
		ShopsBo shopsBo=productBrandSearchService.queryShopsBoById(shopId);
		logger.info("ShopsDecoratorController==>queryShopsIntroByShopId shopsBo  is {}",shopsBo);
		if(shopsBo==null){
			return new ApiResponse.ApiResponseBuilder().code(200).message("not exist shopsIntro by value of shopId that you send as param.").build();
		}
		
		//获取头部图片是否展示店铺名称字段值
		boolean isShowShopName = true;
		BaseRequest<Integer> request=new BaseRequest<Integer>();
		request.setParam(shopId);
		ShopsDecoratorTemplateBo shopsDecoratorTemplateBo=serviceCaller.asyncCall("product.getShopsDecoratorTemplateBoById", request, ShopsDecoratorTemplateBo.class).get(1);
		logger.info("ShopsDecoratorController==>getShopsDecoratorTemplateBoById shopsDecoratorTemplateBo  is {}",shopsDecoratorTemplateBo);
		if(shopsDecoratorTemplateBo==null){
			isShowShopName = true;
		}else{
			shopsBo.setShopTemplateType(shopsDecoratorTemplateBo.getTemplateType());
		}
		
		ShopsDecoratorTemplateResourceBo[] list=serviceCaller.asyncCall("product.getShopsDecoratorTemplateResourceList", request, ShopsDecoratorTemplateResourceBo[].class).get(1);
		for(ShopsDecoratorTemplateResourceBo bo : list){
			if("1".equals(shopsBo.getShopTemplateType()) && "shopTopBanner_base".equals(bo.getResourceName())){// 1：基础模板
				JSONArray resourceData=JSON.parseArray(bo.getResourceData());
				if("N".equals(resourceData.getJSONObject(0).getString("isShowShopName"))){
					isShowShopName = false;
					break;
				}
			}else if("2".equals(shopsBo.getShopTemplateType()) && "shopTopBanner".equals(bo.getResourceName())){// 2：经典模板
				JSONArray resourceData=JSON.parseArray(bo.getResourceData());
				if("N".equals(resourceData.getJSONObject(0).getString("isShowShopName"))){
					isShowShopName = false;
					break;
				}
			}
		}
		
		boolean isFavorite = false;
		if(uid!=null&&uid>0){
			FavoriteRequest favRequest = new FavoriteRequest();
			favRequest.setId(shopId);
			favRequest.setUid(uid);
			favRequest.setType("shop");
			//1s之内不给请求就直接返回,不能死等
			try {
				isFavorite = serviceCaller.asyncCall("product.isFavorite", favRequest,Boolean.class).get(1);
			} catch (Throwable e) {
				// 捕捉异常，让流程继续走下去
				logger.warn("call product.isFavorite fail uid:{},shop_id:{}",uid, shopId, e);
				isFavorite = false;
			}
		}
		
		ShopsVo vo=convetBo2Vo(shopsBo,isFavorite, isShowShopName);
		return new ApiResponse.ApiResponseBuilder().code(200).message("query successed!").data(vo).build();
	}

	/**
	 * 根据店铺id查询该店铺下面的品牌
	 * @param shopsId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.shops.getShopsBrands")
	@ResponseBody
	@Cachable(expire=ExpireTime.app_shops_getShopsBrands)
	public ApiResponse queryShopsBrandsByShopId(@RequestParam(value="shop_id",defaultValue="0") Integer shopId) throws GatewayException {
		logger.info("ShopsDecoratorController==>queryShopsBrandsByShopId param shop_id is {}",shopId);
		if(null==shopId||shopId<1){
			throw new GatewayException(500, "shop_id must be not null!");
		}
		BaseRequest<Integer> request=new BaseRequest<Integer>();
		request.setParam(shopId);
		BrandBo[] list=serviceCaller.call("product.queryShopsBrandsById", request, BrandBo[].class);

		if(list==null||list.length==0){
			return new ApiResponse.ApiResponseBuilder().code(200).message("not exist BrandBo by value of shopId that you send as param.").build();
		}
		logger.info("ShopsDecoratorController==>queryShopsBrandsByShopId list is {}",list.toString());
		List<BrandVo> listVo=Lists.newArrayList();
		//组装返回列表
		for(BrandBo brandBo:list){
			BrandVo brandVo=new BrandVo();
			brandVo.setBrandIco(brandBo.getBrandIco());
			brandVo.setBrandId(brandBo.getId());
			brandVo.setBrandName(brandBo.getBrandName());
			brandVo.setBrandDomain(brandBo.getBrandDomain());
			listVo.add(brandVo);
		}
		logger.info("ShopsDecoratorController==>queryShopsBrandsByShopId vo is {}",listVo.toString());
		return new ApiResponse.ApiResponseBuilder().code(200).message("query successed!").data(listVo).build();
	}
	
	/**
     * 根据店铺ID获取店铺的banner
     * @param shop_id
     * @param client_type:app,web
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.shop.banner")
    @ResponseBody
    @Cachable(expire = ExpireTime.app_shop_banner)
    public ApiResponse queryShopBanner(@RequestParam(value = "shop_id", required = true)Integer shopId,
    		@RequestParam(value = "client_type", required = false)String clientType) {
        logger.info("[method=app.brand.queryShopBanner] param shop_id is {}", shopId);
        if(null == shopId||shopId<1){
        	return new ApiResponse(500, SHOP_NOT_EXIST, null);
        }
        BaseRequest<Integer> request=new BaseRequest<Integer>();
		request.setParam(shopId);
		
		ShopsDecoratorTemplateBo shopsDecoratorTemplateBo=serviceCaller.call("product.getShopsDecoratorTemplateBoById", request, ShopsDecoratorTemplateBo.class);
		logger.info("ShopsDecoratorController==>queryShopBanner shopsDecoratorTemplateBo  is {}",shopsDecoratorTemplateBo);
		if(shopsDecoratorTemplateBo==null){
			return new ApiResponse(500, SHOP_BANNER_NOT_EXIST, null);
		}
		
		ShopsDecoratorTemplateResourceBo[] list=serviceCaller.call("product.getShopsDecoratorTemplateResourceList", request, ShopsDecoratorTemplateResourceBo[].class);
        if(list==null || list.length==0){
        	return new ApiResponse(500, SHOP_BANNER_NOT_EXIST, null);
        }
        String shopBanner = getShopBanner(shopsDecoratorTemplateBo, list, clientType);
        if(StringUtils.isEmpty(shopBanner)){
        	return new ApiResponse(500, SHOP_BANNER_NOT_EXIST, null);
        }
        
        String bannerImage = shopBanner + "?imageMogr2/auto-orient/strip/thumbnail/x{height}/crop/{width}x{height}";
        JSONObject obj = new JSONObject();
        obj.put("banner", bannerImage);
        
        return new ApiResponse.ApiResponseBuilder().code(200).message("banner").data(obj).build();
    }
    
	private String getShopBanner(ShopsDecoratorTemplateBo shopsDecoratorTemplateBo, ShopsDecoratorTemplateResourceBo[] list, String clientType) {
		String templateType = shopsDecoratorTemplateBo.getTemplateType();//店铺模板类型 1 基础模板  2 经典模板
		String shopBanner = "";
		for(ShopsDecoratorTemplateResourceBo bo:list){
			if("1".equals(templateType) && SHOP_TOP_BANNER_BASE.equals(bo.getResourceName())){
				shopBanner = buildShopBanner(bo);
				break;
			}else if("2".equals(templateType) && ("iphone".equals(clientType) || "android".equals(clientType)) && SHOP_TOP_BANNER_APP.equals(bo.getResourceName())){
				shopBanner = buildShopBanner(bo);
				break;
			}else if("2".equals(templateType) && ("web".equals(clientType) || "h5".equals(clientType)) && SHOP_TOP_BANNER.equals(bo.getResourceName())){
				shopBanner = buildShopBanner(bo);
				break;
			}
			
		}
		return shopBanner;
	}
	private String buildShopBanner(ShopsDecoratorTemplateResourceBo bo) {
		if(null==bo){
			return null;
		}
		String shopBanner = "";
		JSONArray shopTopBanner=JSON.parseArray(bo.getResourceData());
		for(int index=0;index<shopTopBanner.size();index++){
			JSONObject jsonObject=shopTopBanner.getJSONObject(index);
			shopBanner = jsonObject.getString("shopSrc");
		}
		
		return shopBanner;
	}
	private ShopsVo convetBo2Vo(ShopsBo shopsBo, boolean isFavorite, boolean isShowShopName){
		if(shopsBo==null){
			return null;
		}
		ShopsVo vo=new ShopsVo();
		vo.setShopDomain(shopsBo.getShopDomain());
		vo.setShopIntro(shopsBo.getShopIntro());
		if(StringUtils.isEmpty(shopsBo.getShopLogo())){
			vo.setShopLogo("");
		}else{
			vo.setShopLogo(ImagesHelper.template2(shopsBo.getShopLogo(), "yhb-img01"));
		}

		vo.setShopName(shopsBo.getShopName());
		vo.setShopsId(shopsBo.getShopsId());
		vo.setIsFavorite(isFavorite?"Y":"N");
		vo.setIsShowShopName(isShowShopName ? "Y" : "N");
		vo.setMultBrandShopType(String.valueOf(shopsBo.getShopsType()));
		vo.setShopTemplateType(shopsBo.getShopTemplateType());
		return vo;
	}

	private ShopsDecoratorResourceGiftVo convetShopsDecoratorTemplateBo2Vo(ShopsDecoratorTemplateBo shopsDecoratorTemplateBo,ShopsDecoratorTemplateResourceBo[] list){
		ShopsDecoratorResourceGiftVo vo=new ShopsDecoratorResourceGiftVo();
		if(shopsDecoratorTemplateBo!=null){
			vo.setPlateform(shopsDecoratorTemplateBo.getPlatform());
			vo.setTemplateType(shopsDecoratorTemplateBo.getTemplateType());
		}
		List<ShopsDecoratorTemplateResourceVo> listVO=Lists.newArrayList();
		if(list!=null){
			for(ShopsDecoratorTemplateResourceBo bo:list){
				ShopsDecoratorTemplateResourceVo sourceVo=new ShopsDecoratorTemplateResourceVo();
				sourceVo.setId(bo.getId());
				sourceVo.setResourceData(bo.getResourceData());
				sourceVo.setShopsDecoratorId(bo.getShopsDecoratorId());
				sourceVo.setShopsDecoratorTemplateId(bo.getShopsDecoratorTemplateId());
				sourceVo.setResourceName(bo.getResourceName());
				listVO.add(sourceVo);
			}
		}
		vo.setList(listVO);
		return vo;
	}

	/**
	 * 单品店设置品牌的名称和logo
	 * @param vo
	 */
	private void processShopBrandInfo(ShopsVo vo) {
		if(vo==null || !"1".equals(vo.getMultBrandShopType())){
			return;
		}

		// 查询品牌信息
		List<BrandBo> brandBoList = shopService.getBrandListByShopId(vo.getShopsId());
		if(CollectionUtils.isNotEmpty(brandBoList)){
			BrandBo brandBo = brandBoList.get(0);
			vo.setShopLogo(brandBo.getBrandIco());
			vo.setShopName(brandBo.getBrandName());
		}
	}
}
