package com.yoho.gateway.service.assist;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.utils.DateUtil;

/**
 * 检索参数的处理
 * 
 * @author MALI
 *
 */
public class SearchParam {
	
	private Map<String, Object> param = new HashMap<String, Object>();
	
	private String searchFrom;

	public String getSearchFrom() {
		return searchFrom;
	}

	public SearchParam setSearchFrom(String searchFrom) {
		this.searchFrom = searchFrom;
		return this;
	}

	/**
	 * 查询的排序键值对集合，用于页面的条件和索引查询条件的转换
	 */
	public final static Map<String, String> orderMap = new HashMap<String, String>();

	static {
		orderMap.put("s_t_desc", "shelve_time:desc");
		orderMap.put("s_t_asc", "shelve_time:asc");
		orderMap.put("s_p_asc", "sales_price:asc");
		orderMap.put("s_p_desc", "sales_price:desc");
		orderMap.put("p_d_desc", "discount:desc");
		orderMap.put("p_d_asc", "discount:asc");
		orderMap.put("skn_desc", "product_skn:desc");
		orderMap.put("skn_asc", "product_skn:asc");
		orderMap.put("activities_desc", "activities.order_by:desc");
		orderMap.put("activities_asc", "activities.order_by:asc");
		orderMap.put("s_n_asc", "sales_num:asc");
		orderMap.put("s_n_desc", "sales_num:desc");
		orderMap.put("activities_id_desc", "activities.activity_id:desc");
		orderMap.put("activities_id_asc", "activities.activity_id:asc");
		orderMap.put("s_s_desc", "storageNum:desc");
		orderMap.put("s_s_asc", "storageNum:asc");
		orderMap.put("d_s_desc", "discount_score:desc");
		orderMap.put("d_s_asc", "discount_score:asc");
	}

	/**
	 * 转成检索使用的对象字段名称
	 * 
	 * @param req
	 * @param needFilter
	 *            是否需要查询过滤条件
	 * @return
	 */
	public SearchParam buildSearchParam(ProductSearchReq req, boolean needFilter) {
		setOrder(req.getOrder()).setSort(req.getSort()).setType(req.getType())
				.setBrand(req.getBrand()).setShop(req.getShop()).setSize(req.getSize())
				.setColor(req.getColor()).setPrice(req.getPrice())
				.setPromotion(req.getPromotion()).setPd(req.getPd())
				.setMsort(req.getMsort()).setMisort(req.getMisort())
				.setQuery(req.getQuery()).setShelveTime(req.getShelveTime())
				.setFirstShelveTime(req.getFirstShelveTime()).setStoragenumber(req.getStorageNum())
				.setNotProductSkn(req.getNotProductSkn()).setBreaking(req.getBreaking()).setOutlets(req.getOutlets())
				.setVdt(req.getVdt()).setProductPool(req.getProductPool())
				.setViewNum(req.getLimit()).setPage(req.getPage()).setStatus().setIsDiscount(req.getIsdiscount())
				.setStocknumber(req.getStocknumber()).setAttributenot().setFrom(req.getFrom()).setStoragenumber(req.getStocknumber())
				.setNeedFilter(req, needFilter).setActTemp(req.getActTemp()).setContainGlobal(req).setAgeLevel(req.getAgeLevel());

		return this;
	}


    /**
	 * 自主品牌的条目字段 每次查询的条数
	 *
	 * @param limit
	 */
	public SearchParam setLimit(Integer limit) {
		param.put(SearchConstants.IndexNameConstant.LIMIT, null != limit ? limit : SearchConstants.IndexNameConstant.DEFAULT_LIMIT);
		return this;
	}

	public SearchParam setDayLimit(String dayLimit) {
		// 3天内上新的
		if ("1".equals(dayLimit)) {
			setShelveTime(DateUtil.getIntervalTimeSecond(-3) + ","
					+ DateUtil.getCurrentTimeSecond());
			setOrder("s_t_desc");
		} else if ("2".equals(dayLimit)) {
			// 一周内上新
			setShelveTime(DateUtil.getIntervalTimeSecond(-7) + ","
					+ DateUtil.getCurrentTimeSecond());
			setOrder("s_s_desc");
		} else if ("3".equals(dayLimit)) {
			// 一个月内上新
			setFirstShelveTime(DateUtil.getIntervalTimeSecond(-30) + ","
					+ DateUtil.getCurrentTimeSecond());
			setOrder("s_n_desc");
		} else {
			setShelveTime(DateUtil.getIntervalTimeSecond(-3) + ","
					+ DateUtil.getCurrentTimeSecond());
			setOrder("s_t_desc");
		}
		return this;
	}

