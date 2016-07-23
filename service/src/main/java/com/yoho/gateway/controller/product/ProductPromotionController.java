package com.yoho.gateway.controller.product;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.product.CallBackChain.PromotionActityWrapper;
import com.yoho.gateway.controller.product.builder.ProductCallbackBuilder;
import com.yoho.product.model.PromotionBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.service.model.promotion.PointActivityInfoBo;
import com.yoho.service.model.promotion.request.PointActivityReq;

@Controller
public class ProductPromotionController {
	
	private final Logger logger = LoggerFactory.getLogger(ProductPromotionController.class);
	
	@Autowired
	private ServiceCaller serviceCaller;

	@Autowired
	private MemecacheClientHolder memecacheClientHolder;
	
	@RequestMapping(params = "method=app.product.promotion")
	@ResponseBody
	public ApiResponse queryProductPromotionListBySkn(@RequestParam(value = "product_skn", required = true) Integer productskn)
	{
		logger.info("queryProductPromotionListBySkn method=app.product.promotion productskn is:{}",productskn);
		final String key="yh:gw:product:promotionBoList:"+productskn;
		PromotionActityWrapper promotionActityWrapper=memecacheClientHolder.getLevel1Cache().get(key, PromotionActityWrapper.class);
		if(null!=promotionActityWrapper){	
			return new ApiResponse.ApiResponseBuilder().data(promotionActityWrapper.getPromotionBoList()).code(200).message("product promotionInfo").build();
		}
		try{	
			BaseRequest<Integer> request=new BaseRequest<Integer>();
			request.setParam(productskn);
			AsyncFuture<PromotionBo[]> promotionBoFuture=serviceCaller.asyncCall("product.queryProductFitPromotionListBySkn", request, PromotionBo[].class);
			AsyncFuture<PointActivityInfoBo[]> pointActivityInfoBoFuture=serviceCaller.asyncCall("promotion.queryPointActivity", new PointActivityReq(), PointActivityInfoBo[].class);
			List<PromotionBo> promotionBoList=ProductCallbackBuilder.buildPromotion(promotionBoFuture.get(1),pointActivityInfoBoFuture.get(1));
			memecacheClientHolder.getLevel1Cache().set(key, 180, new PromotionActityWrapper(promotionBoList));
			return new ApiResponse.ApiResponseBuilder().data(promotionBoList).code(200).message("product promotionInfo").build();

		}catch(Throwable e)
		{
			// 捕捉异常，让流程继续走下去
			logger.warn("call promotion.queryPromotionActivity failed!!!!!",e);
			return new ApiResponse.ApiResponseBuilder().data(Lists.newArrayList()).code(200)
					.message("product promotionInfo").build();
		}
	}
}
