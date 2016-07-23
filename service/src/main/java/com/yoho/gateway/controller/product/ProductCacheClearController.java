package com.yoho.gateway.controller.product;

import java.util.List;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.service.product.ProductCacheClearService;

@Controller
public class ProductCacheClearController {
	
	static final Logger logger = LoggerFactory.getLogger(ProductCacheClearController.class);
	
	@Autowired
	private ProductCacheClearService productCacheClearService;
	
	@RequestMapping(params = "method=clear.productCache")
	@ResponseBody
	public ApiResponse clearProductCacheBySkn(@RequestParam(value = "productSkn", required = true) Integer productSkn)
	{	
		logger.info("into clearProductCacheBySkn method=clear.productCache productSkn is:{}", productSkn);
		if(null==productSkn)
		{
			return new ApiResponse.ApiResponseBuilder().data("clear failed!!").code(404)
					.message("clear product cache").build();
		}
		
		try {
			productCacheClearService.clearProductCacheBySkn(productSkn);
		} catch (Exception e) {
			logger.error("clearProductCache error with productSkn={}", productSkn, e);
		}
		
		logger.info("out clearProductCacheBySkn method=clear.productCache productSkn is:{}", productSkn);
		return new ApiResponse.ApiResponseBuilder().data("clear OK!!").code(200)
				.message("clear product cache").build();
	}
	
	@RequestMapping("/erp/clear/batch/productCache")
	@ResponseBody
	public ApiResponse clearBatchProductCacheBySkn(@RequestBody List<Integer> productSkns)
	{
		logger.info("into clearBatchProductCacheBySkn with productSkns={}", productSkns);
		if(CollectionUtils.isEmpty(productSkns)){
			return new ApiResponse.ApiResponseBuilder().data("clear OK!!").code(200)
					.message("clear product cache").build();
		}
		
		productCacheClearService.clearBatchProductCacheBySkn(productSkns);
		logger.info("out clearProductCache with productSkns={}", productSkns);
		return new ApiResponse.ApiResponseBuilder().data("clear OK!!").code(200)
				.message("clear product cache").build();
	}
	
}