	/**
	 * 设置排序
	 *
	 * @param order
	 */
	public SearchParam setOrder(String order) {
		if (StringUtils.isNotEmpty(order)) {
			param.put(SearchConstants.IndexNameConstant.ORDER,orderMap.get(order));
		}
		return this;
	}

	public SearchParam setAgeLevel(String ageLevel) {
		if (StringUtils.isNotEmpty(ageLevel)) {
			param.put(SearchConstants.IndexNameConstant.AGE_LEVEL,ageLevel);
		}
		return this;
	}

	/**
	 * 设置小分类Id
	 *
	 * @param sort
	 */
	public SearchParam setSort(String sort) {
		if (StringUtils.isNotEmpty(sort)) {
			param.put(SearchConstants.IndexNameConstant.SORT, sort);
		}
		return this;
	}

	/**
	 * 设置类型
	 *
	 * @param type
	 */
	public SearchParam setType(String type) {
		if (StringUtils.isNotEmpty(type)
				&& SearchConstants.IndexNameConstant.NEW.equals(type)) {
			param.put(SearchConstants.IndexNameConstant.NEW,SearchConstants.IndexNameConstant.IS_NEW);
		}
		return this;
	}
	/**
	 * 设置断码区
	 * @param breaking 为1是断码区
	 * @return
	 */
	public SearchParam setBreaking(String breaking) {
		if (SearchConstants.IndexNameConstant.DEFAULT_SALL_FILTER.equals(breaking)) {
			param.put(SearchConstants.IndexNameConstant.KEY_BREACKING,breaking);
		}
		return this;
	}
	/**
	 * 设置折扣专场
	 * @param filterSaleAction 为1是折扣专场
	 * @return
	 */
	public SearchParam setFilterSaleAction(String filterSaleAction) {
		if (SearchConstants.IndexNameConstant.DEFAULT_SALL_FILTER.equals(filterSaleAction)) {
			param.put(SearchConstants.IndexNameConstant.KEY_FILTER_SALEACTION,filterSaleAction);
		}
		return this;
	}

	/**
	 * 设置尺寸
	 *
	 * @param size
	 */
	public SearchParam setSize(String size) {
		if (StringUtils.isNotEmpty(size)) {
			param.put(SearchConstants.IndexNameConstant.SIZE, size);
		}
		return this;
	}

	/**
	 * 设置品牌
	 *
	 * @param brand
	 */
	public SearchParam setBrand(String brand) {
		brand = com.yoho.gateway.utils.StringUtils.converInt(brand,
				SearchConstants.IndexNameConstant.SEPERATOR_COMMA);
		if (StringUtils.isNotEmpty(brand)) {
			param.put(SearchConstants.IndexNameConstant.BRAND, brand);
		}
		return this;
	}

	/**
	 * 设置店铺
	 *
	 * @param shop
	 */
	public SearchParam setShop(String shop) {
		shop = com.yoho.gateway.utils.StringUtils.converInt(shop,
				SearchConstants.IndexNameConstant.SEPERATOR_COMMA);
		if (StringUtils.isNotEmpty(shop)) {
			param.put(SearchConstants.IndexNameConstant.SHOP, shop);
		}
		return this;
	}

	/**
	 * 设置颜色
	 *
	 * @param color
	 */
	public SearchParam setColor(String color) {
		color = com.yoho.gateway.utils.StringUtils.converInt(color,
				SearchConstants.IndexNameConstant.SEPERATOR_COMMA);
		if (StringUtils.isNotEmpty(color)) {
			param.put(SearchConstants.IndexNameConstant.COLOR, color);
		}
		return this;
	}

	/**
	 * 设置价格区间
	 *
	 * @param price
	 */
	public SearchParam setPrice(String price) {
		if (StringUtils.isNotEmpty(price)) {
			param.put(SearchConstants.IndexNameConstant.PRICE, price);
		}
		return this;
	}
	public SearchParam setFrom(String from) {
		if (StringUtils.isNotEmpty(from)) {
			param.put(SearchConstants.IndexNameConstant.FROM, from);
		}
		return this;
	}
	public SearchParam setIsDiscount(String isDiscount) {
		if (StringUtils.isNotEmpty(isDiscount)) {
			param.put(SearchConstants.IndexNameConstant.SALE_IS_DISCOUNT, isDiscount);
		}
		return this;
	}
	public SearchParam setVdt(String vdt) {
		if ("1".equals(vdt)) {
			param.put(SearchConstants.IndexNameConstant.VDT, vdt);
		}
		return this;
	}
	public SearchParam setProductPool(String productPool) {
		if (StringUtils.isNotEmpty(productPool)) {
			param.put(SearchConstants.IndexNameConstant.PRODUCT_POOL, productPool);
		}
		return this;
	}

