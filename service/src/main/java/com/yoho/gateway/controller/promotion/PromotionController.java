package com.yoho.gateway.controller.promotion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.gateway.cache.CacheFactory;
import com.yoho.gateway.cache.KeyBuilder;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.promotion.CouponsVo;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.service.model.promotion.CouponForm;
import com.yoho.service.model.promotion.CouponsBo;
import com.yoho.service.model.promotion.CouponsCenterWrapperBo;
import com.yoho.service.model.promotion.CouponsCenterWrapperBo.CouponsCenterBo;
import com.yoho.service.model.promotion.request.CouponSendReq;
import com.yoho.service.model.promotion.request.CouponUseReq;
import com.yoho.service.model.promotion.request.CouponsCenterRequest;
import com.yoho.service.model.promotion.request.CouponsCenterRequest.CouponSet;
import com.yoho.service.model.promotion.request.CouponsCenterRequest.CouponsRequest;
import com.yoho.service.model.promotion.request.CouponsReq;
import com.yoho.service.model.promotion.response.CouponSendResp;
import com.yoho.service.model.resource.ResourcesServices;
import com.yoho.service.model.resource.request.ResourcesRequestBody;

/**
 * promotion gateway
 *
 * @author lijian
 * @Time 2015/12/11
 */
@Controller
public class PromotionController {

	static Logger logger = LoggerFactory.getLogger(PromotionController.class);
	private final String QUERY_COUPONLIST_SERVICE_NAME = "promotion.queryCouponList";
	private final String QUERY_BIRTH_COUPONLIST_SERVICE_NAME = "promotion.queryUserBirthdayCouponList";

	/**
	 * 券发放管理
	 */
	private static  final String COUPON_SEND_SERVICE_NODE="promotion.couponSend";

	private final String SEND_COUPON_SERVICE_NAME = "promotion.sendCoupon";

	private final String QUERY_SUCCESS = "请求成功";

	private final String SEND_COUPON_SUCCESS = "优惠券领取成功";

	private final String RESOURCES_TEMPLATE_NOT_EXIST = "资源位模板不存在！";

	@Autowired
	private ServiceCaller serviceCaller;

	@Autowired
    private MemecacheClientHolder cacheClient;
	
	@Resource
	CacheFactory cacheFactory;

