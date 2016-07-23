package com.yoho.gateway.controller.product;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.product.convert.FavoriteAssistConvert;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.BrandFavoriteReqVO;
import com.yoho.gateway.model.request.ProductFavoriteReqVO;
import com.yoho.gateway.service.favorite.FavoriteService;
import com.yoho.product.constants.FavoriteProductParams;
import com.yoho.product.model.FavoriteWrapper;
import com.yoho.product.model.ProductBo;
import com.yoho.product.request.FavoriteRequest;
import com.yoho.product.response.PageResponseBo;
import com.yoho.product.response.VoidResponse;
import com.yoho.service.model.request.BrandFavoriteReqBO;
import com.yoho.service.model.request.PriceReductionRequest;

@Controller
public class FavoriteController {

	private static Logger logger = LoggerFactory.getLogger(FavoriteController.class);
	
	private static final String FAVORITE_PRODUCT_LIST = "favorite product list";
	
	private static final String FAVORITE_BRAND_LIST = "favorite brand list";
	
	private static final String FAVORITE_SHOP_LIST = "favorite shop list";

	private static final String ADD_FAVORITE_SUCCESS = "收藏成功";

	private static final String CANCEL_FAVORITE_SUCCESS = "取消收藏成功";
	
	private static final String CANCEL_FAVORITE_FAILED = "取消收藏失败";
	
	@Resource
	private ServiceCaller serviceCaller;
	
	@Resource
	private FavoriteService favoriteService;
	
	@Autowired
	private CacheClient cacheClient;
	
	@Autowired
	private MemecacheClientHolder memecacheClientHolder;

    @Autowired
    private CallBackChain chain;
	
