package com.yoho.gateway.service.assist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.yoho.gateway.model.search.ProductSearchReq;

/**
 * 查询参数的相关转换辅助类
 * @author yoho
 *
 */
public class QueryParamConvert {
	private static final String SEPRATOR_COMMA = ",";
	
	
	/**
	 * 查询的排序键值对集合，用于页面的条件和索引查询条件的转换
	 */
	public final static Map<String, String> orderMap = new HashMap<String, String>();
	
	static{
		orderMap.put("s_t_desc",  "shelve_time:desc");
		orderMap.put("s_t_asc",  "shelve_time:asc");
		orderMap.put("s_p_asc",  "sales_price:asc");
		orderMap.put("s_p_desc",  "sales_price:desc");
		orderMap.put("p_d_desc",  "discount:desc");
		orderMap.put("p_d_asc",  "discount:asc");
		orderMap.put("skn_desc",  "product_skn:desc");
		orderMap.put("skn_asc",  "product_skn:asc");
		orderMap.put("activities_desc",  "activities.order_by:desc");
		orderMap.put("activities_asc",  "activities.order_by:asc");
		orderMap.put("s_n_asc",  "sales_num:asc");
		orderMap.put("s_n_desc",  "sales_num:desc");
		orderMap.put("activities_id_desc",  "activities.activity_id:desc");
		orderMap.put("activities_id_asc",  "activities.activity_id:asc");
		orderMap.put("s_s_desc",  "storageNum:desc");
		orderMap.put("s_s_asc",  "storageNum:asc");
	}
	
