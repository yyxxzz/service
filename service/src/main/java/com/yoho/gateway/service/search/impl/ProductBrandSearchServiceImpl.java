package com.yoho.gateway.service.search.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.KeyBuilder;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.model.product.BrandIntroVo;
import com.yoho.gateway.model.product.CouponsVo;
import com.yoho.gateway.service.search.BrandService;
import com.yoho.gateway.service.search.ProductBrandSearchService;
import com.yoho.gateway.service.search.SalesCategoryService;
import com.yoho.gateway.service.search.ShopService;
import com.yoho.gateway.service.search.wrapper.SearchRestTemplateWrapper;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.gateway.utils.PinYinUtils;
import com.yoho.gateway.utils.StripTagsUtil;
import com.yoho.product.model.BrandBo;
import com.yoho.product.model.CouponsBo;
import com.yoho.product.model.ShopsBo;
import com.yoho.product.model.ShopsBrandsBo;
import com.yoho.product.request.BaseRequest;
import com.yoho.product.request.FavoriteRequest;
import com.yoho.service.model.promotion.ProductBrandCouponsBo;
import com.yoho.service.model.promotion.request.BrandCouponsReq;
import com.yoho.service.model.promotion.request.CouponsLogReq;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by sailing on 2015/11/21.
 */
@Service
public class ProductBrandSearchServiceImpl extends AbstractProductSearchService implements ProductBrandSearchService {

	private final Logger logger = LoggerFactory.getLogger(ProductBrandSearchServiceImpl.class);
	
	@Autowired
	private CacheClient cacheClient;
	
	@Autowired
	private SearchRestTemplateWrapper searchRestTemplateWrapper;
		
	@Autowired
	private ServiceCaller serviceCaller;

	@Autowired
	private SalesCategoryService salesCategoryService;

	@Autowired
	private BrandService brandService;

	@Autowired
	private MemecacheClientHolder memecacheClientHolder;

	@Autowired
	private ShopService shopService;

	private SerializerFeature feature = SerializerFeature.DisableCircularReferenceDetect;

	@Value("${ip.port.search.server}")
    private String searchServerIpAndPort;
	
	private static final int PRD_STATUS_USE = 1;

	private final static long DIFF_7_DAYS = 604800L;
	
	private final static String YES = "Y";
	
	private final static String NO = "N";

	/**
	 * 数字数组
	 */
	private final static Integer[] DIGITALS = new Integer[]{0,1,2,3,4,5,6,7,8,9};
	/**
	 * 数字列表
	 */
	private static final List<Integer> DIGITALLIST = new ArrayList<Integer>(Arrays.asList(DIGITALS));
	/**
	 * 根据频道查询所有品牌
	 * @return
	 * @throws ServiceException
	 */
	@Override
	public JSONObject queryBrandListByChannl(String channel)throws ServiceException {

		JSONObject data = searchAllBrands(channel);
		
		JSONObject brands=data.getJSONObject("brands");
		reBuildBrands(brands);
		if (null!=brands&&!brands.isEmpty()) {
			data.put("brands", brands);
			return data;
		}
		return null;
	}

	@Override
	public Map<String, Object> queryNewBrandListByChannel(String channel)throws ServiceException {

		JSONObject data = searchAllBrands(channel);
		JSONObject brands=data.getJSONObject("brands");
		
		Map<String, Object> result = reBuildBrandList(brands);
		if (null!=result&&!result.isEmpty()) {
			return result;
		}
		return null;
	}
	
