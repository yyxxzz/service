package com.yoho.gateway.controller.search;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.product.HotRankTagVo;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.HotRankTagService;
import com.yoho.gateway.service.search.ProductSearchService;
import com.yoho.gateway.utils.DateUtil;

/**
 * 热卖排行榜top100
 * @author wangshusheng
 *
 */
@Controller
public class TopProductSearchController {
	/**
	 * LOG
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(TopProductSearchController.class);
	
	@Autowired
	private ProductSearchService categoryProductSearchService;
	
	@Autowired
	private HotRankTagService hotRankTagService;
	
	/**
	 * 
	 * @param yhChannel 频道
	 * @param limit 最大限制条数
	 * @param page 当前page
	 * @param tab_id
	 * @param sort 分类ID
	 * @param client_type
	 * @param gender
	 * @return
	 */
	@RequestMapping(params = "method=app.search.top")
	@ResponseBody
	@Cachable(expire=600)
	public ApiResponse searchTopProductList(
			@RequestParam(value = "yh_channel", required = false)String yhChannel, 
			@RequestParam(value = "limit", required = false,defaultValue="50") Integer limit,
			@RequestParam(value = "page", required = false,defaultValue="1") Integer page,
			@RequestParam(value = "tab_id", required = false) String tab_id,
			@RequestParam(value = "sort", required = false)String sort,
			@RequestParam(value = "client_type", required = false)String client_type,
			@RequestParam(value = "gender", required = false) String gender,
            @RequestParam(value = "app_version", required = false) String appVersion,
            @RequestParam(value = "client_type", required = false) String clientType) {
		LOGGER.info("searchTopProductList method=app.search.top in. yhChannel:{}, limit:{}, page:{}, gender:{},sort:{},tab_id:{}",
				new Object[]{yhChannel, limit, page, tab_id, sort, gender});
		// 1. 查询热门标签
		List<HotRankTagVo> hotRankTagList = hotRankTagService.getTagsList(yhChannel, client_type);
		
		// 2. 调搜索接口查询热门商品
		ProductSearchReq productReq = new ProductSearchReq().setLimit(limit).setYhChannel(yhChannel).setPage(page).setSort(sort).setClientType(clientType)
				.setFirstShelveTime(DateUtil.getIntervalTimeSecond(-30) + "," + DateUtil.getCurrentTimeSecond()).setOrder("s_n_desc").setAppVersion(appVersion).setSearchFrom("search.top");
		
		JSONObject productListObject = categoryProductSearchService.searchTopProductList(productReq);
		if(null!=productListObject)
		{
			productListObject.put("tabs", hotRankTagList);
			productListObject.put("tab_id", null != tab_id ? tab_id : 1);
		}
		return new ApiResponse.ApiResponseBuilder().code(200).message("Search List.")
				.data(productListObject).build();
	}
}
