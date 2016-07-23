package com.yoho.gateway.controller.search;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.ProductSearchService;


/**
 * 
 * @author lixuxin
 *
 */
@Controller
public class BreakingProductCategoryController {
	private static final Logger LOGGER = LoggerFactory.getLogger(BreakingProductCategoryController.class);
	
	@Autowired
	private ProductSearchService categoryProductSearchService;
	
	/**
	 * 查询断码区分类和尺码
	 * @gender  性别
	 * @yhChannel 
	 */
	@RequestMapping(params = "method=app.sale.getBreakingSort")
	@ResponseBody
	@Cachable(expire=60)
	public ApiResponse querySaleBreakingSort(@RequestParam("yh_channel")String yhChannel) {
		LOGGER.info("begin invoke method=app.sale.getBreakingSort");
		ProductSearchReq req = new ProductSearchReq().setBreaking("1").setYhChannel(yhChannel).setStocknumber(1).setSearchFrom("search.getBreakingSort");
		JSONArray data = categoryProductSearchService.searchSaleBreakingSort(req);
		return new ApiResponse.ApiResponseBuilder().code(200).message("getBreakingSort List.").data(data).build();
	}
}
