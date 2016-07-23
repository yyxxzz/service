package com.yoho.gateway.controller.product.web;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.product.convert.FavoriteAssistConvert;
import com.yoho.gateway.model.product.ShowProductFavoriteVo;
import com.yoho.gateway.model.request.ProductFavoriteReqVO;
import com.yoho.gateway.service.favorite.FavoriteService;
import com.yoho.product.constants.FavoriteProductParams;
import com.yoho.service.model.request.UserSsoReqBO;
import com.yoho.service.model.response.SsoUserRelationRspBO;

/**
 * app show频道收藏
 * Created by wangshusheng
 */
@Controller
public class ShowFavoriteProductController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShowFavoriteProductController.class);

    private static final String FAVORITE_PRODUCT_LIST = "favorite product list";
    
    @Autowired
    private ServiceCaller serviceCaller;
    
    @Autowired
	FavoriteService favoriteService;
    
	/**
	 * show频道查询用户收藏
	 * @param vo
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=cn.favorite.li")
	@ResponseBody
	public ApiResponse favoriteList(ProductFavoriteReqVO vo) throws ServiceException {
    	LOGGER.info("Begin call cn.favorite.product. with param ProductFavoriteReqVO is {}", vo);
    	
    	List<ShowProductFavoriteVo> productFavoriteVo = favoriteService.queryShowFavoriteProductList(FavoriteAssistConvert.converToBo(vo, FavoriteProductParams.TYPE_PRODUCT));
		
		LOGGER.info("favoriteList call success.");
		
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("favorite list").data(productFavoriteVo).build();
		return response;
	}
	
	/**
	 * show频道查询用户收藏
	 * @param vo
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(params = "method=cn.favorite.product")
	@ResponseBody
	public ApiResponse showFavoriteProductList(ProductFavoriteReqVO vo) throws ServiceException {
    	LOGGER.info("Begin call cn.favorite.product. with param ProductFavoriteReqVO is {}", vo);
    	if (null == vo || 0 == vo.getSso_uid()) {
			return new ApiResponse(404, "Sso_uid is error", null);
		}
    	// 调user接口，根据sso_uid查询uid
    	UserSsoReqBO req = new UserSsoReqBO();
    	req.setSso_uid(vo.getSso_uid());
    	SsoUserRelationRspBO result = serviceCaller.call("users.getUserBySsoUid", req, SsoUserRelationRspBO.class);
		if(result==null){
			return new ApiResponse(404, "get uid by sso_uid Is Null", null);
		}
		vo.setUid(result.getUid());
		if(vo.getLimit()>100){
			vo.setLimit(100);
		}
    	List<ShowProductFavoriteVo> productFavoriteVo = favoriteService.queryShowFavoriteProductList(FavoriteAssistConvert.converToBo(vo, FavoriteProductParams.TYPE_PRODUCT));
		
		LOGGER.info("favoriteList call success, uid:{}", result.getUid());
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(FAVORITE_PRODUCT_LIST).data(productFavoriteVo).build();
		return response;
	}
}
