package com.yoho.gateway.controller.guang;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.ArticlePraiseReqVO;
import com.yoho.service.model.sns.SnsServices;
import com.yoho.service.model.sns.request.ArticlePraiseReqBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping(value = "/guang/api/*/praise")
public class ArticlePraiseController {

	private Logger logger = LoggerFactory.getLogger(ArticlePraiseController.class);
	
	@Resource
	ServiceCaller serviceCaller;
	
	/**
	 * 文章点赞
	 * @param ArticlePraiseReqVO 点赞请求信息
	 * @return ApiResponse 返回信息
	 */
	@RequestMapping(value = "/setPraise")
	@ResponseBody
	public ApiResponse setPraise(ArticlePraiseReqVO vo) throws GatewayException {
		logger.debug("Begin call sns.setPraise gateway. with param praiseReqVO is {}", vo);
		
		ArticlePraiseReqBO bo = new ArticlePraiseReqBO();
		BeanUtils.copyProperties(vo, bo);
		bo.setArticleId(vo.getArticle_id());
		int result = serviceCaller.call(SnsServices.setPraise, bo, Integer.class);
		logger.debug("call guang.setPraise with param is {}, with result is {}", vo, result);
		logger.debug("call sns.setPraise with param is {}, with result is {}", vo, result);
		return new ApiResponse.ApiResponseBuilder().code(200).message("success").data(result).build();
	}
	
	/**
	 * 取消文章点赞
	 * @param ArticlePraiseReqVO 点赞请求信息
	 * @return ApiResponse 返回信息
	 */
	@RequestMapping(value = "/cancel")
	@ResponseBody
	public ApiResponse cancel(ArticlePraiseReqVO vo) throws GatewayException {
		logger.debug("Begin call sns.cancelPraise gateway. with param praiseReqVO is {}", vo);
		
		ArticlePraiseReqBO bo = new ArticlePraiseReqBO();
		BeanUtils.copyProperties(vo, bo);
		bo.setArticleId(vo.getArticle_id());
		int result = serviceCaller.call("sns.cancelArticlePraise", bo, Integer.class);
		logger.debug("call sns.cancelPraise with param is {}, with result is {}", vo, result);
		return new ApiResponse.ApiResponseBuilder().code(200).message("success").data(result).build();
	}
}