	private JSONObject searchAllBrands(String channel){
		Map<String, Object> params = buildBrandListParams(channel);
		String url=null;
		String responseBody=null;
		try {
			String paramsStr = com.yoho.gateway.utils.StringUtils.convertUrlParamStrFromMap2(params);
			logger.info("[ProductBrandSearchServiceImpl][queryBrand] is  convertUrlParamStrFromMap {}",paramsStr);
			url=getBrandListUrl() + "?"+ paramsStr;
			logger.info("begin [class:ProductBrandSearchServiceImpl][queryBrandListByChannl] is url: {}",url);
			responseBody = searchRestTemplateWrapper.getForObject("search.brandlist",url, String.class, params);
			logger.debug("HTTP GET success. url: {}, response: {}",getBrandListUrl(), responseBody);
			if(StringUtils.isBlank(responseBody)){
				return null;
			}
			JSONObject jsonBody =JSONObject.parseObject(responseBody);
			if(null==jsonBody){
				return null;
			}
			JSONObject data=jsonBody.getJSONObject("data");
			if(null==data){
				return null;
			}
			
			return data;
		} catch (Exception e) {
			logger.error("some bad thing occur url is：{}", url,e);
			throw new ServiceNotAvaibleException("search server!!!", e);
		}
	}

	private Map<String, Object> buildBrandListParams(String channel){
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("status", PRD_STATUS_USE);
		params.put("gender", super.processGender(channel, null));
		params.put("viewNum", 1);
		//设置库存大于等于1
		params.put("stocknumber", 1);
		params.put("outlets", 2);
		params.put("attribute_not", 2);
        // 要全球购商品
        params.put("contain_global","Y");
		//根据运营品类获取物理品类第三级
		if (com.yoho.gateway.utils.StringUtils.isNotBlank(channel)){
			String sales_category_list = salesCategoryService.queryRelationParamter(channel);
			logger.info("salesCategoryService.queryRelationParamter({}) is {}",channel,sales_category_list);
			boolean isNotBlank = com.yoho.gateway.utils.StringUtils.isNotBlank(sales_category_list);
			params.put("sort",isNotBlank ? sales_category_list : "");
		}
		return params;
	}
	/**
	 * 后置处理（图片补全，数字品牌的重新分类）
	 * @param brands
	 */
	private void reBuildBrands(final JSONObject brands) {
		boolean isEmpty = brands.isEmpty();
		if (!isEmpty) {
			Set<String> keys = brands.keySet();
			JSONArray digitalBrandList = new JSONArray();
			Set<String> matchedDigitals = new HashSet<String>();
			Iterator<String> brandNameFirstCharIter = keys.iterator();
			Map<Integer, BrandBo> allBrandsMap = brandService.queryAllBrandList();
			Map<Integer, List<ShopsBrandsBo>> allshopBrandsMap = shopService.queryAllShopBrandList(Lists.newArrayList(allBrandsMap.keySet()));

			while (brandNameFirstCharIter.hasNext()) {
				String key = brandNameFirstCharIter.next();
				JSONArray singleCharBrands = brands.getJSONArray(key);
				if(null==singleCharBrands||singleCharBrands.size()<=0){
					continue;
				}
				//1.图片补全,判断是否为新品牌（is_new控制）
				JSONArray rCharBrands = completeBrands(singleCharBrands, allBrandsMap, allshopBrandsMap);
				brands.replace(key, rCharBrands);
				//2.数字品牌的重新分类
				groupDigitalCategory( key, digitalBrandList, rCharBrands, matchedDigitals);
			}
			for(String digital : matchedDigitals){
				brands.remove(digital);
			}
			JSONObject tempBrands = new JSONObject();
			tempBrands.put("0~9",digitalBrandList);
			tempBrands.putAll(brands);
			brands.clear();
			brands.putAll(tempBrands);
		}
	}

