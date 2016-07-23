package com.yoho.gateway.controller.promotion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.promotion.PromotionCodeVo;
import com.yoho.service.model.promotion.PromotionCodeBo;
import com.yoho.service.model.promotion.request.PromotionCodeReq;

/**
 * 优惠码控制类
 * @author caoyan
 *
 */
@Controller
public class PromotionCodeController {
	static Logger logger = LoggerFactory.getLogger(PromotionCodeController.class);
	
	@Autowired
	private ServiceCaller serviceCaller;
	
	/**
	 * 查询优惠码详情
	 * @param uid
	 * @param couponId
	 * @return
	 */
	@RequestMapping(params = "method=app.promotion_code.get")
	@ResponseBody
	public ApiResponse getPromotionCode(@RequestParam(value = "uid") Integer uid,
								  @RequestParam(value = "promotionCode") String promotionCode) {
        logger.info("Method getPromotionCode in. uid is {}, promotionCode is {}", uid, promotionCode);
		
		PromotionCodeReq req = new PromotionCodeReq();
		req.setPromotionCode(promotionCode);
		req.setUid(uid);
		PromotionCodeBo promotionCodeBo = serviceCaller.call("promotion.getPromotionCode", req, PromotionCodeBo.class);
		
		PromotionCodeVo codeVo = convertBoToVo(promotionCodeBo);
		logger.info("result data is {}", JSON.toJSONString(codeVo));
		return new ApiResponse.ApiResponseBuilder().code(200).message("getPromotionCode success.").data(codeVo).build();

	}
	
	private PromotionCodeVo convertBoToVo(PromotionCodeBo codeBo){
		PromotionCodeVo codeVo = new PromotionCodeVo();
		codeVo.setId(codeBo.getId());
		codeVo.setCode(codeBo.getCode());
		codeVo.setName(codeBo.getName());
		codeVo.setLimitTimes(codeBo.getLimitTimes());
		codeVo.setDiscountType(codeBo.getDiscountType());
		codeVo.setAmountAtLeast(codeBo.getAmountAtLeast());
		codeVo.setCountAtLeast(codeBo.getCountAtLeast());
		codeVo.setDiscount(codeBo.getDiscount());
		codeVo.setDiscountAtMost(codeBo.getDiscountAtMost());
		codeVo.setStatus(codeBo.getStatus());
		
		return codeVo;
	}
}
