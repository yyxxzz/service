package com.yoho.gateway.controller.search;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.error.GatewayError;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.search.BigDataSearchReq;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.ProductSearchService;

/**
 * Created by caoyan on 2015/11/28.
 * 猜你喜欢
 */
@Controller
public class Last7dayProductSearchController {

	private final Logger logger = LoggerFactory.getLogger(Last7dayProductSearchController.class);
	
	@Autowired
	private ProductSearchService productSearchService;
	
	// 默认推荐的条数
	private final static Integer DEFAULT_COMMENDSIZE = 200;
	
	private final static String CATEGORY_ID = "121,123,125,258,124,119,115,118,173,227,122,129,130,346,133,147,151,148,152,316,77,78,79,80,142,163,164,199,213,217,218,224,234,303,305,306,307,322,323,324,325,326,327,328,329,330,331,334";

	/**
	 * 首页猜你喜欢
	 * 
	 * @param channel
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.search.last7day")
	@ResponseBody
	@Cachable(expire=120)
	public ApiResponse queryLast7dayProductList(
			@RequestParam(value = "yh_channel", required = false) String channel,
			@RequestParam(value = "limit", required = false,defaultValue="20") Integer limit,
			@RequestParam(value = "page", required = false,defaultValue="1") Integer page,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "content_code", required = false) String contentCode,
			@RequestParam(value = "client_type", required = false) String clientType)
			throws GatewayException {
		logger.info("[method=app.search.last7day] param yh_channel is:{},limit is:{},page is:{}, gender is:{},contentCode is:{}",
				channel, limit, page, gender, contentCode);
		if (channel == null) {
			throw new GatewayException(GatewayError.SERVICE_ERROR);
		}
		ProductSearchReq req = new ProductSearchReq()
				.setLimit(limit)
				.setPage(page)
				.setYhChannel(channel)
				.setGender(gender)
				.setFirstShelveTime(getFirstShelveTime()).setClientType(clientType)
				.setSort(CATEGORY_ID).setSearchFrom("search.last7day");
		
		JSONObject data = productSearchService.searchLast7dayProductList(req);
		if (null != data) {
			data.put("content_code", null == contentCode ? "" : contentCode);
		}
		return new ApiResponse.ApiResponseBuilder().code(200).message("Last Search List.").data(data).build();
	}

	private String getFirstShelveTime() {
		Date now = new Date();
		return now.getTime() / 1000 - (86400 * 7)+ "," + now.getTime()/ 1000;
	}

	/**
	 * 首页猜你喜欢新版（带推荐位功能）
	 * 
	 * @param channel
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.search.newLast7day")
	@ResponseBody
	@Cachable(expire=180,excludeL2Args={5,6})
	public ApiResponse queryNewLast7dayProductList(
			@RequestParam(value = "yh_channel", required = false) String channel,
			@RequestParam(value = "limit", required = false,defaultValue="20") Integer limit,
			@RequestParam(value = "page", required = false,defaultValue="1") Integer page,
			@RequestParam(value = "gender", required = false) String gender,
			@RequestParam(value = "content_code", required = false) String contentCode,
			@RequestParam(value = "uid", required = false) String uid,
			@RequestParam(value = "udid", required = false) String udid,
			@RequestParam(value = "rec_pos", required = false) String recPos,
            @RequestParam(value = "client_type", required = false) String clientType)
			throws GatewayException {
		
		logger.info("[method=app.search.newLast7day] param yh_channel is:{},limit is:{},page is:{}, gender is:{},contentCode is:{},uid is:{},udid is:{},recPos is:{}",
				channel, limit, page, gender, contentCode, uid, udid, recPos);
		if (null == udid) {
			return new ApiResponse.ApiResponseBuilder().code(404).message("udid Is Null").data(null).build();
		}
		
		if (null == channel) {
			return new ApiResponse.ApiResponseBuilder().code(404).message("channel Is Null").data(null).build();
		}
		
		if(null == recPos){
			recPos = getRecPos(channel);
		}
		BigDataSearchReq searchReq = new BigDataSearchReq();
		limit = null == limit ? DEFAULT_COMMENDSIZE : limit;
		searchReq.setYhChannel(channel).setLimit(limit).setPage(page).setGender(gender).setUserId(uid).setClientType(clientType).setSearchFrom("search.newLast7day");
		searchReq.setUdid(udid);
		searchReq.setRecPos(recPos);
		
		JSONObject data = productSearchService.searchProductListByBigData(searchReq);
		if(null==data)
		{
			return new ApiResponse.ApiResponseBuilder().code(200).message("No Product List.").data(new JSONObject()).build();
		}
		data.put("content_code", null == contentCode ? "" : contentCode);
		return new ApiResponse.ApiResponseBuilder().code(200).data(data).message("Last Search List.").build();
	}
	
	/**
	 * 获取推荐位
	 *
	 * @param channel
	 * @return
	 */
	private String getRecPos(String channel) {
		String recPos = "100004";
		if ("1".equals(channel)) {
			recPos = "100001";
		} else if ("2".equals(channel)) {
			recPos = "100002";
		}
		return recPos;
	}
	
}
