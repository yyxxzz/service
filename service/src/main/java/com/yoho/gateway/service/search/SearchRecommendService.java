package com.yoho.gateway.service.search;

import java.util.List;

import com.yoho.gateway.model.search.SearchRecommendVo;


/**
 * 搜索推荐查询接口
 * @author mali
 *
 */
public interface SearchRecommendService {
	/**
	 * 
	 * @param keyword   关键词
	 * @return 搜索关键词结果
	 */
	List<SearchRecommendVo> searchRecommendList(String keyword);
}