	/**
	 * 后置处理（图片补全，数字品牌的重新分类）
	 * @param brands
	 */
	private Map<String, Object> reBuildBrandList(final JSONObject brands) {
		boolean isEmpty = brands.isEmpty();
		if (!isEmpty) {
			Set<String> keys = brands.keySet();
			List<JSONObject> newBrandList = Lists.newArrayList();
			List<JSONObject> hotBrandList = Lists.newArrayList();
			JSONArray digitalBrandList = new JSONArray();
			Set<String> matchedDigitals = new HashSet<String>();
			Iterator<String> brandNameFirstCharIter = keys.iterator();
			Map<Integer, BrandBo> allBrandsMap = brandService.queryAllBrandList();
			Map<Integer, List<ShopsBrandsBo>> allshopBrandsMap = shopService.queryAllShopBrandList(Lists.newArrayList(allBrandsMap.keySet()));

			while (brandNameFirstCharIter.hasNext()) {
				String key = brandNameFirstCharIter.next();
				JSONArray singleCharBrands = brands.getJSONArray(key);
				if(null==singleCharBrands||singleCharBrands.size()<=0){
					continue;
				}
				//1.图片补全,判断是否为新品牌（is_new控制）及排序
				JSONArray rCharBrands = completeBrandList(singleCharBrands, allBrandsMap, allshopBrandsMap, newBrandList, hotBrandList);
				brands.replace(key, rCharBrands);
				//2.数字品牌的重新分类
				groupDigitalCategory(key, digitalBrandList, rCharBrands, matchedDigitals);

			}
			for(String digital : matchedDigitals){
				brands.remove(digital);
			}
			brands.remove("");
			JSONObject tempBrands = new JSONObject();
			tempBrands.put("0~9",digitalBrandList);
			tempBrands.putAll(brands);
			brands.clear();
			brands.putAll(tempBrands);
			Map<String, Object> map = new HashMap<String, Object>();
			//为newBrandList按shelves_brand_time降序排列
			Collections.sort(newBrandList, new Comparator<JSONObject>() {
	            public int compare(JSONObject arg0, JSONObject arg1) {
	                return arg1.getInteger("shelves_brand_time").compareTo(arg0.getInteger("shelves_brand_time"));
	            }
	        });
			map.put("all_list", brands);
			map.put("new_list", newBrandList);
			map.put("hot_list", hotBrandList);
			return map;
		}

		return null;
	}

