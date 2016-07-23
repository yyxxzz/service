package com.yoho.gateway.controller.product.web;


import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.cache.CacheClient;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.product.convert.FavoriteAssistConvert;
import com.yoho.gateway.model.request.ProductFavoriteReqVO;
import com.yoho.gateway.service.favorite.FavoriteService;
import com.yoho.product.constants.FavoriteProductParams;
import com.yoho.product.request.FavoriteRequest;
import com.yoho.product.response.VoidResponse;
import com.yoho.service.model.request.PriceReductionRequest;

/**
 * Created by caoyan
 */
@Controller
public class WebFavoriteProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebFavoriteProductController.class);

    private static final String FAVORITE_PRODUCT_LIST = "favorite product list";
    
    private static final String CANCEL_FAVORITE_SUCCESS = "取消收藏成功";
	
	private static final String CANCEL_FAVORITE_FAILED = "取消收藏失败";
    
    @Autowired
    private ServiceCaller serviceCaller;
    
    @Autowired
	FavoriteService favoriteService;

	@Autowired
	private CacheClient cacheClient;
    
    @RequestMapping(params = "method=web.favorite.product")
	@ResponseBody
	public ApiResponse product(ProductFavoriteReqVO vo) throws ServiceException {
    	LOGGER.info("Begin call web.favorite.product. with param ProductFavoriteReqVO is {}", vo);
		Map<String, Object> map = favoriteService.queryWebFavoriteProductList(FavoriteAssistConvert.converToBo(vo, FavoriteProductParams.TYPE_PRODUCT));
		
		LOGGER.debug("call users.product with param is {}, with result is {}", vo, map);
		
		LOGGER.info("queryFavoriteProductList call success.");
		
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(FAVORITE_PRODUCT_LIST).data(map).build();
		return response;
	}
    
    /**
	 * 取消用户收藏
	 * @param vo
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=web.favorite.cancel")
	@ResponseBody
	public ApiResponse cancelFavorite(FavoriteRequest vo) throws ServiceException {
		LOGGER.info("Begin call cancel Favorite gateway. with param is {}", vo.toString());
		VoidResponse result = serviceCaller.call("product.batchCancelFavorite", vo, VoidResponse.class);
		
		//删除已订阅的商品降价通知
		if("product".equals(vo.getType())){
			// 删掉用户收藏的商品的缓存
			String favoriteKey = "yh:gw:favorite:product:"+vo.getUid();
			cacheClient.delete(favoriteKey);
			PriceReductionRequest req = new PriceReductionRequest();
			req.setUid(vo.getUid());
			req.setIds(vo.getFavIds());
			serviceCaller.call("users.cancelPriceReduction", req, Integer.class);
		}
		
		LOGGER.info("call product.batchCancelFavorite with param is {}, with result is {}", vo, result);
		if(result.isSuccess())
		{
			return new ApiResponse.ApiResponseBuilder().message(CANCEL_FAVORITE_SUCCESS).build();
		}else
		{
			return new ApiResponse.ApiResponseBuilder().message(CANCEL_FAVORITE_FAILED).build();
		}
	}
    
}
