package com.yoho.gateway.controller.users;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.user.percenter.GiftExchangeReqVO;
import com.yoho.service.model.request.GiftExchangeReqBO;
import com.yoho.service.model.response.CommonRspBO;

/**
 * 兑换礼品卡
 * @author yoho
 *
 */
@Controller
public class GiftExchangeController {
	
	private Logger logger = LoggerFactory.getLogger(GiftExchangeController.class);
	
	@Resource
	ServiceCaller serviceCaller;
	
	@RequestMapping(params="method=web.personCen.giftExchange")
	@ResponseBody
	public ApiResponse giftExchange(GiftExchangeReqVO vo)throws ServiceException{
		logger.info("GiftExchangeController.giftExchange params is{}",vo);
		if(StringUtils.isEmpty(vo.getGiftCardCode1())||StringUtils.isEmpty(vo.getGiftCardCode2())||StringUtils.isEmpty(vo.getGiftCardCode3())){
			throw new ServiceException(ServiceError.GIFT_CODE_IS_NULL);
		}
		if(StringUtils.isEmpty(vo.getCaptchaCode())){
			throw new ServiceException(ServiceError.CAPTCHA_CODE_IS_NULL);
		}
		if(StringUtils.isEmpty(vo.getUid())){
			throw new ServiceException(ServiceError.UID_IS_NULL);
		}
		GiftExchangeReqBO giftExchangeReqbo = new GiftExchangeReqBO();
		giftExchangeReqbo.setGiftCardCode1(vo.getGiftCardCode1());
		giftExchangeReqbo.setGiftCardCode2(vo.getGiftCardCode2());
		giftExchangeReqbo.setGiftCardCode3(vo.getGiftCardCode3());
		giftExchangeReqbo.setCaptchaCode(vo.getCaptchaCode());
		giftExchangeReqbo.setUid(vo.getUid());
		
		CommonRspBO commonRsp = serviceCaller.call("users.exchangeAction", giftExchangeReqbo, CommonRspBO.class);
		return new ApiResponse.ApiResponseBuilder().data(commonRsp).build();
		
	}
}