	/**
	 * 图片补全
	 * @param singleCharBrands 首字母相同的品牌集合
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private JSONArray completeBrands(JSONArray singleCharBrands,final Map<Integer, BrandBo> allBrandsMap, Map<Integer, List<ShopsBrandsBo>> allshopBrandsMap){
		Iterator listIterator = singleCharBrands.listIterator();
		JSONArray rCharBrands = new JSONArray();
		while (listIterator.hasNext()) {
			JSONObject brand = (JSONObject) listIterator.next();
			if(brand==null){
				continue;
			}
			completeBrandIconUrl(brand);
			completeIsShowNew(brand,allBrandsMap);
			completeShopInfo(brand,allshopBrandsMap);
			rCharBrands.add(brand);
		}
		return rCharBrands;
	}




	/**
	 * 图片补全
	 * @param singleCharBrands 首字母相同的品牌集合
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private JSONArray completeBrandList(JSONArray singleCharBrands,final Map<Integer, BrandBo> allBrandsMap, Map<Integer,
			List<ShopsBrandsBo>> allshopBrandsMap, List<JSONObject> newBrandList, List<JSONObject> hotBrandList){
		Iterator listIterator = singleCharBrands.listIterator();
		JSONArray rCharBrands = new JSONArray();
		while (listIterator.hasNext()) {
			JSONObject brand = (JSONObject) listIterator.next();
			if(brand==null){
				continue;
			}
			completeBrandIconUrl(brand);
			completeShopInfo(brand,allshopBrandsMap);
			completeIsShowNewAndAddNewToList(brand, allBrandsMap, newBrandList);
			rCharBrands.add(brand);
		}
		return sortBrands(rCharBrands, hotBrandList);
	}

	private JSONArray sortBrands(JSONArray brands, List<JSONObject> hotBrandList){
		Map<String, JSONObject> brandMap = new HashMap<String, JSONObject>();
		List<String> brandNameList = Lists.newArrayList();
		List<String> brandNameFormatList = Lists.newArrayList();
		Map<String, String> nameFormatMap = new HashMap<String, String>();
		for(int i=0; i<brands.size(); i++){
			String brandName = brands.getJSONObject(i).getString("brand_name");
			brandMap.put(brandName, brands.getJSONObject(i));
			brandNameList.add(brandName);
			String formatName = changeToSpecialFormat(brandName, brandNameFormatList);
			brandNameFormatList.add(StringUtils.isEmpty(formatName) ? brands.getJSONObject(i).getString("brand_alif") : formatName);
			nameFormatMap.put(formatName, brandName);
		}
		Collections.sort(brandNameFormatList);
		JSONArray sortedBrands = new JSONArray();
		for(String formatName : brandNameFormatList){
			String brandName = nameFormatMap.get(formatName);
			JSONObject brand = brandMap.get(brandName);
			sortedBrands.add(brand);
			if("Y".equals(brand.getString("is_hot"))){
				hotBrandList.add(JSON.parseObject(JSON.toJSONString(brand, feature)));
			}
		}

		return sortedBrands;
	}

	private static String changeToSpecialFormat(String name, List<String> brandNameFormatList){
		if(StringUtils.isEmpty(name)){
			return name;
		}
		String resultStr = "";
		//1.去除特殊字符，只保留字母和数字
		name = StringFilter(name);
		//2.全部转成字母加数字格式且都是小写
		name = PinYinUtils.getPingYin(name).toLowerCase();
		//3.转成特殊格式，字母后加0，数字前加z，比如c5--->c0z5
		for (int i = 0; i < name.length(); i++){
		  if (Character.isDigit(name.charAt(i))){
			    resultStr += "z" + name.charAt(i);
		  }else{
			  resultStr += name.charAt(i) + "0";
		  }
		}
		//有可能出现两个品牌转化成的比较字符一样
		if(brandNameFormatList.contains(resultStr)){
			resultStr += "1";
		}
		
		return resultStr;
	}

	public static String StringFilter(String str) throws PatternSyntaxException{
        // 只允许字母和数字    清除掉所有特殊字符
        String regEx="[`~!@#$%^&*_()+=|{}':;',\\[\\].\\s-<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }

	/**
	 * 单个品牌店铺信息处理，无店铺、有单品店、无单品店有多品店
	 * 对应不同的跳转枚举值：
	 * 无店铺：0--->品牌页
	 * 无单品店有多品店：1--->搜索页
	 * 有单品店：2--->店铺页面
	 *
	 */
	private void completeShopInfo(JSONObject brand, Map<Integer, List<ShopsBrandsBo>> allshopBrandsMap) {
		Integer brandId = brand.getInteger("id");
		List<ShopsBrandsBo> shopBrandList = allshopBrandsMap.get(brandId);
    	if(CollectionUtils.isEmpty(shopBrandList)){
    		brand.put("type","0");
    		return;
    	}

    	// 判断是否存在单品店
    	for (ShopsBrandsBo shopsBrandsBo : shopBrandList) {
			if(shopsBrandsBo.getShopsType()!=null && shopsBrandsBo.getShopsType()==1){
				brand.put("type","2");
				brand.put("shop_id",shopsBrandsBo.getShopsId());
				brand.put("shop_template_type",shopsBrandsBo.getShopTemplateType());
				return;
			}
		}

    	// 无单品店有多品店
		brand.put("type","1");
	}

	/**
	 * 单个品牌图片url补全
	 * @param brand
	 */
	private void completeBrandIconUrl(JSONObject brand){
		String key_brandIco = "brand_ico";
		String background = "d2hpdGU=";
		String position = "center";
		String bucket = "brandLogo";
		String brandIco = brand.getString(key_brandIco);
		brand.replace(key_brandIco,StringUtils.isNotBlank(brandIco) ? ImagesHelper.template2(brandIco, bucket, position,background) : brandIco);
	}