	/**
	 * 设置促销信息
	 *
	 * @param promotion
	 */
	public SearchParam setPromotion(String promotion) {
		if (StringUtils.isNotEmpty(promotion)) {
			param.put(SearchConstants.IndexNameConstant.PROMOTION, promotion);
		}
		return this;
	}

	/**
	 * 设置折扣区间
	 *
	 * @param pd
	 */
	public SearchParam setPd(String pd) {
		if (StringUtils.isNotEmpty(pd)) {
			param.put(SearchConstants.IndexNameConstant.PD, pd);
		}
		return this;
	}

	/**
	 * 设置产品中分类ID
	 *
	 * @param misort
	 */
	public SearchParam setMisort(String misort) {
		if (StringUtils.isNotEmpty(misort)) {
			param.put(SearchConstants.IndexNameConstant.MISORT, misort);
		}
		return this;
	}

	/**
	 * 设置模糊查询词
	 *
	 * @param query
	 */
	public SearchParam setQuery(String query) {
		if (StringUtils.isNotEmpty(query)) {
			param.put(SearchConstants.IndexNameConstant.QUERY, query);
		}
		return this;
	}

	/**
	 * 设置模糊查询词
	 *
	 * @param shelveTime
	 */
	public SearchParam setShelveTime(String shelveTime) {
		if (StringUtils.isNotEmpty(shelveTime)) {
			param.put(SearchConstants.IndexNameConstant.SHELVETIME, shelveTime);
		}
		return this;
	}

	/**
	 * 设置参数列表
	 *
	 * @param parameter
	 */
	public SearchParam setParameter(String[] parameter) {
		if (parameter != null && parameter.length > 0) {
			for (String para : parameter) {
				String[] arr = para.split("\\_");
				if (arr.length > 0) {
					param.put(SearchConstants.IndexNameConstant.PARAMETER
							+ arr[0], arr[1]);
				}
			}
		}
		return this;
	}

	public SearchParam removeParameter() {
		Iterator<String> iterator=param.keySet().iterator();
		while(iterator.hasNext()){
			String key = iterator.next();
			if(key.contains(SearchConstants.IndexNameConstant.PARAMETER)){
				iterator.remove();
			}
		}
		return this;
	}

	/**
	 * 设置首次上架时间戳的区间,逗号分隔
	 *
	 * @param firstShelveTime
	 */
	public SearchParam setFirstShelveTime(String firstShelveTime) {
		if (StringUtils.isNotEmpty(firstShelveTime)) {
			param.put(SearchConstants.IndexNameConstant.FIRSTSHELVETIME,
					firstShelveTime);
		}
		return this;
	}

	/**
	 * 设置需要排除掉的商品id,逗号分隔
	 *
	 * @param notProductSkn
	 */
	public SearchParam setNotProductSkn(String notProductSkn) {
		if (StringUtils.isNotEmpty(notProductSkn)) {
			param.put(SearchConstants.IndexNameConstant.NOTPRODUCTSKN,
					notProductSkn);
		}
		return this;
	}

	public SearchParam setGender(Integer channel) {
		if (null == channel) {
			return this;
		}
		param.put(SearchConstants.IndexNameConstant.GENDER, getGender(channel));
		return this;
	}

	public SearchParam setGender(String gender) {
		param.put(SearchConstants.IndexNameConstant.GENDER, gender);
		return this;
	}

	// 假如传值1 则需要查询1,3 假如查询2 则需要查询2,3
	public SearchParam setGender(String yh_channel, String gender) {
		if (StringUtils.isBlank(gender) && StringUtils.isNotBlank(yh_channel)) {
			if ("1".equals(yh_channel)) {
				gender = "1,3";
			} else if ("2".equals(yh_channel)) {
				gender = "2,3";
			}
		}
		param.put(SearchConstants.IndexNameConstant.GENDER, gender);

		return this;
	}

	public SearchParam setStatus() {
		this.setStatus(SearchConstants.IndexNameConstant.PRD_STATUS_USE);
		return this;
	}

