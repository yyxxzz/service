package com.yoho.gateway.service.search;

/**
 * 搜索关键词相关接口
 * @author mali
 *
 */
public interface KeywordsService {
	/**
	 * 将搜索关键词入搜索记录表
	 * @param queryNew 去html预定便签的关键词
	 * @param mobileInitPlatform 平台标识
	 */
	void saveKeyWords(String queryNew, String mobileInitPlatform);
}
