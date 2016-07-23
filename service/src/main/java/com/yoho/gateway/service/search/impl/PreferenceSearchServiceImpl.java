package com.yoho.gateway.service.search.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.service.assist.SearchParam;
import com.yoho.gateway.service.search.PreferenceSearchService;
import com.yoho.gateway.service.search.wrapper.SearchRestTemplateWrapper;
import com.yoho.gateway.utils.DateUtil;

/**
 * 为您优选商品搜索接口
 *
 * @author wss
 */
@Service(value = "preferenceSearchService")
public class PreferenceSearchServiceImpl extends AbstractProductSearchService implements PreferenceSearchService {
	// LOG
	private static final Logger LOGGER = LoggerFactory.getLogger(PreferenceSearchServiceImpl.class);

	//默认优选的条数
    private final static Integer DEFAULT_PREFERENCESIZE=9;
    
	// 搜索推荐URL的链接
	@Value("${ip.port.search.server}")
	private String searchServerIpAndPort;

	@Autowired
	private SearchRestTemplateWrapper searchRestTemplateWrapper;

	@Override
	public JSONArray queryPreference(ProductSearchReq req) {
		//30天内上新
		SearchParam buildSearchParam=buildSearchParam(req,30);
		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), buildSearchParam.getGender());
		//优选的商品是先选20条，然后在20条中随机取9条
		JSONArray productList = data.getJSONArray(SearchConstants.NodeConstants.KEY_PRODUCT_LIST);
		if(CollectionUtils.isEmpty(productList))
		{
			return null;
		}
		Collections.shuffle(productList);
		return productList;
	}

	@Override
	public JSONArray querySortPreference(ProductSearchReq req) {
		//14天内上新
		SearchParam buildSearchParam=buildSearchParam(req,14);
		//这个是可配置的
		Map<String,Integer> preferenceSort=SearchConfig.getPreferenceSort();
		Set<String> sortSet=preferenceSort.keySet();
		JSONArray jsonArray=new JSONArray();
		//从这些为你优选的分类中随机选取一些商品
		for (String sort : sortSet) {
			buildSearchParam.setMsort(sort);
			buildSearchParam.setViewNum(preferenceSort.get(sort));
			JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), buildSearchParam.getGender());
			//优选的商品是先选20条，然后在20条中随机取9条
			JSONArray productList = data.getJSONArray(SearchConstants.NodeConstants.KEY_PRODUCT_LIST);
			if(jsonArray.size()>=DEFAULT_PREFERENCESIZE)
			 {
				 break;
			 }
			 jsonArray.addAll(productList); 
		}
		//随机选取
		Collections.shuffle(jsonArray);
		return jsonArray;
	}
	
	/**
	 * 调用检索接口，根据条件检索出结果
	 *
	 * @param params
	 *            检索条件
	 * @return 如果查询出现异常则返回null 正常则返回检索结果JSON对象，商品列表的图片已处理为绝对路径
	 */
	private JSONObject getSearch(String searchFrom, SearchParam params, List<String> ignoreTags, String gender) {
		// 查询商品列表信息接口的返回报文，根节点为data的父节点。商品列表在data节点下 product_list
		String resultJsonStr = null;

		// 合成URL及请求参数，得到最终的GET请求链接
		String url = getUrl(params.toParamString());

		LOGGER.info("search product list begin.url : {}", url);
		try {
			// 搜索接口的调用
			resultJsonStr = searchRestTemplateWrapper.getForObject(searchFrom, url, String.class, params.getParam());
		} catch (RestClientException e) {
			LOGGER.warn("The result of search product list find wrong. params : {},url {}: ", params.getParam(), url, e);
			throw new ServiceNotAvaibleException("search server!!!", e);
		} catch (Exception e) {
			LOGGER.warn("The result of search product list find wrong. params : {},url {}: ", params.getParam(), url, e);
			throw new ServiceNotAvaibleException("search server!!!", e);
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = JSONObject.parseObject(resultJsonStr);
		} catch (Exception e) {
			LOGGER.warn("The result of search product list is not string of json. result : {}", resultJsonStr, e);
			return null;
		}
		// 取出搜索接口真实查询的列表结果
		JSONObject data = jsonObject.getJSONObject(SearchConstants.NodeConstants.KEY_DATA);
		if (null == data) {
			LOGGER.info("The data field of search product list is null. params : {}, productListStr : {} ", params.getParam(), resultJsonStr);
			return null;
		}
		// 对商品列表的信息进行修改，商品图片地址URL补全
		processProductList(gender, data.getJSONArray(SearchConstants.NodeConstants.KEY_PRODUCT_LIST), ignoreTags);

		return data;
	}
	
	public final static class SearchConfig {
		
		//key：msort  value：viewNum
		//是有顺序的
		private static Map<String,Integer> preferenceSort=new LinkedHashMap<String, Integer>();
			
		static
		{	
			initPreferenceSort();
		}

		/**
		 * 除了商品详情页的为你优选，其它优选都从这些分类里面取指定的条数
		 */
		private static void initPreferenceSort() {
			preferenceSort.put("1", 2);
			preferenceSort.put("3", 2);
			preferenceSort.put("6", 2);
			preferenceSort.put("7", 1);
			preferenceSort.put("8", 1);
			preferenceSort.put("10", 1);
			preferenceSort.put("4", 9);
			preferenceSort.put("308", 9);
			preferenceSort.put("360", 9);
			preferenceSort.put("365", 9);
		}
		
		public static Map<String,Integer> getPreferenceSort()
		{
			return preferenceSort;
		}
	}
	
	/**
	 * 
	 * @param request 
	 * @param firstShelveTimeDay 多少天内上新
	 * @return
	 */
	private SearchParam buildSearchParam(ProductSearchReq req,int firstShelveTimeDay) {
		 SearchParam searchParam= new SearchParam();
		 searchParam.setGender(req.getYhChannel(), req.getGender());
		 //品牌列表ID
		 searchParam.setBrand(req.getBrand());
		 searchParam.setViewNum();
		 //为你优选的一些默认参
		 searchParam.setStatus().setPromotionDiscount(1).setStocknumber().setOutlets(2).setAttributenot().setFirstShelveTime(StringUtils.join(new Object[] {(DateUtil.getDateOfSenconds() - (86400 * firstShelveTimeDay)),DateUtil.getDateOfSenconds()}, ","));
		 return searchParam;
	}
	
	private String getUrl(String dynamicParam) {
		return "http://" + searchServerIpAndPort + "/yohosearch/search.json?" + dynamicParam;
	}

}