package com.yoho.gateway.controller.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.product.model.SpecialActivityBo;
import com.yoho.product.request.ActivityRequest;

/**
 * 
 * @author lixuxin 
 *
 */
@Controller
public class SpecialActivityProductController {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpecialActivityProductController.class);
	private static final int REQUEST_ERROR_CODE=500;
	private static final String REQUEST_ACTIVITY_SORT_ERROR_MSG="sort must be not null!";
	private static final String REQUEST_PLATEFORM_ERROR_MSG="plateform must be not null!";

	@Autowired
	private ServiceCaller serviceCaller;
	/**
	 * 活动查询
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.activity.get")
	@ResponseBody
	@Cachable(expire=10)
	public ApiResponse querySpecialActivity(@RequestParam(value = "id", required = false) Integer id,@RequestParam(value = "sort", required = true) String sort,@RequestParam(value = "plateform", required = true) String plateform,@RequestParam(value = "yh_channel", required = false) String yhChannel) throws GatewayException {
		LOGGER.info("app.activity.get param status is {},activityType is {}",sort,plateform);
		if("".equals(sort)||null==sort){
			throw new GatewayException(REQUEST_ERROR_CODE, REQUEST_ACTIVITY_SORT_ERROR_MSG);
		}
		if("".equals(plateform)||null==plateform){
			throw new GatewayException(REQUEST_ERROR_CODE, REQUEST_PLATEFORM_ERROR_MSG);
		}
		ActivityRequest<Integer> request=new ActivityRequest<Integer>();
		//设置查询参数
		request.setPlateform(plateform);
		request.setSort(sort);
		request.setId(id);
		// 查询正在进行的【开始时间小于现在，结束时间大于现在】
		Integer now = DateUtil.getCurrentTimeSeconds();
		request.setStartLTTime(now);
		request.setEndGTTime(now);
        request.setYhChannel(yhChannel);
		SpecialActivityBo[] list=serviceCaller.call("product.queryActivity", request, SpecialActivityBo[].class);
		if(null==list){
			list=new SpecialActivityBo[0];
			return new ApiResponse.ApiResponseBuilder().code(200).message(" no data.").data(list).build();
		}
		//重新组装返回对象
		List<Map<String, Object>>  data=new ArrayList<Map<String,Object>>();
		for(SpecialActivityBo bo:list){
			Map<String, Object> dataMap=new HashMap<String, Object>();
			dataMap.put("id", bo.getId());
			dataMap.put("sort", bo.getSort());
			dataMap.put("title", bo.getTitle());
			dataMap.put("promotion_name", bo.getPromotionName());
			dataMap.put("logo_url", bo.getLogoUrl());
			dataMap.put("cover_url", bo.getCoverUrl());
			dataMap.put("web_url", bo.getWebUrl());
            dataMap.put("web_cover_url",bo.getWebCoverUrl());
			dataMap.put("left_time", bo.getEndTime()-System.currentTimeMillis()/1000);
			dataMap.put("product_pool", bo.getProductPoolId());
			data.add(dataMap);
		}

		return new ApiResponse.ApiResponseBuilder().code(200).message("query activity  List successed.").data(data).build();
	}
	/**
	 * Outlets 活动查询
	 * 0 奥莱活动列表接口：所有的正在进行的(默认10个)
	 * 1 奥莱限时嗨购接口：正在进行的活动结束时间大于24小时的 (全部)
	 * 2 奥莱即将结束接口：结束时间小于24小时的正在进行的活动 (全部)
	 * 3 奥莱即将上线接口：活动即将在未来 24 小时内开始的活动 (全部)
	 */
	@Cachable(expire=10)
	@ResponseBody
	@RequestMapping(params = "method=app.outlets.activityGet")
	public ApiResponse outletsActivityGet(@RequestParam(value = "id", required = false) Integer id,
										  @RequestParam(value = "platform", required = true) String platform,
										  @RequestParam(value = "size", required = false) Integer size,
										  @RequestParam(value = "type", required = true) int type,
										  @RequestParam(value = "yh_channel", required = false) String yhChannel) throws GatewayException {
		if (!ArrayUtils.contains(new int[]{0, 1, 2, 3}, type)) {
			throw new ServiceException(ServiceError.PRODUCT_ACTIVITY_PARAMS_TYPE);
		}
		//设置查询参数
		ActivityRequest<Integer> request = new ActivityRequest<Integer>();
		request.setId(id);
		request.setSort("1"); // Outlets
		request.setSize(size == null ? 0 : size);
		request.setPlateform(platform);
		if (yhChannel != null && !"1,2,3,4".contains(yhChannel)) {
			yhChannel = null;
		}
		request.setYhChannel(yhChannel);

		Integer now = DateUtil.getCurrentTimeSeconds();
		int hours24 = 24 * 60 * 60;
		switch (type) {
			// 0 奥莱活动列表接口：所有的正在进行的
			case 0:
				request.setStartLTTime(now);
				request.setEndGTTime(now);
				break;
			// 1 奥莱限时嗨购接口：正在进行的活动结束时间大于24小时的 (全部)
			case 1:
				request.setStartLTTime(now);
				request.setEndGTTime(now + hours24);
				break;
			// 2 奥莱即将结束接口：结束时间小于24小时的正在进行的活动 (全部)
			case 2:
				request.setStartLTTime(now);
				request.setEndGTTime(now);
				request.setEndLTTime(now + hours24);
				break;
			// 3 奥莱即将上线接口：活动即将在未来 24 小时内开始的活动 (全部)
			case 3:
				request.setStartGTTime(now);
				request.setStartLTTime(now + hours24);
				break;
		}

		List<JSONObject> list = serviceCaller.call("product.queryActivity", request, List.class);

		// 增加开始时间，结束时间
		for(JSONObject bo: list){
			bo.put("startLeftTime", bo.getInteger("startTime")-DateUtil.getCurrentTimeSeconds());
			bo.put("endLeftTime",  bo.getInteger("endTime")-DateUtil.getCurrentTimeSeconds());
		}

		return new ApiResponse.ApiResponseBuilder().code(200).message(" query success.").data(list).build();
	}

}
