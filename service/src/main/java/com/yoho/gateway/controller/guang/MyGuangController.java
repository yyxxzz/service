package com.yoho.gateway.controller.guang;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.MyGuangVO;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.sns.SnsServices;
import com.yoho.service.model.sns.request.MyGuangReqBO;
import com.yoho.service.model.sns.response.MyGuangPageRspBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = {"/guang/api/*/favorite", "/guang/api/*/Favorite"})
public class MyGuangController {

	static Logger logger = LoggerFactory.getLogger(MyGuangController.class);
	
	// 获取用户收藏的咨询列表
//	private static final String USERFAVART_LIST_SERVICE = "sns.getUserFavArticleList";
	
	//取消用户收藏服务
//	private static final String SETFAVORITE_SERCVICE = "sns.setFavorite";
		
	//取消用户收藏服务
//	private static final String CANCLEFAVORITE_SERCVICE = "sns.cancelFavorite";
	
	//获取用户收藏的列表成功的code和message
	private static final int SUCCESS_LIST_CODE = 200;
	private static final String USER_ARTICLE_LIST = "收藏列表";
	
	//收藏成功的code和message
	private static final int SUCCESS_SET_CODE = 200;
	private static final String SUCCESS_SET_MSG = "收藏成功";
		
	//取消收藏成功的code和message
	private static final int SUCCESS_CANCLE_CODE = 200;
	private static final String CANCLE_FAVO_MSG = "取消收藏成功";
	
	@Resource
	ServiceCaller serviceCaller;
	
	/**
	 * 用户收藏资讯信息
	 * 缓存时间10秒
	 * @param MyGuangVO
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/getUserFavArticleList")
	@ResponseBody
	@Cachable(expire = 5, needMD5 = true)
	public ApiResponse getUserFavArticleList(MyGuangVO vo) throws GatewayException{
		logger.debug("Begin call MyGuangController.getUserFavArticleList gateway. with param MyGuangVO is {}", vo);
		MyGuangReqBO bo = new MyGuangReqBO();
		BeanUtils.copyProperties(vo, bo);
		MyGuangPageRspBO result = serviceCaller.call(SnsServices.getUserFavArticleList, bo, MyGuangPageRspBO.class);
		logger.debug("call sns.getUserFavArticleList with param is {}, with result is {}", vo, result);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("data", result.getMyGuangRspBO());
		jsonObject.put("total", result.getTotal());
		jsonObject.put("totalPage", result.getTotalPage());
		jsonObject.put("page", result.getPage());
		return new ApiResponse.ApiResponseBuilder().code(SUCCESS_LIST_CODE).message(USER_ARTICLE_LIST).data(jsonObject).build();
	}
	
	/**
	 * 收藏资讯
	 * @param MyGuangVO
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/setFavorite")
	@ResponseBody
	public ApiResponse setFavorite(MyGuangVO vo) throws GatewayException{
		logger.debug("Begin call MyGuangController.setFavorite gateway. with param MyGuangVO is {}", vo);
		MyGuangReqBO bo = new MyGuangReqBO();
		BeanUtils.copyProperties(vo, bo);
		serviceCaller.call(SnsServices.setFavorite, bo, CommonRspBO.class);
		logger.debug("call sns.setFavorite with param is {}", vo);
		return new ApiResponse.ApiResponseBuilder().code(SUCCESS_SET_CODE).message(SUCCESS_SET_MSG).build();
	}
	
	/**
	 * 取消用户收藏
	 * @param MyGuangVO
	 * @return
	 * @throws ServiceException
	 */
	@RequestMapping(value = "/cancelFavorite")
	@ResponseBody
	public ApiResponse cancelFavorite(MyGuangVO vo) throws GatewayException{
			logger.debug("Begin call MyGuangController.cancelFavorite gateway. with param MyGuangVO is {}", vo);
		MyGuangReqBO bo = new MyGuangReqBO();
		BeanUtils.copyProperties(vo, bo);
		serviceCaller.call(SnsServices.cancelFavorite, bo, CommonRspBO.class);
		logger.debug("call sns.cancelFavorite with param is {}", vo);
		return new ApiResponse.ApiResponseBuilder().code(SUCCESS_CANCLE_CODE).message(CANCLE_FAVO_MSG).build();
	}
	
}