	/**
	 * 查询优惠券的列表信息 TYPE=4 查询生日券列表
	 * @param couponType
	 * @return
	 */
	@RequestMapping(params = "method=app.promotion.queryCoupon")
	@ResponseBody
	public ApiResponse queryCouponList(@RequestParam(value = "couponType") String couponType) {


		CouponsReq couponsReq = new CouponsReq();
		couponsReq.setCouponType(couponType);
		CouponsBo[] data = serviceCaller.call(QUERY_COUPONLIST_SERVICE_NAME, couponsReq, CouponsBo[].class);
		logger.info("result data is {}", JSONObject.toJSONString(data));
		List<CouponsVo> couponsVoList=new ArrayList<CouponsVo>();
		if(data!=null&&data.length>0){
			for (CouponsBo couponsBo:data){
				CouponsVo couponsVo=new CouponsVo();
				BeanUtils.copyProperties(couponsBo,couponsVo);
				couponsVo.setStartTime(DateUtil.getDateStrBySecond(couponsBo.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
				couponsVo.setEndTime(DateUtil.getDateStrBySecond(couponsBo.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
				couponsVoList.add(couponsVo);
			}
		}
		return new ApiResponse.ApiResponseBuilder().code(200).message(QUERY_SUCCESS).data(couponsVoList).build();

	}


	/**
	 * TYPE=4 查询生日券列表 并且提前发放
	 * @param couponType
	 * @param uid
	 * @return
	 */
	@RequestMapping(params = "method=app.promotion.queryBirthCoupon")
	@ResponseBody
	public ApiResponse queryBirthCouponList(@RequestParam(value = "couponType") String couponType,
									   @RequestParam(value = "uid") String uid) {
		logger.info("queryBirthCouponList couponType is {} uid is {}",couponType,uid);
		CouponUseReq couponsReq = new CouponUseReq();
		couponsReq.setCouponType(Integer.valueOf(couponType));
		couponsReq.setUid(Integer.valueOf(uid));
		CouponsBo[] data = serviceCaller.call(QUERY_BIRTH_COUPONLIST_SERVICE_NAME, couponsReq, CouponsBo[].class);
		logger.info("result data is {}", JSONObject.toJSONString(data));
		List<CouponsVo> couponsVoList=new ArrayList<CouponsVo>();
		if(data!=null&&data.length>0){
			for (CouponsBo couponsBo:data){
				CouponsVo couponsVo=new CouponsVo();
				BeanUtils.copyProperties(couponsBo,couponsVo);
				couponsVo.setStartTime(DateUtil.getDateStrBySecond(couponsBo.getStartTime(), "yyyy-MM-dd HH:mm:ss"));
				couponsVo.setEndTime(DateUtil.getDateStrBySecond(couponsBo.getEndTime(), "yyyy-MM-dd HH:mm:ss"));
				couponsVoList.add(couponsVo);
			}
		}
		return new ApiResponse.ApiResponseBuilder().code(200).message(QUERY_SUCCESS).data(couponsVoList).build();

	}


	/**
	 * 用户领券
	 * @param uid
	 * @param couponId
	 * @return
	 */
	@RequestMapping(params = "method=app.promotion.getCoupon")
	@ResponseBody
	public ApiResponse sendCoupon(@RequestParam(value = "uid") String uid,
								  @RequestParam(value = "couponId") String couponId) {

		CouponForm couponForm = new CouponForm();
		couponForm.setCouponId(couponId);
		couponForm.setUid(uid);
		String data = serviceCaller.call(SEND_COUPON_SERVICE_NAME, couponForm, String.class);
		//将用户领券结果放入缓存
		cacheFactory.getRedisValueCache().hashPut(KeyBuilder.getUserCouponsKeyBuilder(Integer.parseInt(uid)), couponId, "Y");
		cacheFactory.getRedisValueCache().expire(KeyBuilder.getUserCouponsKeyBuilder(Integer.parseInt(uid)), 60*30);
		logger.info("result data is {}", JSON.toJSONString(data));
		return new ApiResponse.ApiResponseBuilder().code(200).message(SEND_COUPON_SUCCESS).data(data).build();

	}


	@RequestMapping(params = "method=app.promotion.queryCouponCenter")
	@ResponseBody
	public ApiResponse queryCouponCenter(@RequestParam(value = "uid", required = false) String uid,
										 @RequestParam(value = "contentCode", required=true) String contentCode,
                                         @RequestParam(value = "client_type", required=false) String clientType) {
		logger.info("Method queryCouponCenter request params:{},{},{}", uid, contentCode, clientType);
		
		//1、获取资源位信息
		JSONArray resourceData = cacheClient.getLevel1Cache().get(KeyBuilder.couponCenterKeyBuilder((StringUtils.isEmpty(clientType) ? "h5" : clientType), contentCode), JSONArray.class);
		if(null == resourceData || resourceData.size() == 0){
			//缓存没查到数据，调资源位接口获取
			ResourcesRequestBody request = new ResourcesRequestBody();
	        request.setContentCode(contentCode);
	        request.setClientType(clientType);
	        Object[] data = serviceCaller.call(ResourcesServices.get, request, Object[].class);
	        logger.debug("queryCouponCenter resourcesServices.get result is {}", JSON.toJSONString(data));
	        if(ArrayUtils.isEmpty(data)){
				return new ApiResponse.ApiResponseBuilder().code(400).message(RESOURCES_TEMPLATE_NOT_EXIST).build();
	        }
	        resourceData = JSONArray.fromObject(data);
	        //将结果放入缓存2分钟
	        cacheClient.getLevel1Cache().set(KeyBuilder.couponCenterKeyBuilder((StringUtils.isEmpty(clientType) ? "h5" : clientType), contentCode), 120, resourceData);
		}
		
		//2、获取券信息
		Set<String> totalCouponSet = new HashSet<String>();
        List<CouponsRequest> couponsRequestList = getCouponRequestList(resourceData, totalCouponSet);
        List<String> totalCouponList = Lists.newArrayList();
		totalCouponList.addAll(totalCouponSet);
        if(CollectionUtils.isEmpty(totalCouponList)){
        	queryPromotion(resourceData, uid, couponsRequestList, (StringUtils.isEmpty(clientType) ? "h5": clientType), contentCode);
    		return new ApiResponse.ApiResponseBuilder().code(200).message(QUERY_SUCCESS).data(resourceData).build();
    	}
        
        //用户未登录的券状态
        //1：可领取 2：已抢光 3：已领取 4：已过期
        Map<String, String> cacheCouponStatusMap = dealCacheCouponMap(cacheFactory.getRedisValueCache().mHashGet(KeyBuilder.getCouponCenterStatusKeyBuiler((StringUtils.isEmpty(clientType) ? "h5" : clientType), contentCode),
	    		totalCouponList, String.class));
        logger.debug("queryCouponCenter cacheCouponStatusMap result is {}", cacheCouponStatusMap);
        if(StringUtils.isEmpty(uid)){
        	if(MapUtils.isEmpty(cacheCouponStatusMap)){
        		queryPromotion(resourceData, uid, couponsRequestList, (StringUtils.isEmpty(clientType) ? "h5": clientType), contentCode);
                return new ApiResponse.ApiResponseBuilder().code(200).message(QUERY_SUCCESS).data(resourceData).build();
    	    }else{
    	    	fillCouponStatusForResourceData(resourceData, cacheCouponStatusMap);
    	    	return new ApiResponse.ApiResponseBuilder().code(200).message(QUERY_SUCCESS).data(resourceData).build();
    	    }
        }else{
        	 //领券信息
            Map<String, String> cacheReceivedCouponMap = dealCacheCouponMap(cacheFactory.getRedisValueCache().mHashGet(KeyBuilder.getUserCouponsKeyBuilder(Integer.parseInt(uid)), totalCouponList, String.class));
            logger.debug("queryCouponCenter ccacheReceivedCouponMap result is {}", cacheCouponStatusMap);
            //缓存获取不到，调Promotion接口获取
            if(MapUtils.isEmpty(cacheCouponStatusMap)){
            	queryPromotion(resourceData, uid, couponsRequestList, (StringUtils.isEmpty(clientType) ? "h5": clientType), contentCode);
                return new ApiResponse.ApiResponseBuilder().code(200).message(QUERY_SUCCESS).data(resourceData).build();
    	    }else{
    	    	if(MapUtils.isEmpty(cacheReceivedCouponMap)){
    	    		queryPromotion(resourceData, uid, couponsRequestList, (StringUtils.isEmpty(clientType) ? "h5": clientType), contentCode);
                    return new ApiResponse.ApiResponseBuilder().code(200).message(QUERY_SUCCESS).data(resourceData).build();
    	    	}

    	    	for(String couponId : cacheCouponStatusMap.keySet()){
    	    		if(cacheReceivedCouponMap.containsKey(couponId) && "Y".equals(cacheReceivedCouponMap.get(couponId))){
    	    			cacheCouponStatusMap.put(couponId, "3");
    	    		}
    	    	}
    	    	fillCouponStatusForResourceData(resourceData, cacheCouponStatusMap);
    	    	return new ApiResponse.ApiResponseBuilder().code(200).message(QUERY_SUCCESS).data(resourceData).build();
    	    }
        }
	}

	private void queryPromotion(JSONArray resourceData, String uid, List<CouponsRequest> couponsRequestList, String clientType, String contentCode){
		CouponsCenterRequest ccReq = new CouponsCenterRequest();
        ccReq.setUid(null == uid ? null : Integer.valueOf(uid));
        ccReq.setCouponsRequestList(couponsRequestList);
        CouponsCenterWrapperBo bo = serviceCaller.call("promotion.queryCouponsCenter", ccReq, CouponsCenterWrapperBo.class);
        logger.debug("queryCouponCenter CouponsCenterWrapperBo is {}", bo);
        String key = KeyBuilder.getCouponCenterStatusKeyBuiler((StringUtils.isEmpty(clientType) ? "h5" : clientType), contentCode);
        Map<String, String> statusMap = getCouponStatusMap(bo, key, uid);
        fillCouponStatusForResourceData(resourceData, statusMap);
	}

	private Map<String, String> dealCacheCouponMap(Map<String, String> map){
		if(MapUtils.isEmpty(map)){
			return map;
		}
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();  
        while(it.hasNext()){  
            Map.Entry<String, String> entry=it.next();  
            if(null == entry.getValue()){  
                it.remove();     
            }  
        }
		return map;
	}
	
	private Map<String, String> getCouponStatusMap(CouponsCenterWrapperBo wrapperBo, String key, String uid){
		List<CouponsCenterBo> couponsCenterBoList = wrapperBo.getCouponsCenterBoList();
		Map<String, String> map = new HashMap<String, String>();
		Map<String, String> hashMap = new HashMap<String, String>();
		for(CouponsCenterBo bo : couponsCenterBoList){
			map.put(bo.getCouponIds(), String.valueOf(bo.getStatus()));
			hashMap.put(bo.getCouponIds(), 3 == bo.getStatus() ? "1" : String.valueOf(bo.getStatus()));
			if(StringUtils.isNotEmpty(uid) && 3 == bo.getStatus()){
				cacheFactory.getRedisValueCache().hashPut(KeyBuilder.getUserCouponsKeyBuilder(Integer.parseInt(uid)), bo.getCouponIds(), "Y");
			}
		}
		cacheFactory.getRedisValueCache().mHashSet(key, hashMap);
		cacheFactory.getRedisValueCache().expire(key, 2*60);//缓存2分钟
        
        return map;
	}
	
	private void fillCouponStatusForResourceData(JSONArray cacheData, Map<String, String> statusMap){
		for(int i=0; i<cacheData.size(); i++){
	           Object templateIdObj = cacheData.getJSONObject(i).get("template_name");
	           if(null == templateIdObj || !"getCoupon".equals(templateIdObj.toString())){
	       		templateIdObj = cacheData.getJSONObject(i).get("templateName");
	       		if(null == templateIdObj || !"getCoupon".equals(templateIdObj.toString())){
	       			continue;
	       		}
	       	}
	           Object obj = cacheData.getJSONObject(i).get("data");
	           if(!(obj instanceof JSONArray)){
	            	continue;
	           }
	           JSONArray couponArr = cacheData.getJSONObject(i).getJSONArray("data");
	           for(int j=0; j<couponArr.size(); j++){
	        	   String couponID = couponArr.getJSONObject(j).getString("couponID");
	        	   if(MapUtils.isEmpty(statusMap)){
		        	   couponArr.getJSONObject(j).put("status", 1);//把预计放入缓存的已领取状态改为未领取
	        		   continue;
	        	   }
	        	   if(statusMap.keySet().contains(couponID)){
	        		   String status = statusMap.get(couponID);
	        		   if(null == status){
	        			   couponArr.getJSONObject(j).put("status", "1"); 
	        			   continue;
	        		   }
	        		   if("4".equals(status)){
	                	  couponArr.remove(j);
	                	  continue;
	        		   }
	        		   couponArr.getJSONObject(j).put("status", statusMap.get(couponID));
	        	   }
	           }
	        }
	}
	
	private List<CouponsRequest> getCouponRequestList(JSONArray tmpData, Set<String> totalCouponList){
		List<CouponsRequest> couponsRequestList = Lists.newArrayList();
        for(int i=0; i<tmpData.size(); i++){
        	CouponsRequest couponsRequest = new CouponsRequest();
        	Object templateIdObj = tmpData.getJSONObject(i).get("template_name");
        	if(null == templateIdObj || !"getCoupon".equals(templateIdObj.toString())){
        		templateIdObj = tmpData.getJSONObject(i).get("templateName");
        		if(null == templateIdObj || !"getCoupon".equals(templateIdObj.toString())){
        			continue;
        		}
        	}
        	Object obj = tmpData.getJSONObject(i).get("data");
        	if(!(obj instanceof JSONArray)){
        		continue;
        	}

        	couponsRequest.setCode(tmpData.getJSONObject(i).getString("template_id"));

        	JSONArray couponArr = (JSONArray)obj;
        	List<CouponSet> couponSetList = Lists.newArrayList();
        	for(int j=0; j<couponArr.size(); j++){
        		CouponSet cs = new CouponSet();
        		String couponIds = couponArr.getJSONObject(j).getString("couponID");
        		if(StringUtils.isEmpty(couponIds)){
        			continue;
        		}
        		String[] couponIdArr = couponIds.split(",");
        		List<Integer> couponIdList = Lists.newArrayList();
        		for(String couponId : couponIdArr){
        			couponIdList.add(Integer.parseInt(couponId));
        			totalCouponList.add(couponId);
        		}
        		cs.setCouponsIds(couponIdList);
        		couponSetList.add(cs);
        	}

        	couponsRequest.setCouponSetList(couponSetList);
        	couponsRequestList.add(couponsRequest);
        }
        
        return couponsRequestList;
	}

	/**
	 * 券发放管理
	 * @param uid 用户id
	 * @param token 发券管理token
	 * @return
	 */
	@RequestMapping(params = "method=app.coupons.couponSend")
	@ResponseBody
	public ApiResponse couponSend(@RequestParam("uid") String uid,
								  @RequestParam("coupon_send_token") String token) {
		logger.info("uid:{},token:{}", uid, token);

		CouponSendReq req = new CouponSendReq();
		req.setUid(uid);
		req.setCouponSendToken(token);
		CouponSendResp resps = serviceCaller.call(COUPON_SEND_SERVICE_NODE, req, CouponSendResp.class);
		if(resps.isFlag()){
			return new ApiResponse.ApiResponseBuilder().code(200).message(SEND_COUPON_SUCCESS).build();
		}
		//已经领取的返回401,其他错误返回400
		if((ServiceError.PROMOTION_COUPON_HAS_RECEIVED.getMessage().equals(resps.getErrMsg()))){
			return new ApiResponse.ApiResponseBuilder().code(401).message(resps.getErrMsg()).build();
		}else {
			return new ApiResponse.ApiResponseBuilder().code(400).message(resps.getErrMsg()).build();
		}
	}
}