	/**
	 * 品牌是否显示为新
	 * @param brand
	 */
	private void completeIsShowNew(JSONObject brand,final Map<Integer, BrandBo> allBrandsMap){
		Integer brandId_search = brand.getInteger("id");
		BrandBo brand_db = allBrandsMap.get(brandId_search);
		boolean isIn7Days = false;
		if (brand_db != null){
			Integer shelvesBrandTime = brand_db.getShelvesBrandTime();
			boolean isLegalTime = shelvesBrandTime != null && shelvesBrandTime > 1 ;
			if (isLegalTime){
				long currentTime = System.currentTimeMillis()/1000L;
				long timeDiff = Math.abs(currentTime - shelvesBrandTime);
				isIn7Days = timeDiff < DIFF_7_DAYS;
			}
		}
		String result = NO;
		if (isIn7Days){
			result = YES;
		}
		brand.put("is_show_new",result);
	}

	/**
	 * 品牌是否显示为新并整合新入驻品牌
	 * @param brand
	 */
	private void completeIsShowNewAndAddNewToList(JSONObject brand,final Map<Integer, BrandBo> allBrandsMap, List<JSONObject> newBrandList){
		Integer brandId_search = brand.getInteger("id");
		BrandBo brand_db = allBrandsMap.get(brandId_search);
		boolean isIn7Days = false;
		if (brand_db != null){
			Integer shelvesBrandTime = brand_db.getShelvesBrandTime();
			boolean isLegalTime = shelvesBrandTime != null && shelvesBrandTime > 1 ;
			if (isLegalTime){
				long currentTime = System.currentTimeMillis()/1000L;
				long timeDiff = Math.abs(currentTime - shelvesBrandTime);
				isIn7Days = timeDiff < DIFF_7_DAYS;
				brand.put("shelves_brand_time", brand_db.getShelvesBrandTime());
			}
		}
		String result = NO;
		if (isIn7Days){
			result = YES;
			newBrandList.add(JSON.parseObject(JSON.toJSONString(brand, feature)));
		}
		brand.put("is_show_new",result);
	}

	/**
	 * 聚合数字分类,收集key准备清除
	 * @param key
	 * @param digitalBrandList
	 * @param rCharBrands
	 * @param matchedDigitals
	 */
	private void  groupDigitalCategory(String key,JSONArray digitalBrandList,JSONArray rCharBrands,Set<String> matchedDigitals){
		if(NumberUtils.isNumber(key))
		{
			int digital = Integer.valueOf(key);
			if(DIGITALLIST.contains(digital)){
				digitalBrandList.addAll(rCharBrands);
				matchedDigitals.add(key);
			}
		}
	}

	/**
	 * 根据品牌ID获取品牌信息及其优惠信息
	 */
	@Override
	public BrandIntroVo queryBrandIntroById(Integer brandId, Integer uid) {
		final String key = "yh:gw:brandCoupon:" + brandId;
		ProductBrandCouponsBo brandBo = cacheClient.get(key, ProductBrandCouponsBo.class);
		//缓存为空就去调用服务查询
		if (null == brandBo) {
			BrandCouponsReq request = new BrandCouponsReq();
			request.setBrandId(brandId);
			brandBo = serviceCaller.call("promotion.getBrandCoupons", request, ProductBrandCouponsBo.class);
			//不为空放缓存 缓存5分钟
			putCache(key, brandBo, 300, new Checker() {
				@Override
				public boolean check(Object obj) {
					return null != obj;
				}
			});
		}
		if (null == brandBo) {
			logger.warn("call promotion.getBrandCoupons failed!!!");
			return null;
		}
		BrandIntroVo brandIntro = new BrandIntroVo();
		brandIntro.setBrandId(String.valueOf(brandBo.getId()));
		brandIntro.setBrandIco(brandBo.getBrandIco());
		//对html解码
		brandIntro.setBrandIntro(StringUtils.isBlank(brandBo.getBrandIntro()) ? "" : StripTagsUtil.parse(StringEscapeUtils.unescapeHtml(brandBo.getBrandIntro())));
		brandIntro.setBrandName(brandBo.getBrandName());
		brandIntro.setBrandDomain(brandBo.getBrandDomain());
		brandIntro.setIsFavorite(getIsFavorite(uid, brandId));
		buildCouponList(brandIntro, brandBo, uid);
		return brandIntro;
	}

