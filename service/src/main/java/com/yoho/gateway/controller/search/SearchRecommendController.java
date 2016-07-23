package com.yoho.gateway.controller.search;

import java.util.List;

import com.yoho.gateway.cache.expire.product.ExpireTime;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.search.SearchRecommendVo;
import com.yoho.gateway.service.search.SearchRecommendService;
import com.yoho.gateway.utils.StripTagsUtil;

/**
 * app.search.fuzzy
 * 搜索建议
 * @author mali
 *
 */
@Controller
public class SearchRecommendController {
	/**
	 * LOG
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchRecommendController.class);
	
	@Autowired
	private SearchRecommendService searchRecommendService;
	
	/**
	 * 
	 * @param keyword   关键词   如果为空或非0则返回“关键词不能为空”的提示
	 * @param yhChannel	渠道
	 * @param userId	用户ID
	 * @param gender	性别
	 * @return 搜索关键词推荐请求
	 */
	@RequestMapping(params = "method=app.search.fuzzy")
	@ResponseBody
	@Cachable(needMD5=true, expire = ExpireTime.app_search_fuzzy)
	public ApiResponse searchRecommendList(@RequestParam(value = "keyword", required = false)String keyword) {
		LOGGER.info("fuzzyQueryProductList method=app.search.fuzzy in. keyword : {}", keyword);
		
		// 如果关键词为空，则返回“关键词不能为空”的提示
		if (StringUtils.isEmpty(keyword)) {
			return new ApiResponse.ApiResponseBuilder().code(200).message("搜索词为空.").build();
		}
		// 调用检索的推荐搜索接口，查询推荐列表
		List<SearchRecommendVo> searchRecommendList = searchRecommendService.searchRecommendList(StripTagsUtil.parse(keyword));
		
		return new ApiResponse.ApiResponseBuilder().code(200).message("fuzzy search list")
				.data(searchRecommendList).build();
	}
}
