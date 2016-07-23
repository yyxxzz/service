package com.yoho.gateway.service.search.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.client.hystrix.AsyncFuture;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.core.rest.exception.ServiceNotFoundException;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.search.convert.SalesCategoryConvert;
import com.yoho.gateway.model.product.BrandVo;
import com.yoho.gateway.model.product.TogetherProductPriceVo;
import com.yoho.gateway.model.product.TogetherProductRspVo;
import com.yoho.gateway.model.product.TogetherProductVo;
import com.yoho.gateway.model.request.ProductRecommendReqVO;
import com.yoho.gateway.model.search.BigDataSearchReq;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.model.search.SalesCategoryRsp;
import com.yoho.gateway.model.search.SalesCategoryVo;
import com.yoho.gateway.model.search.SimpleBrandInfoVo;
import com.yoho.gateway.redis.BigDataValueOperations;
import com.yoho.gateway.service.assist.ImageUrlAssist;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.service.assist.SearchParam;
import com.yoho.gateway.service.search.BrandService;
import com.yoho.gateway.service.search.DiscountService;
import com.yoho.gateway.service.search.ProductSearchService;
import com.yoho.gateway.service.search.SalesCategoryService;
import com.yoho.gateway.service.search.ShopService;
import com.yoho.gateway.service.search.SortService;
import com.yoho.gateway.service.search.builder.FilterBuilder;
import com.yoho.gateway.service.search.helper.FilterHelper;
import com.yoho.gateway.service.search.wrapper.SearchRestTemplateWrapper;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.ProductBo;
import com.yoho.product.model.ProductSearchBo;
import com.yoho.product.model.SalesCategoryRspBo;
import com.yoho.product.model.ShopsBo;
import com.yoho.product.model.SpecialActivityBo;
import com.yoho.product.request.ActivityRequest;
import com.yoho.product.request.SalesCategoryReq;

/**
 * 商品搜索接口
 *
 * @author mali
 */
