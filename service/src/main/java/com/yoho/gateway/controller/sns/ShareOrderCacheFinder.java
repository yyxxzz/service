package com.yoho.gateway.controller.sns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.GatewayError;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.sns.ShareOrderVo;
import com.yoho.gateway.service.sns.ShareOrderService;
import com.yoho.service.model.resource.request.ResourcesRequestBody;
import com.yoho.service.model.resource.resource.FocusParsedResource;
import com.yoho.service.model.sns.model.CommentBo;
import com.yoho.service.model.sns.request.BatchSnsRequest;
import com.yoho.service.model.sns.request.ShareOrderForm;
import com.yoho.service.model.sns.response.PageResponse;
import com.yoho.service.model.sns.response.ResponseBean;

@Service
public class ShareOrderCacheFinder {
	
    private final Logger logger = LoggerFactory.getLogger(ShareOrderCacheFinder.class);
    
    private String showChannelContentCode="e7a7f8b6695a99a1be911e0775f41cda";
    
    // show列表页面缓存5分钟
    private final static int SHOW_LIST_CACHE = 300;
    
	@Autowired
    private ServiceCaller serviceCaller;
	
	@Autowired
	private CacheClient cacheClient;
	
	@Autowired
    private ShareOrderService shareOrderService;
	

	public ApiResponse fetchShowChannelCache(int limit, int page, String udid, String clientType) {
		// show页面列表缓存key
		String cacheKey = "yh:gw:sns:" + limit + ":" + page + ":" + clientType;
		ApiResponse apiResponse = cacheClient.get(cacheKey, ApiResponse.class);
		if(null!=apiResponse&&StringUtils.isNotEmpty(udid)){
			try {
				//缓存不为空回填：1、每条晒单的点赞个数，2、udid是否点赞
				onCallBack(apiResponse,udid);
			} catch (Exception e) {
				//这里抛异常不影响页面展示
				logger.warn("process praiseInfo fail, exception: {}", e);
			}
			return apiResponse;
		}
		
		// 组装查询条件
		ShareOrderForm shareOrderForm = new ShareOrderForm();
        shareOrderForm.setPage(page);
        shareOrderForm.setLimit(limit);
        shareOrderForm.setUdid(udid);

        //获取推荐晒单列表
        Map<String,Object> info = new HashMap<>();
        PageResponse responseBean = serviceCaller.call("sns.queryRecommendShareOrder",shareOrderForm , PageResponse.class);
        
        // 晒单Bo转Vo
        if(responseBean!=null && responseBean.getList()!=null){
        	responseBean = shareOrderService.getShareOrderVoList(responseBean);
            logger.debug("after set queryRecommendShareOrder result: {}", responseBean.getList());
        }
        
        //获取资源位信息
        ResourcesRequestBody request = new ResourcesRequestBody();
        request.setContentCode(showChannelContentCode);
        request.setClientType(clientType);
        List<Object> data = serviceCaller.call("resources.get", request, List.class);
        info.put("showBannerList",getFocusParsedResource(data));
        info.put("shareOrderList",responseBean);

        apiResponse = new ApiResponse();
        apiResponse.setData(info);
        apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
        apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
        
        //5分钟
		if(responseBean!=null && data!=null)
		{
			cacheClient.set(cacheKey, SHOW_LIST_CACHE, apiResponse);
		}
        return apiResponse;
	}

	private void onCallBack(ApiResponse apiResponse, String udid) {
		// 1、每条晒单的点赞个数
		// 2、udid是否点赞
		logger.info("show.queryRecommendShareOrder, start onCallBack(), udid: {}", udid);
		Map<String,Object> dataCache = (Map<String, Object>)apiResponse.getData();
		PageResponse pageResponseCache = JSON.parseObject(dataCache.get("shareOrderList").toString(), PageResponse.class);
		if(pageResponseCache==null || pageResponseCache.getList()==null){
			return;
		}
		List<ShareOrderVo> commentVoListCache = JSON.parseArray(pageResponseCache.getList().toString(), ShareOrderVo.class);
		
		// 获取待回填的commentId列表
		List<Integer> commentIdList = getCommentIdList(commentVoListCache);
		if(commentIdList!=null){
			BatchSnsRequest batchSnsRequest = new BatchSnsRequest();
			batchSnsRequest.setParams(commentIdList);
			batchSnsRequest.setUdid(udid);
			logger.info("start sns.getPraiseInfo, batchSnsRequest: {}", batchSnsRequest);
			// 调sns接口，查询点赞个数及是否点赞
			ResponseBean responseBean = serviceCaller.call("sns.getPraiseInfo",batchSnsRequest, ResponseBean.class);
			// 处理返回结果，把返回的点赞信息回填到cache得到的记录中
			if(null==responseBean||null==responseBean.getData())
			{
				return;
			}
			List<CommentBo> commentBoList = JSON.parseArray(responseBean.getData().toString(), CommentBo.class);
			for (ShareOrderVo ShareOrderVoCache : commentVoListCache) {
				for (CommentBo commentBo : commentBoList) {
					if(ShareOrderVoCache.getId().intValue() == commentBo.getId().intValue()){
						ShareOrderVoCache.setPraise_num(commentBo.getPraise_num());
						ShareOrderVoCache.setIsPraise(commentBo.getIsPraise());
						continue;
					}
				}
			}
			pageResponseCache.setList(commentVoListCache);
			dataCache.put("shareOrderList", pageResponseCache);
			apiResponse.setData(dataCache);
		}
		logger.info("show.queryRecommendShareOrder, end onCallBack(), udid: {}", udid);
	}
	
	private List<Integer> getCommentIdList(List<ShareOrderVo> commentVoListCache) {
		List<Integer> commentIdList = new ArrayList<Integer>();
		if(commentVoListCache!=null && commentVoListCache.size()>0){
			for (ShareOrderVo commentVo : commentVoListCache) {
				commentIdList.add(commentVo.getId());
			}
		}else{
			return null;
		}
		return commentIdList;
	}

	private Object getFocusParsedResource(List<Object> data){
        for(Object obj : data){
            if(obj instanceof  FocusParsedResource){
                return obj;
            }
        }
        return new JSONObject();
    }
}