	/*
	*     @RequestMapping(value = "/getBrandCoupons")
    @ResponseBody
    public ProductBrandCouponsBo getBrandCoupons(@RequestBody BrandCouponsReq brandCouponsReq) {
	* */
	@Override
	public ShopsBo queryShopsBoById(Integer shopid) {
		final String key="yh:gw:shop:"+shopid;
		ShopsBo shopsBo=cacheClient.get(key, ShopsBo.class);
		//缓存为空就去调用服务查询
		if(null==shopsBo)
		{
			BaseRequest<Integer> request=new BaseRequest<Integer>();
			request.setParam(shopid);
			 shopsBo=serviceCaller.call("product.getShopsIntroById", request, ShopsBo.class);
			//不为空放缓存 缓存5分钟
			putCache(key, shopsBo,300, new Checker() {
				@Override
				public boolean check(Object obj) {
					return null!=obj;
				}
			});
		}
		if (null == shopsBo) {
			logger.warn("call product.getBrandCoupons failed!!!");
			return null;
		}
		
		return shopsBo;
	}
	/**
	 * 放缓存
	 * @param key
	 * @param value
	 * @param cacheTime
	 * @param checker
	 */
	public void putCache(String key,Object value,int cacheTime,Checker checker)
	{
		if(checker.check(value))
		{	
			cacheClient.set(key, cacheTime, value);
		}
	}
	
	public interface Checker
	{
		boolean check(Object obj);
	}
	
	
	@Override
	public BrandBo queryBrandById(Integer brandId){
		BaseRequest<Integer> baseRequest = new BaseRequest<Integer>();
        baseRequest.setParam(brandId);
        String serviceName = "product.queryBrandById";
        BrandBo brandBo = serviceCaller.call(serviceName, baseRequest, BrandBo.class);
        return brandBo;
	}

	/**
	 * 获取用户是否收藏该品牌
	 * 
	 * @param uid
	 * @param brandId
	 * @return
	 */
	private String getIsFavorite(Integer uid, Integer brandId) {
		String isFavorite = "N";
		//如果用户id为空或用户ID为0就直接返回不收藏
		if(null==uid||0==uid){
			return isFavorite;
		}
		final String favoriteKey="yh:gw:favorite:brand:"+uid+":"+brandId;
		//先从缓存获取是否收藏
		String isFavoriteCache=memecacheClientHolder.getLevel1Cache().get(favoriteKey, String.class);
		if(StringUtils.isNotEmpty(isFavoriteCache)){	
			return isFavoriteCache;
		}
		FavoriteRequest favRequest = new FavoriteRequest();
		favRequest.setId(brandId);
		favRequest.setUid(uid);
		favRequest.setType("brand");
		boolean isFavB = false;
		try {
			isFavB = serviceCaller.call("product.isFavorite", favRequest,Boolean.class);
		} catch (Exception e) {
			// 捕捉异常，让流程继续走下去
			logger.warn("call product.isFavorite fail uid:{},brandId:{}",uid, brandId, e);
			isFavorite = "N";
		}
		return isFavB ? "Y" : "N";

	}