@Service(value = "categoryProductSearchService")
public class ProductSearchServiceImpl extends AbstractProductSearchService implements ProductSearchService {
	// LOG
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductSearchServiceImpl.class);
	
	private static final Logger recLogger = LoggerFactory.getLogger("recommendLogger");

	// 搜索推荐URL的链接
	@Value("${ip.port.search.server}")
	private String searchServerIpAndPort;
	
	@Autowired
	private SortService sortService;

	@Autowired
	private DiscountService discountService;

	@Autowired
	private SearchRestTemplateWrapper searchRestTemplateWrapper;

	@Autowired
	private SalesCategoryService salesCategoryService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private ShopService shopService;

	@Autowired
	private ServiceCaller serviceCaller;

	@Autowired
	private SalesCategoryConvert convert;

	@Autowired
	private CacheClient cacheClient;

	@Autowired
	private BigDataValueOperations<String, String> bigDataValueOperations;

	private static final String DETAIL_URL = "http://item.yohobuy.com/product/pro_{0}_{1}/{2}.html";

	// 用于断码区潮童展示用！
	private static final Set<String> jacketSet = Sets.newHashSet("366","367","386","396","400","402","404","406","417","423","430","451","453","456","464");

	private static final Set<String> pantSet = Sets.newHashSet("370","371","369","372","384","388","390","470");

	private static final Set<String> shoeSet = Sets.newHashSet("368","382","460","462");
	
	private static Map<String, String> ageLevelMap = new HashMap<String, String>(){{
		put("1", "成人");
		put("2", "大童");
		put("3", "小童");
		put("4", "中童");
		put("5", "幼童");
	}};

	/**
	 * 根据条件搜索符合条件的商品列表数据
	 *
	 * @param req 条件对象
	 * @return 符合条件的商品列表数据
	 */
	@Override
	public JSONObject searchProductListByCategory(ProductSearchReq req, AsyncFuture<String[]> labelNameAsync, String shop) {
		//多品店根据shopId来查,单品店根据品牌来查询
		buildShopSearchParam(req, shop);
		// 组装参数
		SearchParam buildSearchParam = new SearchParam().setGender(req.getYhChannel(), req.getGender()).setNeedFilter(null, true).setParameter(req.getParameter()).buildSearchParam(req, true)
				.setSearchFrom(req.getSearchFrom()).setParamNum(100);

		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), buildSearchParam.getGender());

		if (null != data) {
			processFilterInfo(buildSearchParam, buildSearchParam.getNeedFilter(), data, Lists.newArrayList(), req.getGender());

			// 过滤标签（商品参数）列表
			String[] labelNameArr = null == labelNameAsync ? null : labelNameAsync.get();
			if (null != labelNameArr && 0 < labelNameArr.length) {
				JSONArray standardJSONArray = data.getJSONArray("standard");
				Map<String, JSONObject> standardMap = new HashMap<String, JSONObject>();
				for (Object obj : standardJSONArray) {
					if (null == obj) {
						continue;
					}
					JSONObject jsonObj = JSONObject.parseObject(JSONObject.toJSONString(obj));
					standardMap.put(jsonObj.getString("standard_name"), jsonObj);
				}
				List<JSONObject> filterListJsonObj = new ArrayList<JSONObject>();
				for (String labelName : labelNameArr) {
					JSONObject jsonObj = standardMap.get(labelName);
					if (null == jsonObj) {
						continue;
					}
					filterListJsonObj.add(jsonObj);
				}
				data.put("standard", filterListJsonObj);
			} else {
				data.put("standard", new ArrayList<JSONObject>());
			}
		}
		if (null != data) {
			processCategory(data, req.getCategoryId(), req.getSubCategoryId());
			// 如果是店铺，筛选中的品牌列表应该是该店铺内的品牌
			processShopBrand(data, shop);
		}
		return data;
	}

	/**
	 * 根据条件搜索符合条件的奥莱潮品速递的商品列表数据
	 * @param req 条件对象
	 * @return 符合条件的奥莱潮品速递的商品列表数据
	 *
	 */
	@Override
	public JSONObject searchTrendCourierProductList(ProductSearchReq req) {

		SearchParam buildSearchParam = new SearchParam().setGender(req.getYhChannel(), req.getGender()).setLimit(req.getLimit()).setStocknumber(req.getStocknumber()).buildSearchParam(req, false).setSearchFrom(req.getSearchFrom());

		JSONObject data = getSearch(req.getSearchFrom(),buildSearchParam, new ArrayList<String>(0), buildSearchParam.getGender());

		return data;
	}

	/**
	 * 根据条件搜索符合条件的商品列表数据
	 *
	 * @param req
	 *            条件对象
	 * @return 符合条件的商品列表数据
	 */
	@Override
	public JSONObject searchActProduct(ProductSearchReq req) {

		boolean needFilter = "N".equals(req.getNeedFilter()) ? false : true;
		// 设置默认分类
		getSortInfo(req);

		// 组装参数
		SearchParam buildSearchParam = new SearchParam().setGender(req.getYhChannel(), req.getGender()).setParameter(req.getParameter()).buildSearchParam(req, needFilter)
				.setSearchFrom(req.getSearchFrom());

		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), buildSearchParam.getGender());

		if (null != data) {
			processFilterInfoForAct(buildSearchParam, buildSearchParam.getNeedFilter(), data, Lists.newArrayList());
		}

		return data;
	}

	/**
	 * 根据条件搜索符合条件的商品列表数据
	 *
	 * @param req 条件对象
	 * @return 符合条件的商品列表数据
	 */
	@Override
	public JSONObject searchFuzzyProductList(ProductSearchReq req, String shop) {
		// 设置默认分类
		getSortInfo(req);
		//多品店根据shopId来查,单品店根据品牌来查询
		buildShopSearchParam(req, shop);
		// 通过关键词查询相关的品牌信息
		SimpleBrandInfoVo simpleBrandInfoVo = setBrandInfo(req, shop);
		SearchParam buildSearchParam = new SearchParam().setGender(req.getYhChannel(), req.getGender()).buildSearchParam(req, true).setSearchFrom(req.getSearchFrom());
		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), buildSearchParam.getGender());
		if (null != data) {
			// 设置filter信息
			processFilterInfo(buildSearchParam, buildSearchParam.getNeedFilter(), data, Lists.newArrayList(), req.getGender());
			// 设置品牌信息
			processBrandInfo(data, simpleBrandInfoVo, buildSearchParam.getPage());
			// 如果是店铺，筛选中的品牌列表应该是该店铺内的品牌
			processShopBrand(data, shop);
			// 如果品牌有多个店铺中存在
			if (simpleBrandInfoVo!=null ) {
				shopService.processShopList(data, simpleBrandInfoVo.getId(), buildSearchParam.getPage());
			}
		}
		return data;
	}

	/**
	 * 根据请求参数搜索相关的商品列表
	 * @param req 请求参数
	 * @return 据请求参数搜索相关的商品列表
	 */
	@Override
	public JSONObject searchNewProductList(ProductSearchReq req) {

		getSortInfo(req);

		SearchParam buildSearchParam = new SearchParam().setGender(req.getYhChannel(), req.getGender()).buildSearchParam(req, true).setDayLimit(req.getDayLimit()).setSearchFrom(req.getSearchFrom());

		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, Lists.newArrayList(SearchConstants.NodeConstants.PRODUCT_TAG_IS_NEW, SearchConstants.NodeConstants.PRODUCT_TAG_IS_DISCOUNT),
				buildSearchParam.getGender());

		if (null != data) {
			processFilterInfo(buildSearchParam, buildSearchParam.getNeedFilter(), data, Lists.newArrayList(), req.getGender());
			appendTagHead(data);
		}
		return data;
	}

	/**
	 * 根据打折幅度查询商品列表
	 *
	 * @param req
	 *            请求参数
	 * @return 根据打折幅度查询商品列表
	 */
	@Override
	public JSONObject searchSalesProductList(ProductSearchReq req) {
		getSortInfo(req);
		// 判断折扣专区是否传了商品池id，如果没有则查询所有有效的活动的商品池id
		if ("1".equals(req.getFilterSaleAction())) {
			if (StringUtils.isEmpty(req.getProductPool())) {
				req.setProductPool(getPoolIds());
				req.setStorageNum(1);
			}
		}
		SearchParam buildSearchParam = new SearchParam().setGender(req.getYhChannel(), req.getGender()).buildSearchParam(req, true).setSearchFrom(req.getSearchFrom());

		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, Lists.newArrayList(SearchConstants.NodeConstants.PRODUCT_TAG_IS_NEW, SearchConstants.NodeConstants.PRODUCT_TAG_IS_DISCOUNT),
				buildSearchParam.getGender());

		if (null != data) {
			processFilterInfo(buildSearchParam, buildSearchParam.getNeedFilter(), data, Lists.newArrayList(), req.getGender());
		}
		return data;
	}

	private String getPoolIds() {
		StringBuffer sb = new StringBuffer();
		ActivityRequest<Integer> request = new ActivityRequest<Integer>();
		// 设置查询参数
		request.setPlateform("2");
		request.setSort("2");
		SpecialActivityBo[] list = serviceCaller.call("product.queryActivity", request, SpecialActivityBo[].class);
		for (SpecialActivityBo bo : list) {
			if (bo.getProductPoolId() != null) {
				sb.append(bo.getProductPoolId()).append(",");
			}

		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yoho.gateway.service.search.ProductSearchService#
	 * searchLast7dayProductList(com.yoho.gateway.model.search.ProductSearchReq)
	 */
	@Override
	public JSONObject searchLast7dayProductList(ProductSearchReq req) {

		getSortInfo(req);

		SearchParam buildSearchParam = new SearchParam().setGender(req.getYhChannel(), req.getGender()).buildSearchParam(req, false);
		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), buildSearchParam.getGender());
		if (null != data) {
			// 打乱商品列表的顺序
			shuffleProductList(data.getJSONArray("product_list"));
		}
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.yoho.gateway.service.search.ProductSearchService#
	 * searchLifeStyleProductList
	 * (com.yoho.gateway.model.search.ProductSearchReq)
	 */
	@Override
	public JSONArray searchLifeStyleProductList(ProductSearchReq req) {

		SearchParam buildSearchParam = new SearchParam().buildSearchParam(req, false);

		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), null);

		// 随机选择某页
		int total = 0 == data.getInteger("total") ? 0 : data.getInteger("total");
		int totalPage = (int) (Math.ceil(total / req.getLimit()));
		if (totalPage - 1 > 1) {
			Random rd = new Random();
			int page = rd.nextInt(totalPage - 1) + 1;
			req.setPage(page);
			buildSearchParam = new SearchParam().buildSearchParam(req, false);
			data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), null);
		}
		return data.getJSONArray("product_list");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yoho.gateway.service.search.ProductSearchService#searchKidsProductList
	 * (com.yoho.gateway.model.search.ProductSearchReq)
	 */
	@Override
	public JSONObject searchKidsProductList(ProductSearchReq req) {
		int initPage = null == req.getPage() ? 1 : req.getPage();

		SearchParam buildSearchParam = new SearchParam().buildSearchParam(req, false);
		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), null);
		// 随机选择某页
		int total = 0 == data.getInteger("total") ? 0 : data.getInteger("total");
		int totalPage = (int) (Math.ceil(total / req.getLimit()));
		if (totalPage - 1 > 1) {
			Random rd = new Random();
			int page = rd.nextInt(totalPage - 1) + 1;
			req.setPage(page);
			buildSearchParam = new SearchParam().buildSearchParam(req, false);
			data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), null);
		}
		JSONObject rspJson = new JSONObject();
		rspJson.put("product_list", data.getJSONArray("product_list"));
		rspJson.put("page", initPage);
		rspJson.put("page_total", data.getInteger("page_total"));
		rspJson.put("total", data.getInteger("total"));

		return rspJson;
	}

	//1.从大数据的redis获取skn
	//2.根据skn查询搜索 排序
	@Override
	public JSONObject searchProductListByBigData(BigDataSearchReq searchReq) {
		ProductRecommendReqVO req = new ProductRecommendReqVO();
		req.setUid(StringUtils.isEmpty(searchReq.getUserId()) ? 0 : Integer.parseInt(searchReq.getUserId()));
		req.setRec_pos(searchReq.getRecPos());
		req.setTotal(getTotalByRecPos(searchReq.getRecPos()));
		LOGGER.info("start call bigdata requestParam is {}", req);
		long beginTime=System.currentTimeMillis();
		Integer[] sknArray = serviceCaller.asyncCall("bigdata.getRecommendSknList", req, Integer[].class).get(1);
		LOGGER.info("end call bigdata service size is:{},cost time :{}",null==sknArray?0:sknArray.length,System.currentTimeMillis()-beginTime);
		return buildBigDataDetail(sknArray, searchReq);
		
	}
	
	/**
	 * 从大数据查询买了又买商品
	 * @param searchReq
	 * @return
	 */
	@Override
	public JSONObject searchPurchasedListByBigData(BigDataSearchReq searchReq) {
		ProductRecommendReqVO req = new ProductRecommendReqVO();
		req.setProductSkn(searchReq.getProductSkn());
		req.setRec_pos(searchReq.getRecPos());
		req.setTotal(getTotalByRecPos(searchReq.getRecPos()));
		LOGGER.info("start call bigdata requestParam is {}", req);
		Integer[] sknArray = serviceCaller.call("bigdata.getRecommendSknListBySkn", req, Integer[].class);
		
		return buildBigDataDetail(sknArray, searchReq);
		
	}
	
	private JSONObject buildBigDataDetail(Integer[] sknArray, BigDataSearchReq searchReq){
		List<Integer> sknList = Lists.newArrayList();
		Collections.addAll(sknList, sknArray);
		//分页
		List<Integer> newSknList =fetchSknByPage(sknList, (searchReq.getPage() - 1) * searchReq.getLimit(), searchReq.getPage() * searchReq.getLimit());
		LOGGER.info("the recommed redis skn is:{}",newSknList);
		//查询搜索引擎
		JSONObject data = getBigDataFromSearchEngines(newSknList, searchReq);
		if (null == data) {
			return null;
		}
		//按照大数据的skn顺序排序
		sortSearchResult(data, sknList, newSknList, searchReq);
		//构造大数据需要的日志数据
		UUID recId=buildBigDataLog(sknList,searchReq.getUserId(),searchReq.getUdid(),searchReq.getRecPos());
		data.put("rec_id", recId);
		return data;
	}
	
	/**
	 * 100001:首页-男生-猜你喜欢  200个
	 * 100002:首页-女生-猜你喜欢 200个
	 * 100003:购物车-为您优选        30个
	 * 100004:我的个人中心-为您优选  30个
	 * 100005:查看物流-买了又买    2个
	 * 100006:查看物流-猜你喜欢   50个
	 * 100007:支付成功-买了又买  2个
	 * 100008:支付成功-猜你喜欢   50个
	 * 100009:yoho币-猜你喜欢   50个
	 * 100010:首页-男生-弹窗       1个
	 * 100011:首页-女生-弹窗       1个
	 * @param recPos
	 * @return
	 */
	private int getTotalByRecPos(String recPos){
		if("100001".equals(recPos) || "100002".equals(recPos)){
			return 200;
		}else if("100003".equals(recPos) || "100004".equals(recPos)){
			return 30;
		}else if("100005".equals(recPos) || "100007".equals(recPos)){
			return 2;
		}else if("100006".equals(recPos) || "100008".equals(recPos) || "100009".equals(recPos)){
			return 50;
		}else{
			return 1;
		}
	}
	
	//构造大数据需要的日志数据
	private UUID buildBigDataLog(List<Integer> sknList, String userId, String udid, String recPos) {
		UUID recId = UUID.randomUUID();
		if(CollectionUtils.isNotEmpty(sknList)){
			JSONObject obj = new JSONObject();
			obj.put("uid", userId);
			obj.put("udid", udid);
			obj.put("rec_id", recId);
			obj.put("timestamp", DateUtil.getCurrentTimeSeconds());
			obj.put("skn_list", sknList);
			obj.put("rec_pos", recPos);
			recLogger.info(obj.toJSONString());
		}
		return recId;
	}

	private void sortSearchResult(JSONObject data, List<Integer> sknList, List<Integer> newSknList, ProductSearchReq searchReq) {
		JSONArray productList = data.getJSONArray("product_list");
		if (CollectionUtils.isEmpty(productList)) {
			return;
		}
		JSONArray newProductList = new JSONArray(newSknList.size());
		for (Integer skn : newSknList) {
			for (int index = 0; index < productList.size(); index++) {
				JSONObject product = productList.getJSONObject(index);
				if (product.getInteger("product_skn").intValue() == skn.intValue()) {
					newProductList.add(product);
				}
			}
		}
		data.replace("product_list", newProductList);
		data.replace("page", searchReq.getPage());
		data.replace("page_total", (sknList.size() + searchReq.getLimit() - 1) / searchReq.getLimit());
		data.replace("total", sknList.size());
	}

	/**
	 * 分页获取skn
	 * 
	 * @param list
	 * @param begin
	 * @param end
	 * @return
	 */
	public static <T> List<T> fetchSknByPage(List<T> list, int begin, int end) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		int startIndex = begin;
		int endIndex = end;
		if (startIndex > endIndex || startIndex > list.size()) {
			return null;
		}
		if (endIndex > list.size()) {
			endIndex = list.size();
		}
		return list.subList(startIndex, endIndex);
	}


	private JSONObject getBigDataFromSearchEngines(List<Integer> sknList, ProductSearchReq searchReq) {
		if (CollectionUtils.isEmpty(sknList)) {
			return null;
		}
		SearchParam buildSearchParam = new SearchParam().setQuery(StringUtils.join(sknList, ",")).setViewNum(searchReq.getLimit());
		JSONObject data = null;
		try {
			data = getSearch(searchReq.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), null);
		} catch (Exception e) {
			LOGGER.warn("getBigDataFromSearchEngines failed!!!", e);
			throw new ServiceNotAvaibleException("search server!!!", e);
		}
		return data;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yoho.gateway.service.search.ProductSearchService#searchProductListByBrand
	 * (com.yoho.gateway.model.search.ProductSearchReq)
	 */
	@Override
	public JSONObject searchProductListByBrand(ProductSearchReq req) {

		SearchParam buildSearchParam = new SearchParam().setGender((StringUtils.isNotBlank(req.getGender()) && !"1,2,3".equals(req.getGender())) ? req.getGender() : null)
				.buildSearchParam(req, true).setSearchFrom(req.getSearchFrom());

		//如果app传入gender为1,2,3，表示筛选里的ALL，根据gender选择封面图时，会存在问题，这种情况根据channel来区别gender
		String gender = buildGender(req);
		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), gender);

		if (null != data) {
			processFilterInfo(buildSearchParam, buildSearchParam.getNeedFilter(), data, Lists.newArrayList(SearchConstants.NodeConstants.FILTER_KEY_BRAND), req.getGender());
		}
		return data;
	}

	/**
	 * 如果app传入gender为1,2,3，表示筛选里的ALL，根据gender选择封面图时，会存在问题，这种情况根据channel来区别gender
	 * @param req
	 */
	private String buildGender(ProductSearchReq req) {
		String gender = req.getGender();
		if("1,2,3".equals(req.getGender())){
			if("1".equals(req.getYhChannel())){
				gender = "1,3";
			}else if("2".equals(req.getYhChannel())){
				gender = "2,3";
			}
		}
		return gender;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.yoho.gateway.service.search.ProductSearchService#searchTopProductList
	 * (com.yoho.gateway.model.search.ProductSearchReq)
	 */
	@Override
	public JSONObject searchTopProductList(ProductSearchReq req) {
		getSortInfo(req);

		SearchParam buildSearchParam = new SearchParam().setTopSearchGender(req.getYhChannel()).buildSearchParam(req, false);

		return getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), buildSearchParam.getGender());
	}

	@Override
	public JSONArray searchSaleBreakingSort(ProductSearchReq req) {
        getSortInfo(req);
		SearchParam buildSearchParam = new SearchParam().setBreaking("1").buildSearchParam(req, true).setViewNum(1).setNeedSmallSort().setIsDiscount("Y")
				.setGender(req.getYhChannel(), req.getGender());
		JSONObject data = getSearchSort(req.getSearchFrom(), buildSearchParam);
		Map<String, Object> shoes = new HashMap<String, Object>();
		Map<String, Object> clothes = new HashMap<String, Object>();
		Map<String, Object> trousers = new HashMap<String, Object>();
		Map<String, Object> other = new HashMap<String, Object>();
		StringBuffer sbShoes = new StringBuffer();
		StringBuffer sbClothes = new StringBuffer();
		StringBuffer sbTrousers = new StringBuffer();
		StringBuffer sbOther = new StringBuffer();
		shoes.put("sort_name", "鞋子");
		//增加max_sort_id属性
		shoes.put("max_sort_id", "6");
		clothes.put("sort_name", "上装");
		clothes.put("max_sort_id", "1");
		trousers.put("sort_name", "下装");
		trousers.put("max_sort_id", "3");
		other.put("sort_name", "其他");
		other.put("max_sort_id", "0");
		JSONArray shoesList = new JSONArray();
		JSONArray clothesList = new JSONArray();
		JSONArray trousersList = new JSONArray();
		JSONArray otherList = new JSONArray();
		if (data != null) {
			JSONArray sort = data.getJSONArray("list");

			for (int index = 0; index < sort.size(); index++) {
				JSONObject jSONObject = (JSONObject) sort.get(index);
				// sort id为1时，表示是一级分类为上衣
				if ("1".equals(jSONObject.get("max_sort_id").toString())) {
					sbClothes.append(jSONObject.get("small_sort_id").toString()).append(",");
					clothesList.addAll(jSONObject.getJSONArray("sizes"));
					// sort id为3时，表示是一级分类为裤装
				} else if ("3".equals(jSONObject.get("max_sort_id").toString())) {
					sbTrousers.append(jSONObject.get("small_sort_id").toString()).append(",");
					trousersList.addAll(jSONObject.getJSONArray("sizes"));
				} else if ("6".equals(jSONObject.get("max_sort_id").toString())) {
					sbShoes.append(jSONObject.get("small_sort_id").toString()).append(",");
					shoesList.addAll(jSONObject.getJSONArray("sizes"));
				} else {
					// 潮童的按照产品和运营给的类目来展示
					if (jacketSet.contains(jSONObject.getString("middle_sort_id"))) {
						sbClothes.append(jSONObject.getString("small_sort_id")).append(",");
						clothesList.addAll(jSONObject.getJSONArray("sizes"));
					} else if (pantSet.contains(jSONObject.getString("middle_sort_id"))) {
						sbTrousers.append(jSONObject.getString("small_sort_id")).append(",");
						trousersList.addAll(jSONObject.getJSONArray("sizes"));
					} else if (shoeSet.contains(jSONObject.getString("middle_sort_id"))) {
						sbShoes.append(jSONObject.getString("small_sort_id")).append(",");
						shoesList.addAll(jSONObject.getJSONArray("sizes"));
					} else {
						sbOther.append(jSONObject.get("small_sort_id").toString()).append(",");
						otherList.addAll(jSONObject.getJSONArray("sizes"));
					}
				}

			}
		}

		shoes.put("sub", distinctJSONArray(shoesList));
		shoes.put("sort_id", sbShoes.toString());
		clothes.put("sub", distinctJSONArray(clothesList));
		clothes.put("sort_id", sbClothes.toString());
		trousers.put("sub", distinctJSONArray(trousersList));
		trousers.put("sort_id", sbTrousers.toString());
		other.put("sub", distinctJSONArray(otherList));
		other.put("sort_id", sbOther.toString());
		JSONArray size = new JSONArray();
		size.add(clothes);
		size.add(trousers);
		size.add(shoes);
		size.add(other);

		return size;
	}

	private List<JSONObject> distinctJSONArray(JSONArray array) {
		Map<Integer, Object> map = new LinkedHashMap<Integer, Object>();
		List<JSONObject> list = Lists.newArrayList();
		for (int index = 0; index < array.size(); index++) {
			map.put(array.getJSONObject(index).getInteger("size_id"), array.getJSONObject(index));
		}
		for (Integer inte : map.keySet()) {
			list.add((JSONObject) map.get(inte));
		}
		return list;
	}

	/**
	 * 自主品牌检索商品列表
	 *
	 * @param req
	 * @return
	 */
	@Override
	public JSONArray searchSelfOwnBrandProductList(ProductSearchReq req) {
		// 获取分类信息
		getSortInfo(req);

		SearchParam buildSearchParam = new SearchParam().setGender(req.getYhChannel(), req.getGender()).buildSearchParam(req, false).setDayLimit4OwnBrand(req.getDayLimit()).setLimit(req.getLimit());

		return getSearchOwnBrand(req.getSearchFrom(), buildSearchParam, Lists.newArrayList("is_new", "is_discount"), buildSearchParam.getGender());
	}

	/**
	 * 根据条件搜索凑单商品列表数据
	 *
	 * @param req
	 *            条件对象
	 * @return 符合条件的商品列表数据
	 */
	@Override
	public TogetherProductRspVo searchTogetherProductList(ProductSearchReq req) {

		SearchParam buildSearchParam = new SearchParam().buildSearchParam(req, false);

		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), null);

		Integer total = data.getInteger("total");
		Integer page = req.getPage();
		// 重新计算页数
		if (total < page * req.getLimit()) {
			double maxRand = Math.ceil(total / req.getViewNum()) > 0 ? Math.ceil(total / req.getViewNum()) : 1;
			Random rd = new Random();
			int rand = (int) maxRand;
			page = rd.nextInt(rand) + 1;
			req.setPage(page);
			data = getSearch(req.getSearchFrom(), new SearchParam().buildSearchParam(req, false), new ArrayList<String>(0), null);
		}

		TogetherProductRspVo rspVo = new TogetherProductRspVo();
		JSONArray productList = data.getJSONArray("product_list");
		List<TogetherProductVo> productVoList = buildTogetherProductList(productList);

		rspVo.setPage(req.getPage());
		rspVo.setTotal(data.getInteger("total"));
		rspVo.setPageTotal(data.getInteger("page_total"));
		rspVo.setGoods(productVoList);

		return rspVo;
	}
	
	/**
	 * 买了又买商品推荐
	 * @param searchReq
	 */
	@Override
	public List<ProductSearchBo> searchPurchasedProductList(ProductSearchReq req, ProductBo product){
		
		SearchParam buildSearchParam = new SearchParam().buildSearchParam(req, false);
		JSONObject data = getSearch(req.getSearchFrom(), buildSearchParam, new ArrayList<String>(0), buildSearchParam.getGender());
		
		//返回的是最终的productList列表
		JSONArray productList = data.getJSONArray(SearchConstants.NodeConstants.KEY_PRODUCT_LIST);
		List<ProductSearchBo> searchList=JSON.parseArray(productList.toJSONString(), ProductSearchBo.class);
		List<ProductSearchBo> purchaseList=Lists.newArrayList();
		for(ProductSearchBo productSearchBo:searchList){
			//同品牌同品类取一个
			if(product.getBrandId().equals(productSearchBo.getBrand_id()) && product.getMiddleSortId().equals(productSearchBo.getMiddle_sort_id())){
					purchaseList.add(productSearchBo);
					break;
				}
			}
		for(ProductSearchBo productSearchBo:searchList){
			//同品牌不同品类取一条
			if(product.getBrandId().equals(productSearchBo.getBrand_id()) && !product.getMiddleSortId().equals(productSearchBo.getMiddle_sort_id())){
				purchaseList.add(productSearchBo);
				break;
			}
		}
			
		return purchaseList;
		
	}
	
	private List<TogetherProductVo> buildTogetherProductList(JSONArray productList) {
		List<TogetherProductVo> togetherProductVoList = new ArrayList<TogetherProductVo>();
		for (int index = 0; index < productList.size(); index++) {
			TogetherProductVo vo = new TogetherProductVo();
			JSONObject product = productList.getJSONObject(index);
			vo.setId(product.getInteger("product_id"));
			vo.setProductName(product.getString("product_name"));
			vo.setProductSkn(product.getInteger("product_skn"));
			TogetherProductPriceVo priceVo = new TogetherProductPriceVo();
			priceVo.setMarketPrice(String.format("%.2f", Math.ceil(product.getFloatValue("market_price"))));
			priceVo.setSalesPrice(String.format("%.2f", Math.ceil(product.getFloatValue("sales_price"))));
			vo.setPrice(priceVo);

			// 获取默认goods
			JSONObject defaultGoods = getDefaultGoods(product);
			if (defaultGoods == null) {
				vo.setDefaultPic(product.getString("default_images"));
				vo.setUrl("");
			} else {
				String productId = product.getString("product_id");
				String goodsId = defaultGoods.getString("goods_id");
				String cnAlphabet = product.getString("cn_alphabet");
				vo.setDefaultPic(defaultGoods.getString("images_url"));
				vo.setUrl(MessageFormat.format(DETAIL_URL, new Object[] { productId, goodsId, cnAlphabet }));
			}
			togetherProductVoList.add(vo);
		}

		return togetherProductVoList;
	}

	private JSONObject getDefaultGoods(JSONObject product) {
		JSONArray goodsList = product.getJSONArray("goods_list");
		if (goodsList == null || goodsList.isEmpty()) {
			return null;
		}
		for (int j = 0; j < goodsList.size(); j++) {
			JSONObject goods = goodsList.getJSONObject(j);
			if ("Y".equals(goods.getString("is_default"))) {
				return goods;
			}
		}
		return goodsList.getJSONObject(0);
	}

	private JSONArray getSearchOwnBrand(String searchFrom, SearchParam params, List<String> ignoreTags, String gender) {
		// 查询商品列表信息接口的返回报文，根节点为data的父节点。商品列表在data节点下 product_list
		String resultJsonStr = null;

		// 合成URL及请求参数，得到最终的GET请求链接
		String url = getOwnBrandUrl(params.toParamString());
		LOGGER.info("search product list begin.url : {}", url);
		try {
			// 搜索接口的调用
			resultJsonStr = searchRestTemplateWrapper.getForObject(searchFrom, url, String.class, params.getParam());
		} catch (Exception e) {
			LOGGER.warn("The result of search product list find wrong. url {}: ", url, e);
			throw new ServiceNotAvaibleException("search server!!!", e);
		}
		JSONObject jsonObject = null;
		try {
			jsonObject = JSONObject.parseObject(resultJsonStr);
		} catch (Exception e) {
			LOGGER.warn("The result of search product list is not string of json. result:{}", resultJsonStr, e);
			return null;
		}
		// 取出搜索接口真实查询的列表结果
		JSONArray productListJsonArray = jsonObject.getJSONArray(SearchConstants.NodeConstants.KEY_DATA);
		if (null == productListJsonArray) {
			LOGGER.warn("The data field of search product list is null. params:{}, productListStr:{} ", params.getParam(), resultJsonStr);
			return null;
		}
		// 对商品列表的信息进行修改，商品图片地址URL补全
		processProductList(gender, productListJsonArray, ignoreTags);

		return productListJsonArray;
	}

	// 新品到着需要额外标签头
	private void appendTagHead(JSONObject data) {
		Map<String, String> tab = new LinkedHashMap<String, String>();
		tab.put("1", DateUtil.date2String(new Date(), "MM月dd日"));
		tab.put("2", "本周上新");
		tab.put("3", "销量");
		data.put("tabs", tab);
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
		String url = null;
		if (params.getParam().containsKey(SearchConstants.IndexNameConstant.KEY_BREACKING)) {
			url = getBreakSearchUrl(params.toParamString());
		} else {
			url = getUrl(params.toParamString());
		}

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

	/**
	 * 查询断码区商品分类
	 *
	 * @param searchFrom
	 * @param params
	 * @return
	 */
	private JSONObject getSearchSort(String searchFrom, SearchParam params) {
		// 查询商品列表信息接口的返回报文，根节点为data的父节点。商品列表在data节点下 product_list
		String resultJsonStr = null;
		// 合成URL及请求参数，得到最终的GET请求链接
		String url = getGroupSortUrl(params.toParamString());
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
			LOGGER.warn("The data field of search product list is null. params : {}, productListStr : {} ", params.getParam(), resultJsonStr);
			return null;
		}
		return data;
	}

	/**
	 * 如果是模糊查询，则将关键词匹配到的品牌信息塞到data节点下，返回出去
	 *
	 * @param data
	 *            搜索结果的data节点的JSONObject对象
	 * @param brandInfo
	 *            如果是模糊查询，则通过关键词匹配到的品牌对象
	 */
	private void processBrandInfo(JSONObject data, SimpleBrandInfoVo brandInfo, Integer pageStr) {
		// 如果是第一页查询，且关键词匹配得到了品牌信息
		if (null != pageStr && 1 == pageStr && null != brandInfo) {
			data.put(SearchConstants.NodeConstants.FILTER_KEY_BRAND, brandInfo);
		}
	}

	// 处理过滤条件的结果集
	private void processFilterInfo(SearchParam params, String needFilter, JSONObject data, List<String> ignoreFilter, String gender) {
		// 对过滤条件进行更改
		JSONObject filter = data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER);
		if (null != filter) {
			// 删除不必要返回到app的过滤条件
			delNeedlessItem(data, filter, ignoreFilter);

			// 将过滤条件的键值price转成priceRange
			JSONObject price = filter.getJSONObject(SearchConstants.NodeConstants.FILTER_KEY_PRICE);
			filter.put(SearchConstants.NodeConstants.FILTER_KEY_PRICERANGE, price);
			filter.remove(SearchConstants.NodeConstants.FILTER_KEY_PRICE);
			// 对过滤条件（品牌）的图标URL进行补全
			JSONArray brandList = filter.getJSONArray(SearchConstants.NodeConstants.FILTER_KEY_BRAND);
			if (null != brandList) {
				int size = brandList.size();
				for (int i = 0; i < size; i++) {
					JSONObject brand = (JSONObject) brandList.get(i);
					brand.replace(SearchConstants.NodeConstants.KEY_BRAND_ICO,
							ImageUrlAssist.getAllProductPicUrl(brand.get(SearchConstants.NodeConstants.KEY_BRAND_ICO), "brandLogo", "center", "d2hpdGU="));
				}
			}
			// 添加打折的过滤集合
			filter.put(SearchConstants.NodeConstants.KEY_DISCOUNT, discountService.getDiscount(params.getSearchFrom(), params.toParamString()));

			// 查询过滤信息中需要的group_sort信息列表
			if ("1".equals(needFilter)) {
				params.setNeedSmallSort();
				if (!ignoreFilter.contains(SearchConstants.NodeConstants.KEY_GROUP_SORT)) { // 如果忽略了分类筛选条件，则无需再查询分类信息
					// 查询分类信息
					params.removeParameter();
					filter.put(SearchConstants.NodeConstants.KEY_GROUP_SORT, sortService.getSortList(params.getSearchFrom(), params.toParamString()));
				}
			}

			//判断性别
			if(!StringUtils.isEmpty(gender)){
				Map<String, String> genderMap = new HashMap<String, String>();
				if ("1".equals(gender) || "1,3".equals(gender)) {
					genderMap.put("1,3", "BOYS");
				} else if ("2".equals(gender) || "2,3".equals(gender)) {
					genderMap.put("2,3", "GIRLS");
				} else {
					genderMap.put("1,3", "BOYS");
					genderMap.put("2,3", "GIRLS");
				}

				filter.put(SearchConstants.NodeConstants.FILTER_KEY_GENDER, genderMap);
			}
			
			//处理年龄层
            String ageLevel = (String) params.getParam().get(SearchConstants.IndexNameConstant.AGE_LEVEL);
            if(StringUtils.isEmpty(ageLevel)){
            	return;
            }
            
            JSONArray ageResultArr = getAgeResult(ageLevel);
            
            filter.put(SearchConstants.NodeConstants.FILTER_KEY_AGELEVEL, ageResultArr);
            
		}
	}
	
	private JSONArray getAgeResult(String ageLevel){
		String[] ageArr = ageLevel.split(",");
        JSONArray ageResultArr = new JSONArray(ageArr.length);
        for(String item : ageArr){
        	Map<String, String> ageMap = new HashMap<String, String>();
        	ageMap.put("id", item);
            ageMap.put("name", ageLevelMap.get(item));
            ageResultArr.add(ageMap);
        }
        
        return ageResultArr;
	}

	/**
	 * 处理活动模板中过滤条件
	 * 
	 * @param params
	 * @param needFilter
	 * @param data
	 * @param ignoreFilter
	 */
	private void processFilterInfoForAct(SearchParam params, String needFilter, JSONObject data, List<String> ignoreFilter) {
		// 对过滤条件进行更改
		final JSONObject filter = data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER);
		if (null != filter) {
			// 将过滤条件的键值price转成priceRange
			FilterBuilder filterBuilder = new FilterBuilder(filter);
			filterBuilder.buildPriceRange();
			filter.remove(SearchConstants.NodeConstants.FILTER_KEY_PRICE);
			// 对过滤条件（品牌）的图标URL进行补全
			FilterHelper.completeBrandImg(filter.getJSONArray(SearchConstants.NodeConstants.FILTER_KEY_BRAND));
			// 添加打折的过滤集合
			filterBuilder.buildDiscount(discountService.getDiscount(params.getSearchFrom(), params.toParamString()));

			// 查询过滤信息中需要的group_sort信息列表
			if ("1".equals(needFilter)) {
				params.setNeedSmallSort();
				if (!ignoreFilter.contains(SearchConstants.NodeConstants.KEY_GROUP_SORT)) { // 如果忽略了分类筛选条件，则无需再查询分类信息
					// 查询分类信息
					params.removeParameter();
					filterBuilder.buildGroupSort(sortService.getSortList(params.getSearchFrom(), params.toParamString()));
				}
			}
		}
	}

	// 删除不必要返回到app的过滤条件
	private void delNeedlessItem(JSONObject data, JSONObject filter, List<String> ignoreFilter) {
		List<String> needItemList = Lists.newArrayList(SearchConstants.NodeConstants.FILTER_KEY_PRICE, SearchConstants.NodeConstants.FILTER_KEY_SIZE, SearchConstants.NodeConstants.FILTER_KEY_GENDER,
				SearchConstants.NodeConstants.FILTER_KEY_BRAND, SearchConstants.NodeConstants.FILTER_KEY_COLOR, SearchConstants.NodeConstants.FILTER_KEY_AGELEVEL);
		Set<String> keySet = new HashSet<String>(filter.keySet());
		List<JSONObject> listJsonObj = new ArrayList<JSONObject>();
		for (String key : keySet) {
			// 重新组装参数列表
			if (key.contains("parameter")) {
				JSONObject jsonObj = filter.getJSONObject(key);
				listJsonObj.add(jsonObj);
			}
			if (!needItemList.contains(key) || ignoreFilter.contains(key)) {
				filter.remove(key);
			}
		}
		data.put("standard", listJsonObj);
	}

	// 如果YhChannel不为空，且分类参数为空，则需要赋默认的分类
	private void getSortInfo(ProductSearchReq req) {
		if (StringUtils.isEmpty(req.getSort()) && StringUtils.isNotEmpty(req.getYhChannel())) {
			req.setSort(salesCategoryService.queryRelationParamter(req.getYhChannel()));
		}
	}
	
	private String getUrl(String dynamicParam) {
		return "http://" + searchServerIpAndPort + "/yohosearch/search.json?" + dynamicParam;
	}

	private String getOwnBrandUrl(String dynamicParam) {
		return "http://" + searchServerIpAndPort + "/yohosearch/new_product.json?" + dynamicParam;
	}

	private String getGroupSortUrl(String dynamicParam) {
		return "http://" + searchServerIpAndPort + "/yohosearch/sort_sizes.json?" + dynamicParam;
	}

	private String getBreakSearchUrl(String dynamicParam) {
		return "http://" + searchServerIpAndPort + "/yohosearch/sort_size_products.json?" + dynamicParam;
	}

	private SimpleBrandInfoVo setBrandInfo(ProductSearchReq req, String shop) {
		if(StringUtils.isNotEmpty(shop)){
			return null;
		}
		SimpleBrandInfoVo brandInfo = brandService.getBrandInfoByQuery(req.getQuery());
		if (null != brandInfo) {
			req.setBrand(String.valueOf(brandInfo.getId()));
		}
		return brandInfo;
	}

	// 打乱商品列表的顺序
	private void shuffleProductList(JSONArray productList) {
		if (null != productList && productList.size() > 1) { // 列表长度大于1 才能打乱
			Collections.shuffle(productList);
		}
	}

	private void processCategory(JSONObject data, String categoryId, String subCategoryId) {
		SalesCategoryRspBo responseBean = null;
		SalesCategoryReq salreq = new SalesCategoryReq();
		salreq.setState(1);
		SalesCategoryRsp salesCategoryRsp = null;
		try {
			responseBean = cacheClient.get("yh:gw:product:salesCategoryList", SalesCategoryRspBo.class);
			if (null == responseBean) {
				responseBean = serviceCaller.call("product.querySalesCategoryList", salreq, SalesCategoryRspBo.class);
				if (null != responseBean) {
					cacheClient.set("yh:gw:product:salesCategoryList", 1200, responseBean);
				}
			}
			salesCategoryRsp = convert.convert(responseBean.getData());
			Set<String> set = salesCategoryRsp.getData().keySet();
			List<SalesCategoryVo> voList = new ArrayList<SalesCategoryVo>();
			for (String v_categoryId : set) {
				List<SalesCategoryVo> list = salesCategoryRsp.getData().get(v_categoryId);
				for (SalesCategoryVo vo : list) {
					if (StringUtils.isNotBlank(categoryId)) {
						if (vo.getCategoryId().equals(categoryId)) {
							if (null == subCategoryId || "".equals(subCategoryId)) {
								voList.add(vo);
								data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER).put("group_sort", voList);
							} else {
								data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER).remove("group_sort");
							}
						}
					} else {
						data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER).remove("group_sort");
					}
				}
			}

		} catch (ServiceException e) {
			LOGGER.warn("call product.querySalesCategoryList is failed", e);
		} catch (ServiceNotAvaibleException e) {
			LOGGER.warn("call product.querySalesCategoryList is failed", e);
		} catch (ServiceNotFoundException e) {
			LOGGER.warn("call product.querySalesCategoryList is failed", e);
		}
	}


	/**
	 * 如果是店铺，筛选中的品牌列表应该是该店铺内的品牌
	 * @param data
	 * @param shop
	 */
	private void processShopBrand(JSONObject data, String shop) {
		if(StringUtils.isEmpty(shop)){
			return;
		}
		List<BrandBo> brandBoList = shopService.getBrandListByShopId(Integer.valueOf(shop));
		List<BrandVo> brandVoList = new ArrayList<BrandVo>();
		if(CollectionUtils.isNotEmpty(brandBoList)){
			for (BrandBo brandBo : brandBoList) {
				BrandVo brandVo = convertBrandBoToVo(brandBo);
				brandVoList.add(brandVo);
			}

		}

		JSONObject filter = data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER);
		if (null != filter) {
			filter.remove(SearchConstants.NodeConstants.FILTER_KEY_BRAND);
			filter.put(SearchConstants.NodeConstants.FILTER_KEY_BRAND, brandVoList);
		}
	}

	/**
	 * 多品店根据shopId来查,单品店根据品牌来查询
	 * @param req
	 * @param shop
	 */
	@Override
	public void buildShopSearchParam(ProductSearchReq req, String shop) {
		if(StringUtils.isEmpty(shop)){
			return;
		}
		List<ShopsBo> shopsBoList = shopService.getShopBoList(Lists.newArrayList(Integer.valueOf(shop)));
		if(CollectionUtils.isNotEmpty(shopsBoList)){
			ShopsBo shopsBo = shopsBoList.get(0);
			//是单品店，设置品牌id
			if(1==shopsBo.getShopsType()){
				List<BrandBo> brandBoList = shopService.getBrandListByShopId(Integer.valueOf(shop));
				if(CollectionUtils.isNotEmpty(brandBoList)){
					req.setBrand(String.valueOf(brandBoList.get(0).getId()));
				}
			}else{
				req.setShop(shop);
			}
		}
	}

	private BrandVo convertBrandBoToVo(BrandBo brandBo) {
		BrandVo brandVo = new BrandVo();
		brandVo.setId(String.valueOf(brandBo.getId()));
		brandVo.setBrandId(brandBo.getId());
		brandVo.setBrandName(brandBo.getBrandName());
		brandVo.setBrandDomain(brandBo.getBrandDomain());
		brandVo.setBrandIco(brandBo.getBrandIco());
		brandVo.setBrandAlif(brandBo.getBrandAlif());
		brandVo.setIsHot(brandBo.getIsHot());
		return brandVo;
	}
}