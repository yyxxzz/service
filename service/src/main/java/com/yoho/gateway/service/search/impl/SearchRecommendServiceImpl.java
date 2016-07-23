package com.yoho.gateway.service.search.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.gateway.model.search.SearchRecommendVo;
import com.yoho.gateway.service.search.SearchRecommendService;
import com.yoho.gateway.service.search.wrapper.SearchRestTemplateWrapper;

/**
 * 搜索推荐查询接口
 * @author mali
 *
 */
@Service
public class SearchRecommendServiceImpl implements SearchRecommendService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchRecommendServiceImpl.class);
	
	// 搜索推荐URL的链接
	@Value("${ip.port.search.server}")
	private String searchServerIpAndPort;
    
	@Autowired
	private SearchRestTemplateWrapper searchRestTemplateWrapper;
    
	/**
	 * 
	 * @param keyword   关键词
	 * @param yhChannel	渠道
	 * @param gender	性别
	 * @return 搜索关键词结果
	 */
	@Override
	public List<SearchRecommendVo> searchRecommendList(String keyword) {
		LOGGER.info("searchRecommendList begin. keyword : {}", keyword);
		
		String searchRecommendStr = null;
		
		// 发送http请求 
		String url = getUrl(keyword);
		try {
			searchRecommendStr = searchRestTemplateWrapper.getForObject("search.fuzzy",url, String.class, Maps.newHashMap());
		} catch (Exception e) {
			LOGGER.warn("searchRecommendList find wrong. url : {}" ,url, e);
			throw new ServiceNotAvaibleException("search server!!!", e);
		}
		if (StringUtils.isNotEmpty(searchRecommendStr)) {
			// 对搜索结果进行处理，转换成app所需要的格式
			return converVo(searchRecommendStr, url, checkResultFormat(searchRecommendStr, url));
		} else {
			LOGGER.warn("The Result of searchRecommendList is null. url : ", url);
		}
		return null;
	}

	private List<SearchRecommendVo> converVo(String searchRecommendStr, String url, JSONArray items) {
		List<SearchRecommendVo> result = null;
		// 返回结果集不符合预定结构，则返回null
		if (null == items) {
			LOGGER.warn("The Result of searchRecommend is empty. searchRecommendStr is {}, url is {}", 
					searchRecommendStr, url);
		} else {
			// 遍历检索出来的结果集，转换成VO返回
			int size = items.size();
			result = new ArrayList<SearchRecommendVo>(10);
			List<String> tempItemStrList = new ArrayList<String>();
			String itemStr;
			
			// 找出10个不重复的搜索建议词，则返回；需要过滤除去空格之后的重复值
			int flag = 0;
			try {
				for (int i = 0; i < size; i++) {
					JSONObject jsonObject = items.getJSONObject(i);
					if (null != jsonObject && null != jsonObject.getString("item")) {
						itemStr = jsonObject.getString("item").replaceAll(" ", "");
						if (tempItemStrList.contains(itemStr)) {
							continue;
						} else {
							tempItemStrList.add(itemStr);
							result.add(new SearchRecommendVo(jsonObject.getString("item"), jsonObject.getInteger("frequency")));
							flag++;
							
							// 前段只展示10个
							if (10 == flag) {
								break;
							}
						}
					}
				}
			} catch (NumberFormatException e) {
				LOGGER.warn("The Result of searchRecommend is invalid. searchRecommendStr is {}, url is {}", 
						searchRecommendStr, url);
			} catch (JSONException e) {
				LOGGER.warn("The Result of searchRecommend is invalid. searchRecommendStr is {}, url is {}", 
						searchRecommendStr, url);
			}
		}
		return result;
	}
	
	// 校验结果的JSON格式是否正确
	private JSONArray checkResultFormat(String searchRecommendStr, String url) {
		JSONObject discountJson = JSONObject.parseObject(searchRecommendStr);
		if (null == discountJson) {
			return null;
		}
		JSONObject suggest = discountJson.getJSONObject("suggest");
		if (null == suggest) {
			return null;
		}
		return suggest.getJSONArray("items");
	}
	
	private String getUrl(String dynamicParam) {
		return "http://" + searchServerIpAndPort + "/yohosearch/suggest.json?size=20&query=" + dynamicParam;
	}
}
