package com.yoho.gateway.controller.product.cnstore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yoho.gateway.cache.expire.product.ExpireTime;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.core.rest.exception.ServiceNotFoundException;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.product.ProductCacheFinder;
import com.yoho.gateway.controller.product.convert.ProductConvert;
import com.yoho.product.model.GoodsBo;
import com.yoho.product.model.GoodsImagesBo;
import com.yoho.product.model.GoodsSizeBo;
import com.yoho.product.model.ProductBannerBo;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductInfoBo;
import com.yoho.product.model.ProductIntroBo;
import com.yoho.product.model.StorageBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.CnstoreRequest;
import com.yoho.service.model.resource.AdsBo;
import com.yoho.service.model.resource.request.AdsRequest;

/**
 * 线下店查询商品信息
 * @author lixuxin
 *
 */
@Controller
public class StoreProductController {
    //请求成功
    private final static int REQUEST_SUCCESS_CODE = 200;
    private final static String REQUEST_SUCCESS_MSG = "获取商品信息成功.";
    private final static double SIVLER_DISCOUNT=0.95;
    private final static double GOLD_DISCOUNT=0.9;
    private final static double PLATINUM_DISCOUNT=0.88;
    private final Logger logger = LoggerFactory.getLogger(StoreProductController.class);
    @Autowired
    private ServiceCaller serviceCaller;
    @Autowired
    private ProductCacheFinder productCacheFinder;
    @Autowired
    private ProductConvert productConvert;
    /**
     * 线下店根据skuid查询商品相关信息
     * @param skuId
     * @return
     */
    @RequestMapping(params = "method=cnstore.product.get")
    @ResponseBody
    @Cachable(expire= ExpireTime.cnstore_product_get)
    public ApiResponse getStoreProductSku(@RequestParam(value="sku", required=false, defaultValue="0") Integer sku){
        if (null == sku||sku<1 ) {
            return new ApiResponse(404, "sku  Is Null",sku);
        }
        logger.info("getStoreProductSku method=store.product.get sku is:{}", sku);
        //查询库存信息 获取商品id或skn
        BaseRequest<Integer> request=new BaseRequest<Integer>();
        request.setParam(sku);
        StorageBo storageBo=serviceCaller.call("product.queryStorageBySkuId", request, StorageBo.class);
        if(storageBo==null){
        	logger.info("not exist storage info by this  sku is:{}", sku);
            return new ApiResponse.ApiResponseBuilder().code(500).message("not exist storage info by this sku").data(null).build();
        }
        request.setParam(storageBo.getProductId());
        ProductBo baseProduct = serviceCaller.call("product.queryProductDetailByProductId", request,ProductBo.class);
        if(baseProduct==null){
        	logger.info("not exist baseProduct info by this  productId is:{}", storageBo.getProductId());
            return new ApiResponse.ApiResponseBuilder().code(500).message("not exist product info by this sku").data(null).build();
        }
        request.setParam(baseProduct.getErpProductId());
        ProductInfoBo productInfoBo = serviceCaller.call("product.queryProductIntroBySkn", request,ProductInfoBo.class);
        if(productInfoBo==null){
            return new ApiResponse.ApiResponseBuilder().code(500).message("not exist product info by this sku").data(null).build();
        }
        //ProductBo productBo =productInfoBo.getProductBo();
		Map<String, Object> data=buildCnProduct(baseProduct,storageBo.getGoodsId(),productInfoBo.getProductIntroBo());
		data.put("brand_intro", baseProduct.getBrand().getBrandIntro());
		data.put("productFavorite",(int)(Math.random()*1000) );
        return new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message(REQUEST_SUCCESS_MSG).data(data).build();
    }
    /**
     *线下店相关信息
     * @param itemId
     * @param key
     * @return
     */
    @RequestMapping(params = "method=cnstore.product.duoban")
    @ResponseBody
    @Cachable(expire= ExpireTime.cnstore_product_duoban)
    public ApiResponse duoban(@RequestParam(value="item_id", required=false, defaultValue="0") String itemId,@RequestParam(value="key", required=false) String key){
        if (StringUtils.isEmpty(itemId)) {
            return new ApiResponse(404, "itemId  Is Null",itemId);
        }
        if (!"a90cd9d8eb4039a52be2be422b1689eb".equals(key)) {
            return new ApiResponse(404, "key  Is Null",key);
        }
        logger.info("getStoreProductSku method=store.product.duoban itemId is:{} , key is {}", itemId,key);
        String[] productIds= itemId.split("_");
        BaseRequest<Integer> request=new BaseRequest<Integer>();
        request.setParam(Integer.parseInt(productIds[0]));
        ProductBo baseProduct = serviceCaller.call("product.queryProductDetailByProductId", request,ProductBo.class);
        if(baseProduct==null){
            return new ApiResponse.ApiResponseBuilder().code(500).message("not exist product info by this skuid").data(null).build();
        }
        request.setParam(baseProduct.getErpProductId());
        //组装返回结果
		Map<String, Object> data=new HashMap<String,Object>();
		data.put("product_id", baseProduct.getId());
		data.put("status",baseProduct.getStatus().intValue()==1?"上架":"下架");
		data.put("product_name", baseProduct.getProductName());
		data.put("brand",baseProduct.getBrand().getBrandName() );
		data.put("max_sort", baseProduct.getMaxSortId());
		data.put("middle_sort",baseProduct.getMiddleSortId() );
		data.put("small_sort", baseProduct.getSmallSortId());
		data.put("price",baseProduct.getProductPriceBo().getSalesPrice() );
		data.put("sale_price",(baseProduct.getProductPriceBo().getSalesPrice().intValue()!=0&baseProduct.getProductPriceBo().getSalesPrice()!=baseProduct.getProductPriceBo().getSpecialPrice()? baseProduct.getProductPriceBo().getSpecialPrice():0));
		List<Map<String, Object>>  goodsImageList=Lists.newArrayList();
		for(GoodsBo goodsbo:baseProduct.getGoodsList()){
			for(GoodsImagesBo imagebo:goodsbo.getGoodsImagesList()){
				Map<String, Object> goods=new HashMap<String, Object>();
				goods.put("image", ImagesHelper.getImageUrl(imagebo.getImageUrl(), 510, 567));
				goods.put("is_default", imagebo.getIsDefault());
				goodsImageList.add(goods);
			}

		}
		data.put("image_list",goodsImageList );
        return new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message(REQUEST_SUCCESS_MSG).data(data).build();
    }
    private String getDefaultGoods(List<GoodsBo> goodsList) {
		for(GoodsBo bo:goodsList){
			if("Y".equals(bo.getIsDefault())){
				for(GoodsImagesBo imagebo:bo.getGoodsImagesList()){
					if("Y".equals(imagebo.getIsDefault())){
						return imagebo.getImageUrl();
					}
				}
			}
		}
		return "";
	}
	private List<String> buildImagesList(String productIntro) {
    	String[] imageArray=productIntro.split("src=");
		List<String> images=new  ArrayList<String>();
		
		for(int index=1;index<imageArray.length;index++){
			String s= imageArray[index].substring(1);
			images.add(s.substring(0, s.indexOf('"')));
		}
		return images;
	}
	/**
     * 线下店商品介绍
     * @param skuId 库存id
     * @return
     */
    @RequestMapping(params = "method=cnstore.product.info")
    @ResponseBody
    @Cachable(expire= ExpireTime.cnstore_product_info)
    public ApiResponse getStoreProductIntro(@RequestParam(value="sku", required=false, defaultValue="0") Integer sku){
        if (null == sku||sku<1 ) {
            return new ApiResponse(404, "sku  Is Null",sku);
        }
        logger.info("getStoreProductSku method=store.product.intro skuId is:{}", sku);
        //查询库存信息 获取商品id或skn
        BaseRequest<Integer> request=new BaseRequest<Integer>();
        request.setParam(sku);
        StorageBo storageBo=serviceCaller.call("product.queryStorageBySkuId", request, StorageBo.class);
        if(storageBo==null){
            return new ApiResponse.ApiResponseBuilder().code(500).message("Not exist Product Data").data(null).build();
        }
        request.setParam(storageBo.getProductId());
        ProductBo baseProduct = serviceCaller.call("product.queryProductDetailByProductId", request,ProductBo.class);
        if(baseProduct==null){
            return new ApiResponse.ApiResponseBuilder().code(500).message("not exist product info by this sku").data(null).build();
        }
        request.setParam(baseProduct.getErpProductId());
        ProductInfoBo productInfoBo = serviceCaller.call("product.queryProductIntroBySkn", request,ProductInfoBo.class);
        if(productInfoBo==null){
            return new ApiResponse.ApiResponseBuilder().code(500).message("not exist product info by this sku").data(null).build();
        }
        ProductBo productBo =productInfoBo.getProductBo();
        request.setParam(productBo.getErpProductId());
        CnstoreRequest cnstoreRequest=new CnstoreRequest();
        cnstoreRequest.setBrand(baseProduct.getBrandId());
        cnstoreRequest.setGender(baseProduct.getGender());
        cnstoreRequest.setPrice(baseProduct.getProductPriceBo().getSalesPrice().toString());
        @SuppressWarnings("unchecked")
		Map<String, Object> sameProduct = serviceCaller.call("product.querySameProduct", request,Map.class);
        //查询商品banner
        ProductBannerBo productBannerBo=null;
		try {
			productBannerBo = serviceCaller.call("product.queryProductBanner", request, ProductBannerBo.class);
		} catch (ServiceException e) {
			logger.debug("product.queryProductBanner ex is {}",e.getMessage());
		} catch (ServiceNotAvaibleException e) {
			logger.debug("product.queryProductBanner ex is {}",e.getMessage());
		} catch (ServiceNotFoundException e) {
			logger.debug("product.queryProductBanner ex is {}",e.getMessage());
		}
		Map<String, Object> data=buildCnProduct(baseProduct,storageBo.getGoodsId(),productInfoBo.getProductIntroBo());
		if(productInfoBo.getProductBo().getVipDiscountType()!=null){

			if((int)productInfoBo.getProductBo().getVipDiscountType()==1){
				data.put("vip_silver",baseProduct.getProductPriceBo().getSalesPrice()*SIVLER_DISCOUNT);
				data.put("vip_gold",baseProduct.getProductPriceBo().getSalesPrice()*GOLD_DISCOUNT);
				data.put("vip_platinum",baseProduct.getProductPriceBo().getSalesPrice()*PLATINUM_DISCOUNT);
			}
		}

		//查询促销信息
		data.put("promotion_banner","");
		data.put("promotion_url","");
		data.put("promotion_id",productBo.getIsPromotion());
		data.put("goods",buildGoodsList(productBo.getGoodsList() , storageBo.getGoodsId()));
		if(productBannerBo!=null){
			data.put("promotion_banner",ImagesHelper.getImageUrl(productBannerBo.getBannerImg() ,510,567));
			data.put("promotion_url", productBannerBo.getPromotionUrl());
		}else if("N".equals(productBo.getIsOutlets())){
			AdsRequest ads =new AdsRequest();
			ads.setPositionId(24);
			ads.setMaxSortId(0);
			ads.setMiddleSortId(0);
			AdsBo[] list=serviceCaller.call("resources.queryAdsList", ads, AdsBo[].class);
			if(list!=null&&list.length>0){
				data.put("promotion_banner", list[0].getAdsImage());
				data.put("promotion_url", list[0].getAdsUrl());
			}
			
		}
		data.put("sameGoods", sameProduct);

		data.put("productFavorite",(int)(Math.random()*1000) );
        return new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message(REQUEST_SUCCESS_MSG).data(data).build();
    }
    private String getGoodsName(Integer goodsId,ProductBo productBo){

		for(GoodsBo goodsbo:productBo.getGoodsList()){
			if(goodsbo.getId().equals(goodsId)){
				return goodsbo.getGoodsName();
			}
		}

    	return null;
    }
   private  Map<Integer, Object>  buildGoodsList(List<GoodsBo> list ,Integer goodsId){

	   Map<Integer, Object> goodsList=new HashMap<Integer,Object>();
		for(GoodsBo goodsBo:list){
			Map<String, Object> goodsData=new HashMap<String, Object>();
			if(goodsBo.getId().equals(goodsId)){
				goodsData.put("flag", "Y");
			}else{
				goodsData.put("flag", "N");
			}
			goodsData.put("color_image", ImagesHelper.getImageUrl(goodsBo.getColorImage(),510,567));
			goodsData.put("color", goodsBo.getColorName());
			List<Map<String, Object>> sizeList=new ArrayList<Map<String,Object>>();
			for(GoodsSizeBo sizeBo:goodsBo.getGoodsSizeBoList()){
				Map<String, Object> size=new HashMap<String, Object>();
				size.put("sku", sizeBo.getGoodsSizeSkuId());
				size.put("storage_num", sizeBo.getGoodsSizeStorageNum());
				size.put("size", sizeBo.getSizeName());
				sizeList.add(size);
			}
			goodsData.put("size", sizeList);
			goodsList.put(goodsBo.getId(), goodsData);
		}
		return goodsList;
    }
   private Map<String, Object> buildCnProduct(ProductBo productBo,Integer goodsId,ProductIntroBo productIntroBo){
	   Map<String, Object> data=new HashMap<String, Object>();

		data.put("product_skn",productBo.getErpProductId() );
		data.put("proudct_name", productBo.getProductName());
		data.put("goods_name",getGoodsName(goodsId,productBo) );
		data.put("brand_ico", ImagesHelper.getImageUrl(productBo.getBrand().getBrandIco(), 105, 105));
		data.put("brand", productBo.getBrand().getBrandName());
		data.put("brand_outline",productBo.getBrand().getBrandOutline() );
		data.put("market_price",  productBo.getProductPriceBo().getMarketPrice());
		data.put("sales_price",productBo.getProductPriceBo().getSalesPrice() );
		data.put("special_price",productBo.getProductPriceBo().getFormatSpecialPrice() );
		data.put("goodsDefault",ImagesHelper.getImageUrl(getDefaultGoods(productBo.getGoodsList()), 510, 567) );
		data.put("goods_img",buildImagesList( productIntroBo.getProductIntro()) );
		return data;
   }


}