	/**
	 * 构建优惠券列表到品牌vo中
	 */
	private void buildCouponList(BrandIntroVo brandIntro,ProductBrandCouponsBo brandBo, Integer uid) {
		List<CouponsVo> couponsVoList = new ArrayList<CouponsVo>();
		brandIntro.setCouponsVoList(couponsVoList);
		List<com.yoho.service.model.promotion.CouponsBo> couponsBoList = brandBo.getCouponsBo();
		if (CollectionUtils.isEmpty(couponsBoList)) {
			return;
		} else {
			List<Integer> keyList = new ArrayList<Integer>();
			for (com.yoho.service.model.promotion.CouponsBo couponsBo : couponsBoList) {
				keyList.add(couponsBo.getId());
			}
			Map<String, Object> countMap = getCouponsStatusMap(keyList, uid);
			logger.info("ProductBrandSearchServiceImpl::getCouponsStatusMap is :{}", countMap);
			for (com.yoho.service.model.promotion.CouponsBo couponsBo : couponsBoList) {
				CouponsVo coupons = new CouponsVo();
				coupons.setCouponId(couponsBo.getId());
				coupons.setCouponName(couponsBo.getCouponName());
				coupons.setMoney(String.valueOf(couponsBo.getCouponAmount()));
				String startTime = DateUtil.getDateStrBySecond(couponsBo.getStartTime(), "yyyy.MM.dd");
				String endTime = DateUtil.getDateStrBySecond(couponsBo.getEndTime(), "yyyy.MM.dd");
				coupons.setCouponValidity(startTime + "-" + endTime);
				coupons.setCouponPic(StringUtils.isNotEmpty(couponsBo.getCouponIco()) ? ImagesHelper.template2(couponsBo.getCouponIco(), "couponImg")
						: "http://static.yohobuy.com/images/v2/activity/default_coupon.jpg");
				// 如果用户未登陆，全部设置为可领取
				if (MapUtils.isEmpty(countMap)) {
					// 1:可领取
					coupons.setStatus(1);
				} else {
					int usedNum = null == countMap.get(KeyBuilder.couponIdKeyBuilder(String.valueOf(couponsBo.getId()))) ? 0 :
							(int) countMap.get(KeyBuilder.couponIdKeyBuilder(String.valueOf(couponsBo.getId())));
					boolean userIsGet = null == countMap.get(KeyBuilder.couponIdAndUidKeyBuilder(String.valueOf(couponsBo.getId()), String.valueOf(uid))) ? false :
							(boolean) countMap.get(KeyBuilder.couponIdAndUidKeyBuilder(String.valueOf(couponsBo.getId()), String.valueOf(uid)));
					int status = getCouponStatus(usedNum, userIsGet, couponsBo.getCouponNum());
					//如果状态是已经抢光就不要展示了
					if (status == 2) {
						continue;
					}
					coupons.setStatus(getCouponStatus(usedNum, userIsGet, couponsBo.getCouponNum()));
				}
				couponsVoList.add(coupons);
			}
		}

		// 按status排序
		Collections.sort(couponsVoList, new Comparator<CouponsVo>() {
			@Override
			public int compare(CouponsVo arg0, CouponsVo arg1) {
				return arg0.getStatus().compareTo(arg1.getStatus());
			}
		});
	}

	/**
	 * 查询优惠券状态
	 * 
	 * @param couponIdList
	 * @param uid
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map<String, Object> getCouponsStatusMap(List<Integer> couponIdList,
			Integer uid) {
		// 用户ID不存在,就不需要去查询券的状态了,全部显示已经领取
		if (null == uid) {
			return Maps.newHashMap();
		}
		CouponsLogReq couponsLogReq = new CouponsLogReq();
		couponsLogReq.setUid(uid);
		couponsLogReq.setCouponIds(couponIdList);
		Map map = null;
		try {
			map = serviceCaller.call("promotion.queryCouponCount",couponsLogReq, Map.class);
		} catch (Exception e) {
			// 捕捉异常，让流程继续走下去
			logger.warn("call promotion.queryCouponCount fail couponIdList:{},uid:{}",couponIdList, uid, e);
			return null;
		}
		logger.info("getCouponsStatusMap map is:{}", map);
		return map;
	}

	/**
	 * 获取单张优惠券的领取状态
	 * 
	 * @param usedNum
	 * @param userIsGet
	 * @param couponNum
	 * @return
	 */
	private int getCouponStatus(int usedNum, boolean userIsGet, int couponNum) {
		int status;// 1:可领取 2：已抢光 3：已领取
		if (userIsGet) {
			status = 3;
		} else if (couponNum > usedNum) {
			status = 1;
		} else {
			status = 2;
		}
		return status;
	}
	
	protected String getBrandListUrl() {
        return "http://"+searchServerIpAndPort+"/yohosearch/brands.json";
    }
}