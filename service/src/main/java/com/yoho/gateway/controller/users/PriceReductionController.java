package com.yoho.gateway.controller.users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.service.model.profile.PriceReductionBo;
import com.yoho.service.model.profile.SubcribeInfoBo;
import com.yoho.service.model.request.PriceReductionRequest;
/**
 * 
 * @author lixuxin
 *
 */
@Controller
public class PriceReductionController {
	@Resource
	ServiceCaller serviceCaller;

	private static final Logger logger = LoggerFactory.getLogger(PriceReductionController.class);
	/**
	 * 新增订阅信息
	 * @param uid
	 * @param productId
	 * @param mobile
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.redution.add")
	@ResponseBody
	public ApiResponse addPriceReduction(@RequestParam(defaultValue = "0") Integer uid 
			,@RequestParam(defaultValue = "0") Integer productId
			,@RequestParam(defaultValue = "0") String mobile) throws GatewayException {
		logger.info("Begin call getMyComlaintCount. with param uid is {}", uid);
		if(null==uid||uid<1){
			throw new GatewayException(500,"uid must bu not null");
		}
		if(null==productId||productId<1){
			throw new GatewayException(500,"productId must bu not null");
		}
		if(StringUtils.isEmpty(mobile)){
			throw new GatewayException(500,"mobile must bu not null");
		}
		PriceReductionRequest req=new PriceReductionRequest();
		req.setMobile(mobile);
		req.setProductId(productId);
		req.setUid(uid);
		Integer count = serviceCaller.call("users.getReductionCount", req, Integer.class);
		if(count!=null&&count>=5){
			return new ApiResponse.ApiResponseBuilder().code(500).message("count must be  lt 5").build();
		}
		PriceReductionBo priceReductionBo = serviceCaller.call("users.addPriceReduction", req, PriceReductionBo.class);
		if(priceReductionBo.getStatus()!=null&&(int)priceReductionBo.getStatus()==1){
			return new ApiResponse.ApiResponseBuilder().code(500).message("该商品已设置降价通知！").build();
		}
		Map<String, Object> result=new HashMap<String, Object>();
		result.put("num", priceReductionBo.getCount());
		result.put("product_skns", priceReductionBo.getList());
		return new ApiResponse.ApiResponseBuilder().code(200).message("web.redution.add query sucessed.").data(result).build();
	}
	/**
	 * 根据uid统计该用户订阅的商品数量
	 * @param uid
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.redution.count")
	@ResponseBody
	public ApiResponse getPriceReductionCount(@RequestParam(defaultValue = "0") Integer uid ) throws GatewayException {
		logger.info("Begin call getPriceReductionCount. with param uid is {}", uid);
		if(null==uid||uid<1){
			throw new GatewayException(500,"uid must bu not null");
		}
		PriceReductionRequest req=new PriceReductionRequest();
		req.setUid(uid);
		Integer num = serviceCaller.call("users.getReductionCount", req, Integer.class);
		SubcribeInfoBo subcribeInfoBo = serviceCaller.call("users.getReductionMobile", req, SubcribeInfoBo.class);
		Map<String, Object> result=new HashMap<String, Object>();
		result.put("num", num);
		result.put("mobile", null == subcribeInfoBo ? "" : subcribeInfoBo.getMobile());
		return new ApiResponse.ApiResponseBuilder().code(200).message("web.redution.count query sucessed.").data(result).build();
	}
	/**
	 * 取消降价订阅
	 * @param uid
	 * @param productId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.redution.cancel")
	@ResponseBody
	public ApiResponse cancelPriceReductionCount(@RequestParam Integer uid ,@RequestParam int[] productIds) throws GatewayException {
		logger.info("Begin call cancelPriceReductionCount. with param uid is {}", uid);
		if(uid==null||uid<1){
			throw new GatewayException(500,"uid must bu not null");
		}
		if(null==productIds||productIds.length<1){
			throw new GatewayException(500,"productId must bu not null");
		}
		PriceReductionRequest req=new PriceReductionRequest();
		
		List<Integer> ids=Lists.newArrayList();
		for(Integer id:productIds){
			ids.add(id);
		}
		req.setUid(uid);
		req.setIds(ids);
		Integer num = serviceCaller.call("users.cancelPriceReduction", req, Integer.class);
		Map<String, Object> result=new HashMap<String, Object>();
		result.put("num", num);
		return new ApiResponse.ApiResponseBuilder().code(200).message("web.redution.cancel query sucessed. ").data(result).build();
	}
	

}