	/**
	 * 获取收藏的商品列表
	 * @param vo
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=app.favorite.product")
	@ResponseBody
	public ApiResponse product(ProductFavoriteReqVO vo) throws ServiceException {
		logger.info("Begin call favorite.product gateway. with param ProductFavoriteReqVO is {}", vo);
		final String key="yh:gw:userFavoriteProduct:"+vo.getUid();
		Map<String, Object> map=cacheClient.get(key, Map.class);
		if(MapUtils.isEmpty(map))
		{
			map=favoriteService.queryFavoriteProductList(FavoriteAssistConvert.converToBo(vo, FavoriteProductParams.TYPE_PRODUCT));
			cacheClient.set(key, 60, map);
		}
		logger.info("end favorite.product gateway uid is:{},total is:{}",vo.getUid(),null==map?"":map.get("total"));
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(FAVORITE_PRODUCT_LIST).data(map).build();
		return response;
	}
	
	/**
	 * 获取收藏的品牌列表
	 * @param vo
	 * @return
	 * @throws ServiceException
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(params = "method=app.favorite.brand")
	@ResponseBody
	public ApiResponse brand(BrandFavoriteReqVO vo) throws ServiceException {
		logger.info("Begin call brand gateway. with param ProductFavoriteReqVO is {}", vo);
		BrandFavoriteReqBO bo = new BrandFavoriteReqBO();
		BeanUtils.copyProperties(vo, bo);
		if (StringUtils.isEmpty(bo.getGender())) {
			bo.setGender("1,3");
		}
		PageResponseBo<JSONObject> result = serviceCaller.call("users.getFavoriteBrandList", bo, PageResponseBo.class);
		logger.debug("call users.getFavoriteBrandList with param is {}, with result is {}", vo, result);
		
		// 处理返回结果，对品牌或店铺的跳转规则做处理
		favoriteService.getBrandOrShopForwardType(result.getList());
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("brand_list", result.getList());
		map.put("page_total", result.getPage_total());
		map.put("page", result.getPage());
		map.put("total", result.getTotal());
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(FAVORITE_BRAND_LIST).data(map).build();
		return response;
	}

	@RequestMapping(params = "method=app.favorite.isFavorite")
	@ResponseBody
	public ApiResponse isFavorite(FavoriteRequest favoriteRequest){
		String favoriteType = favoriteRequest.getType();
		String message = "";
        // product，缓存key和接口有改变
		if (FavoriteProductParams.TYPE_PRODUCT.equals(favoriteType)){
			message = FAVORITE_PRODUCT_LIST;
            ProductBo productBo = new ProductBo();
            productBo.setId(favoriteRequest.getId());
            // 复用chain的方法
            chain.onCallBackUserFavorite(productBo,favoriteRequest.getUid());
            return new ApiResponse.ApiResponseBuilder().message(message).data("Y".equals(productBo.getIsCollect())).build();
		}
		if (FavoriteProductParams.TYPE_BRAND.equals(favoriteType)){
			message = FAVORITE_BRAND_LIST;
		}
		if (FavoriteProductParams.TYPE_SHOP.equals(favoriteType)){
			message = FAVORITE_SHOP_LIST;
		}
		boolean isFavorite = false;
		final String favoriteKey="yh:gw:favorite:"+favoriteRequest.getType()+":"+favoriteRequest.getUid()+":"+favoriteRequest.getId();
		//先从缓存获取是否收藏
		String isFavoriteCache=memecacheClientHolder.getLevel1Cache().get(favoriteKey, String.class);
		if(StringUtils.isNotEmpty(isFavoriteCache))
		{	
			logger.info("get isFavorite from memecache. isFavoriteCache is {}, uid is {}", isFavoriteCache, favoriteRequest.getUid());
			isFavorite = (isFavoriteCache.equals("Y") ? true:false);
			ApiResponse response = new ApiResponse.ApiResponseBuilder().message(message).data(isFavorite).build();
			return response;
		}
		
		//1s之内不给请求就直接返回,不能死等
		try {
			isFavorite = serviceCaller.asyncCall("product.isFavorite", favoriteRequest,Boolean.class).get(1);
		} catch (Throwable e) {
			// 捕捉异常，让流程继续走下去
			logger.warn("call product.isFavorite fail uid:{},productId:{}",favoriteRequest.getUid(), favoriteRequest.getId(), e);
			isFavorite = false;
		}
		//缓存5分钟
		memecacheClientHolder.getLevel1Cache().set(favoriteKey, 300, (isFavorite ? "Y" : "N"));
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(message).data(isFavorite).build();
		return response;
	}
	
	
	/**
	 * 批量是否收藏
	 * @param favoriteRequest
	 * @return
	 * @throws GatewayException 
	 */
	@RequestMapping(params = "method=app.favorite.batchCheckIsFavorite")
	@ResponseBody
	public ApiResponse batchCheckIsFavorite(FavoriteRequest favoriteRequest) throws GatewayException{
		
		if (null == favoriteRequest || null == favoriteRequest.getUid()|| null == favoriteRequest.getType()
				|| CollectionUtils.isEmpty(favoriteRequest.getFavIds()))
		{
			throw new GatewayException(500, "param can't be empty");
		}
		FavoriteWrapper[] favoriteWrappers= serviceCaller.call("product.batchCheckIsFavorite", favoriteRequest,FavoriteWrapper[].class);
		String favoriteType = favoriteRequest.getType();
		String message = "";
		if (FavoriteProductParams.TYPE_PRODUCT.equals(favoriteType)){
			message = FAVORITE_PRODUCT_LIST;
		}
		if (FavoriteProductParams.TYPE_BRAND.equals(favoriteType)){
			message = FAVORITE_BRAND_LIST;
		}
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(message).data(favoriteWrappers).build();
		return response;
	}
	
	
	/**
	 * 添加收藏
	 * @param vo
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=app.favorite.add")
	@ResponseBody
	public ApiResponse addFavorite(FavoriteRequest vo) throws ServiceException {
		logger.info("Begin call addFavorite gateway. with param is {}", vo.toString());
		final String key="yh:gw:userFavoriteProduct:"+vo.getUid();
		cacheClient.delete(key);
		String favoriteKey=null;
        // product缓存key改变了
		if ("product".equals(vo.getType())) {
			favoriteKey = "yh:gw:favorite:product:"+vo.getUid();
		} else {
			favoriteKey="yh:gw:favorite:"+vo.getType()+":"+vo.getUid()+":"+vo.getId();
		}
		cacheClient.delete(favoriteKey);
		
		Integer result = serviceCaller.call("product.addFavorite", vo, Integer.class);
		logger.info("call product.brand with param is {}, with result is {}", vo, result);
		JSONObject obj = new JSONObject();
		obj.put("fav_id", result);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(ADD_FAVORITE_SUCCESS).data(obj).build();
		return response;
	}

	/**
	 * 取消用户收藏
	 * @param vo
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=app.favorite.cancel")
	@ResponseBody
	public ApiResponse cancelFavorite(FavoriteRequest vo) throws ServiceException {
		logger.info("Begin call cancel Favorite gateway. with param is {}:", vo);
		final String key="yh:gw:userFavoriteProduct:"+vo.getUid();
		cacheClient.delete(key);

		String favoriteKey=null;

        // product缓存key改变了
        if ("product".equals(vo.getType())) {
            favoriteKey = "yh:gw:favorite:product:"+vo.getUid();
        } else {
            favoriteKey="yh:gw:favorite:"+vo.getType()+":"+vo.getUid()+":"+vo.getFav_id();
        }
		cacheClient.delete(favoriteKey);
		
		VoidResponse result = serviceCaller.call("product.cancelFavorite", vo, VoidResponse.class);
		//删除已订阅的商品降价通知,这里给pc的要拆开
		if("product".equals(vo.getType())){
			PriceReductionRequest req = new PriceReductionRequest();
			req.setUid(vo.getUid());
			req.setIds( Lists.newArrayList(vo.getFav_id()));
			try
			{
				serviceCaller.call("users.cancelPriceReduction", req, Integer.class);
			}
			catch(Exception e)
			{
				//donothing
			}
		}
		logger.info("call product.brand with param is {}, with result is {}:", vo, result);
		if(result.isSuccess())
		{
			return new ApiResponse.ApiResponseBuilder().message(CANCEL_FAVORITE_SUCCESS).build();
		}else
		{
			return new ApiResponse.ApiResponseBuilder().message(CANCEL_FAVORITE_FAILED).build();
		}
	}
}