	/**
	 * 查询参数相关转换
	 * @param map
	 */
	public static final Map<String, String> filterParamMap(ProductSearchReq req) {
		Map<String,String> paramMap = new HashMap<String, String>();
				
		if (null != req.getAttributeNot()) {
			paramMap.put("attribute_not", String.valueOf(req.getAttributeNot()));
		}
		
		//新品到着自主品牌限制
        if (null != req.getLimit()) {
        	paramMap.put("limit", String.valueOf(req.getLimit()));
        }
		
		// 是否返回过滤条件
        if (StringUtils.isNotEmpty(req.getNeedFilter())) {
        	paramMap.put("needFilter", "1");
        }
        
        // 排序参数
        if (StringUtils.isNotEmpty(req.getOrder())) {
        	paramMap.put("order", getorder(req.getOrder()));
        }
        
        if (null != req.getOutlets() && req.getOutlets() == 2) {
        	paramMap.put("outlets", "2");
        } else {
        	paramMap.put("outlets", "1");
        }
        
		
		if (null != req.getPage() && req.getPage() > 0) {
			paramMap.put("page", String.valueOf(req.getPage()));
        }
		
		if (StringUtils.isNotEmpty(req.getSort())) {
			paramMap.put("sort", req.getSort());
		}
		
        // 产品是否上架的状态
        if (null != req.getStatus()) {
        	paramMap.put("status", String.valueOf(req.getStatus()));
        }
        
        // 设置库存大于等于的数量,为0时则查询库存为0的
        if (null != req.getStocknumber()) {
        	paramMap.put("stocknumber", "1");
        }
        String type = req.getType();
        if (StringUtils.isNotEmpty(req.getType())) {
        	if ("new".equals(type)) {
        		paramMap.put("new", "Y");
        	}
        	/*if ("discount".equals(type)) {
        		paramMap.put("new", "Y");
        	}*/
        	// discount  price  new
        	// paramMap.put("type", req.getType());
        }
        
		// map.put("userId", req.getUserId());
		
        if (null != req.getViewNum() && (req.getViewNum() >= 0 && req.getViewNum() < 500 )) {
        	paramMap.put("viewNum", String.valueOf(req.getViewNum()));
        }
		
		// TODO map.put("yhChannel", req.getYhChannel());
		
		
        
        String brand = com.yoho.gateway.utils.StringUtils.converInt(req.getBrand(), SEPRATOR_COMMA);
        if (StringUtils.isNotEmpty(brand)) {
        	paramMap.put("brand", brand);
        }
        
        if (StringUtils.isNotEmpty(req.getGender())) {
        	paramMap.put("gender", req.getGender());
        }
        
        String size = com.yoho.gateway.utils.StringUtils.converInt(req.getSize(), SEPRATOR_COMMA);
		if (StringUtils.isNotEmpty(size)) {
			paramMap.put("size", req.getSize());
        }
        
		/*String style = com.yoho.gateway.utils.StringUtils.converInt((String)paramMap.get("style"), SEPRATOR_COMMA);
        if (StringUtils.isEmpty(style)) {
        	paramMap.remove("style");
        }*/
        
        String color = com.yoho.gateway.utils.StringUtils.converInt(req.getColor(), SEPRATOR_COMMA);
        if (StringUtils.isNotEmpty(color)) {
        	paramMap.put("color", req.getColor());
        }
        
        String price = getPrice(req.getPrice());
        if (StringUtils.isNotEmpty(price)) {
        	paramMap.put("price", req.getPrice());
        }
        
        if (StringUtils.isNotEmpty(req.getPromotion())) {
        	paramMap.put("promotion", req.getPromotion());
        }
        
        // 折扣
        if (StringUtils.isNotEmpty(req.getPd())) {
        	paramMap.put("p_d", req.getPd());
        }
        //设置上架时间区间，逗号分隔
        if (StringUtils.isNotEmpty(req.getShelveTime())) {
        	paramMap.put("shelve_time", req.getShelveTime());
        }
        
        // 二级分类
        if (StringUtils.isNotEmpty(req.getMsort())) {
        	paramMap.put("msort", req.getMsort());
        }
        
        // 三级分类
        if (StringUtils.isNotEmpty(req.getMisort())) {
        	paramMap.put("misort", req.getMisort());
        }
        
        //设置首次上架时间戳的区间,逗号分隔
        if (StringUtils.isNotEmpty(req.getFirstShelveTime())) {
        	paramMap.put("first_shelve_time", req.getFirstShelveTime());
        }
        
        /*
        //3级分类
        String misort = com.yoho.gateway.utils.StringUtils.converInt((String)paramMap.get("misort"), SEPRATOR_COMMA);
        if (StringUtils.isEmpty(misort)) {
        	paramMap.remove("misort");
        }*/
        
        /*// 是否特价
        if (StringUtils.isNotEmpty((String)paramMap.get("specialoffer"))) {
        	paramMap.put("specialoffer", "Y");
        } else {
        	paramMap.remove("specialoffer");
        }
        
        // 是否是新品
        String newStr = getNew((String)paramMap.get("new"));
        if (null == newStr) {
        	paramMap.remove("new");
        }*/
        
        
       /* Integer paramNum = com.yoho.gateway.utils.StringUtils.parseInt((String)paramMap.get("paramNum"));
        if (null != paramNum && paramNum >= 0) {
        	paramMap.put("paramNum", paramNum);
        } else {
        	paramMap.remove("paramNum");
        }*/
	    
       /* // 是否需要所有的分类
        if (StringUtils.isNotEmpty((String)paramMap.get("needAllSort"))) {
        	paramMap.put("needAllSort", "1");
        } else {
        	paramMap.remove("needAllSort");
        }
        
        // 设置指定返回最新上架有商品的日期的数量
        Integer recentDays = com.yoho.gateway.utils.StringUtils.parseInt((String)paramMap.get("recentDays"));
        if (null != recentDays && recentDays > 1) {
        	paramMap.put("recentDays", recentDays);
        } else {
        	paramMap.remove("recentDays");
        }
        
        // 设置上架日期
        Integer shelvedate = com.yoho.gateway.utils.StringUtils.parseInt((String)paramMap.get("shelvedate"));
        if (null != shelvedate && shelvedate > 1) {
        	paramMap.put("shelvedate", shelvedate);
        } else {
        	paramMap.remove("shelvedate");
        }
        
        // 促销专区id
        Integer promotion = com.yoho.gateway.utils.StringUtils.parseInt((String)paramMap.get("promotion"));
        if (null == promotion || promotion <= 0) {
        	paramMap.remove("promotion");
        }
        
        //TODO 促销专区id
        Integer is_promotion = com.yoho.gateway.utils.StringUtils.parseInt((String)paramMap.get("is_promotion"));
        if (null == is_promotion || is_promotion <= 0) {
        	paramMap.remove("is_promotion");
        }
        
        //活动模板
        Integer act_temp = com.yoho.gateway.utils.StringUtils.parseInt((String)paramMap.get("act_temp"));
        if (null == act_temp || act_temp <= 0) {
        	paramMap.remove("act_temp");
        }
        
        Integer act_rec = com.yoho.gateway.utils.StringUtils.parseInt((String)paramMap.get("act_rec"));
        if (null == act_rec || act_rec <= 0) {
        	paramMap.remove("act_rec");
        }
        //活动模板
        Integer act_status = com.yoho.gateway.utils.StringUtils.parseInt((String)paramMap.get("act_status"));
        if (null == act_status || act_status <= 0) {
        	paramMap.remove("act_status");
        }*/
        
        //设置需要排除掉的商品id,逗号分隔
        if (StringUtils.isNotEmpty(req.getNotProductSkn())) {
        	paramMap.put("not_productSkn", req.getNotProductSkn());
        }
        
        return paramMap;
	}
	
	
	public static String getNew(String str) {
		if (StringUtils.isNotEmpty(str) && !"N".equalsIgnoreCase(str)) {
			return "Y";
		} else {
			return null;
		}
	}
	
	public static String getorder(String order) {
		String[] converArray = com.yoho.gateway.utils.StringUtils.converArray(order, SEPRATOR_COMMA);
		List<String> list = new ArrayList<String>();
		if (converArray.length != 0) {
			for(String item : converArray) {
				String string = orderMap.get(item);
				if (null != string) {
					list.add(string);
				}
			}
		}
		return CollectionUtils.isEmpty(list) ? null :  StringUtils.join(list, SEPRATOR_COMMA);
	}
	
	public static String getPrice(String price) {
		if (StringUtils.isEmpty(price)) {
			return "";
		}
		String[] split = price.split(SEPRATOR_COMMA);
		if (split.length == 2) {
			Integer parseInt = com.yoho.gateway.utils.StringUtils.parseInt(split[0]);
			Integer parseInt2 = com.yoho.gateway.utils.StringUtils.parseInt(split[1]);
			if (null != parseInt && null != parseInt2 &&  parseInt > parseInt2) {
				return split[1] + SEPRATOR_COMMA + split[0];
			} else {
				return price;
			}
		} else {
			return null;
		}
    }
}