	/**
	 * 设置状态
	 *
     * @param req
     * @param needFilter
     */
	public SearchParam setNeedFilter(ProductSearchReq req, boolean needFilter) {
		if (null == req && !needFilter) {
            return this;
        }
		if (null == req && needFilter) {
        	param.put(SearchConstants.IndexNameConstant.NEEDFILTER,
					SearchConstants.IndexNameConstant.DEFAULT_NEEDFILTER);
            return this;
        }
		// 第一页且需要查询过滤条件
		if ((null == req.getPage() || 1 == req.getPage()) && needFilter) {
			param.put(SearchConstants.IndexNameConstant.NEEDFILTER,
					SearchConstants.IndexNameConstant.DEFAULT_NEEDFILTER);
            return this;
		}
		if(param.get(SearchConstants.IndexNameConstant.KEY_BREACKING)!=null&&"1".equals(param.get(SearchConstants.IndexNameConstant.KEY_BREACKING).toString())){
			param.put(SearchConstants.IndexNameConstant.NEEDFILTER,
					SearchConstants.IndexNameConstant.DEFAULT_NEEDFILTER);
		}
        // h5和pc都需要查询过滤条件
        if ("web".equals(req.getClientType()) || "h5".equals(req.getClientType())) {
            param.put(SearchConstants.IndexNameConstant.NEEDFILTER,
                    SearchConstants.IndexNameConstant.DEFAULT_NEEDFILTER);
        }
		return this;
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 */
	public SearchParam setStatus(Integer status) {
		param.put(SearchConstants.IndexNameConstant.STATUS, status);
		return this;
	}

	/**
	 * 设置品牌信息
	 * 
	 * @param brandIds
	 */
	public SearchParam setBrand(List<Integer> brandIds) {
		if (CollectionUtils.isNotEmpty(brandIds)) {
			param.put(SearchConstants.IndexNameConstant.BRAND_ID,
					StringUtils.join(brandIds, ","));
		}
		return this;
	}

	/**
	 * 设置品牌信息
	 * 
	 * @param skns
	 */
	public SearchParam setProductSkn(List<Integer> skns) {
		if (CollectionUtils.isNotEmpty(skns)) {
			param.put(SearchConstants.IndexNameConstant.QUERY,StringUtils.join(skns, ","));
		}
		return this;
	}

	public SearchParam setStocknumber() {
		this.setStocknumber(SearchConstants.IndexNameConstant.DEFAULT_STOCKNUMBER);
		return this;
	}

	/**
	 * @param stocknumber
	 */
	public SearchParam setStocknumber(Integer stocknumber) {
		if (stocknumber == null) {
			setStocknumber();
			return this;
		}
		if (stocknumber == -1) { // -1 时，有库存没有库存都查询
			return this;
		}
		param.put(SearchConstants.IndexNameConstant.STOCKNUMBER, stocknumber);
		return this;
	}
	public SearchParam setStoragenumber(Integer stocknumber) {
		if(stocknumber!=null){
			param.put(SearchConstants.IndexNameConstant.STORAGE_NUMBER,stocknumber);
		}
		return this;
	}

	/**
	 * @param outlets
	 */
	public SearchParam setOutlets(Integer outlets) {
		param.put(SearchConstants.IndexNameConstant.OUTLETS,
				null != outlets ? outlets
						: SearchConstants.IndexNameConstant.DEFAULT_OUTLETS);
		return this;
	}

	public SearchParam setAttributenot() {
		this.setAttributenot(SearchConstants.IndexNameConstant.DEFAULT_ATTRIBUTE_NOT);
		return this;
	}

	/**
	 * @param attribute_not
	 */
	public SearchParam setAttributenot(Integer attribute_not) {
		param.put(SearchConstants.IndexNameConstant.ATTRIBUTE_NOT,null != attribute_not ? attribute_not
						: SearchConstants.IndexNameConstant.DEFAULT_ATTRIBUTE_NOT);
		return this;
	}

	/**
	 * @return
	 */
	public SearchParam setViewNum() {
		this.setViewNum(SearchConstants.IndexNameConstant.DEFAULT_VIEWNUM);
		return this;
	}

	/**
	 * 设置产品大分类ID
	 * 
	 * @param msort
	 * @return
	 */
	public SearchParam setMsort(String msort) {
		if (StringUtils.isNotBlank(msort)) {
			param.put(SearchConstants.IndexNameConstant.MSORT, msort);
		}
		return this;
	}

	/**
	 * 每次查询的条数
	 * 
	 * @param viewNum
	 */
	public SearchParam setViewNum(Integer viewNum) {
		param.put(SearchConstants.IndexNameConstant.VIEWNUM,null != viewNum ? viewNum:SearchConstants.IndexNameConstant.DEFAULT_VIEWNUM);
		return this;
	}

	/**
	 * 每次查询的条数
	 * 
	 * @param page
	 */
	public SearchParam setPage(Integer page) {
		param.put(SearchConstants.IndexNameConstant.PAGE, null != page ? page: SearchConstants.IndexNameConstant.DEFAULT_PAGE);
		return this;
	}

	public SearchParam setShelveTime() {
		// param.put(SearchConstants.IndexNameConstant.SHELVE_TIME,StringUtils.join(new
		// Object[] {(DateUtils.getDateOfSenconds() - (86400 *
		// 30)),DateUtils.getDateOfSenconds()}, ","));
		return this;
	}

	/**
	 * get gender value: string
	 * 
	 * @param channel
	 * @return
	 */
	public static String getGender(Integer channel) {
		String gender = "";
		if (channel == 1) {
			gender = "1,3";
		} else if (channel == 2) {
			gender = "2,3";
		} else {
			gender = String.valueOf(channel);
		}
		return gender;
	}

	public String toParamString() {
		return com.yoho.gateway.utils.StringUtils.convertUrlParamStrFromMap2(param);
	}

	public Map<String, Object> getParam() {
		return param;
	}

	public SearchParam setNeedSmallSort() {
		param.put(SearchConstants.IndexNameConstant.NEEDSMALLSORT,SearchConstants.IndexNameConstant.NEEDSMALLSORT_YES);
		return this;
	}

	public SearchParam setParamNum(Integer paramNum) {
		param.put(SearchConstants.IndexNameConstant.PARAMNUM,paramNum);
		return this;
	}

	public String getGender() {
		return (String) (getParam().get(SearchConstants.IndexNameConstant.GENDER));
	}

	public String getNeedFilter() {
		return (String) (getParam().get(SearchConstants.IndexNameConstant.NEEDFILTER));
	}

	public Integer getPage() {
		return (Integer) (getParam().get(SearchConstants.IndexNameConstant.PAGE));
	}

	public SearchParam setTopSearchGender(String yhChannel) {
		String gender = "";
		if ("1".equals(yhChannel)) {
			gender = "1,3";
		} else if ("2".equals(yhChannel)) {
			gender = "2,3";
		} else {
			gender = "";
		}
		param.put(SearchConstants.IndexNameConstant.GENDER, gender);
		return this;
	}

	public SearchParam setDayLimit4OwnBrand(String dayLimit) {
		if ("1".equals(dayLimit)) {
			setShelveTime(DateUtil.getIntervalTimeSecond(-3) + ","
					+ DateUtil.getCurrentTimeSecond());
			setOrder("s_t_desc");
		} else if ("2".equals(dayLimit)) {
			// 一周内上新
			setShelveTime(DateUtil.getIntervalTimeSecond(-7) + ","
					+ DateUtil.getCurrentTimeSecond());
			setOrder("s_s_desc");
		} else if ("3".equals(dayLimit)) {
			// 一个月内上新
			setShelveTime(DateUtil.getIntervalTimeSecond(-30) + ","
					+ DateUtil.getCurrentTimeSecond());
			setOrder("s_n_desc");
		} else {
			setShelveTime(DateUtil.getIntervalTimeSecond(-3) + ","
					+ DateUtil.getCurrentTimeSecond());
			setOrder("s_t_desc");
		}
		return this;
	}

	/**
	 * add activity template
	 * @param actTemp
	 * @return
	 */
	public SearchParam setActTemp(String actTemp){
		if (StringUtils.isNotBlank(actTemp)) {
			param.put(SearchConstants.IndexNameConstant.ACT_TEMP, actTemp);
		}
		return this;
	}

    /**
     * 4.6版本之后全部需要加contain_global
     * @param req
     * @return
     */
    private SearchParam setContainGlobal(ProductSearchReq req) {
        if (req.getIncludeGlobal().equals("N")) {
            param.put(SearchConstants.IndexNameConstant.CONTAIN_GLOBAL, "N");
        } else if (StringUtils.isNotBlank(req.getAppVersion())  && req.getAppVersion().compareTo("4.6")>=0) {
            param.put(SearchConstants.IndexNameConstant.CONTAIN_GLOBAL, "Y");
        }
        return this;
    }
    
    public SearchParam setPromotionDiscount(double discount)
	{
		param.put(SearchConstants.IndexNameConstant.FILTER_PROMOTIONDISCOUNT,discount);
		return this;
	}
}
