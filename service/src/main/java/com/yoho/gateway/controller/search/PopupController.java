package com.yoho.gateway.controller.search;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.cache.KeyBuilder;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.search.BigDataSearchReq;
import com.yoho.gateway.service.search.ProductSearchService;
import com.yoho.gateway.utils.DateUtil;

/**
 * 男首/女首弹窗功能
 * @author caoyan
 */
@Controller
public class PopupController {
	
	private Logger logger = LoggerFactory.getLogger(PopupController.class);
	
	@Autowired
	private ProductSearchService productSearchService;
	
	@Autowired
    private MemecacheClientHolder memecacheClientHolder;
	
	// 默认条数
	private final static Integer DEFAULT_SIZE = 1;
	
	/**
	 * 首页弹窗功能（大数据推荐功能，4.8版本开始使用）
	 */
	@RequestMapping(params = "method=app.recommend.popup")
	@ResponseBody
	public ApiResponse getRecommendPopup(@RequestParam(value = "uid", required = false) String uid,
			@RequestParam(value = "udid", required = false) String udid,
			@RequestParam(value = "rec_pos", required = false) String recPos) throws GatewayException {
		
		logger.info("getRecommendPopup method=app.recommend.popup uid is{}: udid is:{}, recPos is:{}", uid, udid, recPos);
		//未登录用户不弹窗
		if(StringUtils.isEmpty(uid)){
			return new ApiResponse.ApiResponseBuilder().code(200).message("popup product.").data(null).build();
		}
		if(null == udid) {
			return new ApiResponse.ApiResponseBuilder().code(404).message("udid Is Null").data(null).build();
		}
		if(null == recPos){
			recPos = "100010";
		}
		Integer cacheTime = getFromCache(uid, recPos);
		int nowTime = DateUtil.getCurrentTimeSecond();
		//如果缓存有值且与当前时间小于12小时，则不再弹窗
		if(null != cacheTime && ((nowTime-cacheTime.intValue()) < 12*60*60)){
			return new ApiResponse.ApiResponseBuilder().code(200).message("popup product.").data(null).build();
		}
		BigDataSearchReq searchReq = new BigDataSearchReq();
		searchReq.setUserId(uid).setPage(1).setLimit(DEFAULT_SIZE);
		searchReq.setUdid(udid);
		searchReq.setRecPos(recPos);
		JSONObject data = productSearchService.searchProductListByBigData(searchReq);
		if(null==data || null == data.get("product_list")){
			//找不到商品，返回空
			logger.info("can not find any rec products by: uid:{} udid:{} rec_pos:{}", uid, udid, recPos);
			JSONObject nullData = new JSONObject();
			nullData.put("rec_id", UUID.randomUUID());
			return new ApiResponse.ApiResponseBuilder().code(200).message("popup product.").data(nullData).build();
		}
		//将当前时间放入缓存12小时
		memecacheClientHolder.getLevel1Cache().set(KeyBuilder.getPopupKeyBuilder(uid, recPos), 12*60*60, nowTime);

		return new ApiResponse.ApiResponseBuilder().code(200).message("popup product.").data(data).build();
	}
	
	private Integer getFromCache(String uid, String recPos){
		String key = KeyBuilder.getPopupKeyBuilder(uid, recPos);
		Integer timeStamp = memecacheClientHolder.getLevel1Cache().get(key, Integer.class);//精确到年月日
		
		return timeStamp;
	}
	
}
