package com.yoho.gateway.controller.users;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.SuggestQueryReqVO;
import com.yoho.gateway.model.request.SuggestUpdateReqVO;
import com.yoho.gateway.model.response.SuggestPageRspVO;
import com.yoho.gateway.model.response.SuggestQueryRspVO;
import com.yoho.service.model.request.SuggestQueryReqBO;
import com.yoho.service.model.request.SuggestUpdateReqBO;
import com.yoho.service.model.response.SuggestPageRspBO;
import com.yoho.service.model.response.SuggestQueryRspBO;
import com.yoho.service.model.response.SuggestUpdateRspBO;

@Controller
@RequestMapping(value = "/suggest/api/v1/suggest")
public class SuggestController {

	private Logger logger = LoggerFactory.getLogger(SuggestController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	private static final String SUGGEST_GETLIST_SERVICE = "users.getSuggestList";

	private static final String SUGGEST_SAVESUGGEST_SERVICE = "users.saveSuggest";
	
	private static final String SUGGEST_ISRELIABLE_SERVICE = "users.setSuggestReliable";

	/**
	 * 获取意见反馈列表
	 * 
	 * @param suggestQueryReqVO
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(value = "/getList")
	@ResponseBody
	public ApiResponse getList(SuggestQueryReqVO suggestQueryReqVO) throws GatewayException {
		logger.debug("Enter SuggestController.getList. suggestQueryReqVO is {}", suggestQueryReqVO);

		// (1)调用接口获取意见反馈列表
		SuggestQueryReqBO suggestQueryReqBO = new SuggestQueryReqBO();
		suggestQueryReqBO.setEndTime(suggestQueryReqVO.getEnd_time());
		suggestQueryReqBO.setHasImage(suggestQueryReqVO.getHas_image());
		suggestQueryReqBO.setIsReliable(suggestQueryReqVO.getIs_reliable());
		suggestQueryReqBO.setLimit(suggestQueryReqVO.getLimit());
		suggestQueryReqBO.setPage(suggestQueryReqVO.getPage());
		suggestQueryReqBO.setStartTime(suggestQueryReqVO.getStart_time());
		suggestQueryReqBO.setStatus((byte) 2);
		suggestQueryReqBO.setUdid(suggestQueryReqVO.getUdid());
		SuggestPageRspBO suggestPageRspBO = serviceCaller.call(SUGGEST_GETLIST_SERVICE, suggestQueryReqBO, SuggestPageRspBO.class);
		List<SuggestQueryRspBO> suggestQueryRspBOList = suggestPageRspBO.getList();

		// (2)组织返回
		List<SuggestQueryRspVO> suggestQueryRspVOList = new ArrayList<SuggestQueryRspVO>();
		for (SuggestQueryRspBO suggestQueryRspBO : suggestQueryRspBOList) {
			SuggestQueryRspVO suggestQueryRspVO = new SuggestQueryRspVO();
			suggestQueryRspVO.setClient_type(suggestQueryRspBO.getClientType());
			suggestQueryRspVO.setContent(suggestQueryRspBO.getContent());
			suggestQueryRspVO.setCover_image(suggestQueryRspBO.getCoverImage());
			suggestQueryRspVO.setCover_image_url(suggestQueryRspBO.getCoverImageUrl());
			suggestQueryRspVO.setCreate_time(suggestQueryRspBO.getCreateTime());
			suggestQueryRspVO.setFilter_content(suggestQueryRspBO.getFilterContent());
			suggestQueryRspVO.setHas_image(suggestQueryRspBO.getHasImage());
			suggestQueryRspVO.setId(suggestQueryRspBO.getId());
			suggestQueryRspVO.setImage(suggestQueryRspBO.getImage());
			suggestQueryRspVO.setIs_reliable(suggestQueryRspBO.getIsReliable());
			suggestQueryRspVO.setOrder_by(suggestQueryRspBO.getOrderBy());
			suggestQueryRspVO.setReliable(suggestQueryRspBO.getReliable());
			suggestQueryRspVO.setReply_content(suggestQueryRspBO.getReplyContent());
			suggestQueryRspVO.setStatus(suggestQueryRspBO.getStatus());
			suggestQueryRspVO.setSuggest_id(suggestQueryRspBO.getSuggestId());
			suggestQueryRspVO.setSuggest_type(suggestQueryRspBO.getSuggestType());
			suggestQueryRspVO.setUnreliable(suggestQueryRspBO.getUnreliable());
			suggestQueryRspVO.setUpdate_time(suggestQueryRspBO.getUpdateTime());
			suggestQueryRspVOList.add(suggestQueryRspVO);
		}
		SuggestPageRspVO suggestPageRspVO = new SuggestPageRspVO();
		suggestPageRspVO.setEdited_count(suggestPageRspBO.getEditedCount());
		suggestPageRspVO.setList(suggestQueryRspVOList);
		suggestPageRspVO.setPage(suggestPageRspBO.getPage());
		suggestPageRspVO.setPage_total(suggestPageRspBO.getPage_total());
		suggestPageRspVO.setPublish_count(suggestPageRspBO.getPublishCount());
		suggestPageRspVO.setTotal(suggestPageRspBO.getTotal());
		suggestPageRspVO.setUnedit_count(suggestPageRspBO.getUneditCount());
		return new ApiResponse.ApiResponseBuilder().code(200).message("Hot Suggest List").data(suggestPageRspVO).build();
	}

	@RequestMapping(value = "/saveSuggest")
	@ResponseBody
	public ApiResponse saveSuggest(SuggestUpdateReqVO suggestUpdateReqVO) throws GatewayException {
		logger.debug("Enter SuggestController.saveSuggest. suggestUpdateReqVO is {}", suggestUpdateReqVO);

		// (1)调用接口获取意见反馈列表
		int uid = StringUtils.isEmpty(suggestUpdateReqVO.getUid()) ? 0 : Integer.parseInt(suggestUpdateReqVO.getUid());
		SuggestUpdateReqBO suggestUpdateReqBO = new SuggestUpdateReqBO();
		suggestUpdateReqBO.setAppVersion(suggestUpdateReqVO.getApp_version());
		suggestUpdateReqBO.setClientType(suggestUpdateReqVO.getClient_type());
		suggestUpdateReqBO.setContent(suggestUpdateReqVO.getContent());
		suggestUpdateReqBO.setImage(suggestUpdateReqVO.getImage());
		suggestUpdateReqBO.setOsVersion(suggestUpdateReqVO.getOs_version());
		suggestUpdateReqBO.setUid(uid);
		suggestUpdateReqBO.setSuggestType(suggestUpdateReqVO.getSuggest_type());
		SuggestUpdateRspBO suggestUpdateRspBO = serviceCaller.call(SUGGEST_SAVESUGGEST_SERVICE, suggestUpdateReqBO, SuggestUpdateRspBO.class);

		// (2)组织返回
		return new ApiResponse.ApiResponseBuilder().code(200).message("success").data(suggestUpdateRspBO.getId()).build();
	}
	
	@RequestMapping(value = "/is_reliable")
	@ResponseBody
	public ApiResponse isReliable(SuggestUpdateReqVO suggestUpdateReqVO) throws GatewayException {
		logger.debug("Enter SuggestController.isReliable. suggestUpdateReqVO is {}", suggestUpdateReqVO);
		// (1)调用接口获取意见反馈列表
		int uid = StringUtils.isEmpty(suggestUpdateReqVO.getUid()) ? 0 : Integer.parseInt(suggestUpdateReqVO.getUid());
		SuggestUpdateReqBO suggestUpdateReqBO = new SuggestUpdateReqBO();
		suggestUpdateReqBO.setUid(uid);
		suggestUpdateReqBO.setUdid(suggestUpdateReqVO.getUdid());
		suggestUpdateReqBO.setSuggestId(suggestUpdateReqVO.getSuggest_id());
		suggestUpdateReqBO.setIsReliable(suggestUpdateReqVO.getIs_reliable());
		serviceCaller.call(SUGGEST_ISRELIABLE_SERVICE, suggestUpdateReqBO, SuggestUpdateRspBO.class);

		// (2)组织返回
		return new ApiResponse.ApiResponseBuilder().code(200).message("success").data(new JSONObject()).build();
	}

}
