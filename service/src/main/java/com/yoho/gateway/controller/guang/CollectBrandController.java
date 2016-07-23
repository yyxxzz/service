package com.yoho.gateway.controller.guang;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.sns.CollectBrandContReqVO;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.sns.SnsServices;
import com.yoho.service.model.sns.request.CollectBrandReqBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 明星原创, 潮流优选,进入品牌详情, 点赞,取消点赞
 *
 */
@Controller
@RequestMapping(value = "/guang")
public class CollectBrandController {

	private static Logger logger = LoggerFactory.getLogger(CollectBrandController.class);

	// 收藏成功的code和message
	private static final int SUCCESS_SET_CODE = 200;
	private static final String SUCCESS_SET_MSG = "success";

	@Resource
	ServiceCaller serviceCaller;

	/**
	 * 逛明星原创品牌收藏/取消收藏
	 * 
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/service/*/favorite/toggleBrand")
	@ResponseBody
	public ApiResponse toggleBrand(CollectBrandContReqVO vo) throws GatewayException{
		logger.debug("enter CollectBrandController.setFavorite params is {} ", vo);
		CollectBrandReqBO bo = new CollectBrandReqBO();
		if(StringUtils.isEmpty(vo.getUid())){
			throw new ServiceException(ServiceError.UID_IS_NULL);
		}
		bo.setUid(vo.getUid());
		if(StringUtils.isEmpty(vo.getBrand_id())){
			throw new ServiceException(ServiceError.BRAND_NOT_EXISTS);
		}
		bo.setBrand_id(vo.getBrand_id());
		serviceCaller.call(SnsServices.toggleBrand, bo, CommonRspBO.class);
		return new ApiResponse.ApiResponseBuilder().code(SUCCESS_SET_CODE).message(SUCCESS_SET_MSG).build();
	}

	/**
	 * 逛明星原创品牌收藏/取消收藏
	 *
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/api/*/favorite/togglebrand")
	@ResponseBody
	public ApiResponse apiToggleBrand(CollectBrandContReqVO vo) throws GatewayException{
		logger.debug("enter CollectBrandController.setFavorite params is {} ", vo);
		CollectBrandReqBO bo = new CollectBrandReqBO();
		if(StringUtils.isEmpty(vo.getUid())){
			throw new ServiceException(ServiceError.UID_IS_NULL);
		}
		bo.setUid(vo.getUid());
		if(StringUtils.isEmpty(vo.getBrand_id())){
			throw new ServiceException(ServiceError.BRAND_NOT_EXISTS);
		}
		bo.setBrand_id(vo.getBrand_id());
		serviceCaller.call(SnsServices.toggleBrand, bo, CommonRspBO.class);
		return new ApiResponse.ApiResponseBuilder().code(SUCCESS_SET_CODE).message(SUCCESS_SET_MSG).build();
	}

}