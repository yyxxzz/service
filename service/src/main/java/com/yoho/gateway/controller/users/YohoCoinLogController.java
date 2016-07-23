package com.yoho.gateway.controller.users;

import java.util.Calendar;

import com.yoho.service.model.request.YohoCoinBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.request.YohoCoinVO;
import com.yoho.gateway.model.request.YohoCoinLogVO;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.service.model.request.YohoCoinLogReqBO;
import com.yoho.service.model.request.YohoCoinReqBO;
import com.yoho.service.model.response.YohoCoin;
import com.yoho.service.model.response.YohoCoinLogDataRspBO;
import com.yoho.service.model.response.YohoCurrencyRspBO;

@Controller
public class YohoCoinLogController {

	private Logger logger = LoggerFactory.getLogger(YohoCoinLogController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	// 获取用户有货币服务
	private static final String YOHOCOIN_SERVICE = "users.getYohoCoin";
		
	// 获取用户有货币数量服务
	private static final String YOHOCOIN_TOTAL_SERVICE = "users.getYohoCoinNum";

	// 获取用户有货币流水服务
	private static final String YOHOCOIN_LISTS_SERVICE = "users.getYohoCoinLog";

	// uid为空的code和message
	private static final int UID_IS_NULL_CODE = 404;
	private static final String UID_IS_NULL_MESSAGE = "uid为空！";

	// 获取有货币数量成功的code和message
	private static final int SUCCESS_TOTAL_CODE = 200;
	private static final String SUCCESS_TOTAL_MESSAGE = "yoho coin total";

	// 获取有货币流水成功的code和message
	private static final int SUCCESS_LISTS_CODE = 200;
	private static final String SUCCESS_LISTS_MESSAGE = "yoho coin list";
	
	//有货币即将到期提醒信息
	private static final String YOHOCOIN_NEAREXP_NOTICE = "您有%d个YOHO币即将于%d年12月31日过期，请尽快使用";

	@RequestMapping(params = "method=app.yoho.yohocoin")
	@ResponseBody
	public ApiResponse getYohoCoin(YohoCoinLogVO yohoCoinLogVO) {
		logger.debug("Begin call YohoCoinLogController.getYohoCoin gateway. uid is {}", yohoCoinLogVO.getUid());
		
		YohoCoinLogReqBO yohoCoinLogReqBO = new YohoCoinLogReqBO();
		BeanUtils.copyProperties(yohoCoinLogVO, yohoCoinLogReqBO);
		try {
			YohoCoin result = serviceCaller.call(YOHOCOIN_SERVICE, yohoCoinLogReqBO, YohoCoin.class);
			int nearExpCoinNum = result.getNearExpNum();
			String notice = "";
			if(nearExpCoinNum > 0){
				int currentYear = Calendar.getInstance().get(Calendar.YEAR);
				notice = String.format(YOHOCOIN_NEAREXP_NOTICE, nearExpCoinNum, currentYear);
			}
			
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("yohocoin_num", result.getCoinNum());
			jsonObject.put("notice", notice);
			jsonObject.put("nearExpCoinNum", nearExpCoinNum);
			//jsonObject.put("code", "20111130-152530");
			return new ApiResponse.ApiResponseBuilder().code(SUCCESS_TOTAL_CODE).message(SUCCESS_TOTAL_MESSAGE).data(jsonObject).build();
		} catch(ServiceException e) {
			if (ServiceError.UID_IS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(UID_IS_NULL_CODE).message(UID_IS_NULL_MESSAGE).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}
	}
	
	/**
	 * 获取有货币的总数<br>
	 * 使用场景： <br>
	 * 1.YOHO的APP中的YOHO币<br>
	 * 2.mars points中的mars点数
	 * @param yohoCoinLogVO
	 * @return
	 */
	@RequestMapping(params = "method=app.yohocoin.total")
	@ResponseBody
	public ApiResponse getYohoCoinNum(YohoCoinLogVO yohoCoinLogVO) {
		logger.debug("Begin call YohoCoinLogController.getYohoCoinNum gateway. uid is {}", yohoCoinLogVO.getUid());
		
		YohoCoinLogReqBO yohoCoinLogReqBO = new YohoCoinLogReqBO();
		BeanUtils.copyProperties(yohoCoinLogVO, yohoCoinLogReqBO);
		try {
			Integer result = serviceCaller.call(YOHOCOIN_TOTAL_SERVICE, yohoCoinLogReqBO, Integer.class);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("total", result);
			jsonObject.put("code", "20111130-152530");
			return new ApiResponse.ApiResponseBuilder().code(SUCCESS_TOTAL_CODE).message(SUCCESS_TOTAL_MESSAGE).data(jsonObject).build();
		} catch(ServiceException e) {
			if (ServiceError.UID_IS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(UID_IS_NULL_CODE).message(UID_IS_NULL_MESSAGE).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}
	}

	/**
	 * 获取有货币的明细<br>
	 * 使用场景:<br>
	 * 1.YOHO APP的有货币 明细
	 * 2.mars points明细
	 * @param yohoCoinLogVO
	 * @return
	 */
	@RequestMapping(params = "method=app.yohocoin.lists")
	@ResponseBody
	public ApiResponse getYohoCoinLog(YohoCoinLogVO yohoCoinLogVO) {
		logger.debug("Begin call YohoCoinLogController.getYohoCoinLog gateway. uid is {},limit is {},page is {}", yohoCoinLogVO.getUid(), yohoCoinLogVO.getLimit(), yohoCoinLogVO.getPage());

		YohoCoinLogReqBO yohoCoinLogReqBO = new YohoCoinLogReqBO();
		yohoCoinLogReqBO.setUid(yohoCoinLogVO.getUid());
		yohoCoinLogReqBO.setPage(yohoCoinLogVO.getPage());
		yohoCoinLogReqBO.setLimit(yohoCoinLogVO.getLimit());
		yohoCoinLogReqBO.setQueryType(yohoCoinLogVO.getQueryType());
		yohoCoinLogReqBO.setBeginTime(StringUtils.isEmpty(yohoCoinLogVO.getBeginTime()) ? 0 : (int) (DateUtil.stringToDate(yohoCoinLogVO.getBeginTime(), DateUtil.DATE_FORMAT).getTime() / 1000));
		yohoCoinLogReqBO.setEndTime(StringUtils.isEmpty(yohoCoinLogVO.getEndTime()) ? 0 : (int) (DateUtil.stringToDate(yohoCoinLogVO.getEndTime(), DateUtil.DATE_FORMAT).getTime() / 1000));
		
		try {
			YohoCoinLogDataRspBO result = serviceCaller.call(YOHOCOIN_LISTS_SERVICE, yohoCoinLogReqBO, YohoCoinLogDataRspBO.class);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("coinlist", result.getData());
			jsonObject.put("page_total", result.getCurrTotal());
			jsonObject.put("limit", result.getLimit());
			jsonObject.put("total", result.getTotal());
			jsonObject.put("page", result.getCurrPage());
			return new ApiResponse.ApiResponseBuilder().code(SUCCESS_LISTS_CODE).message(SUCCESS_LISTS_MESSAGE).data(jsonObject).build();
		} catch (ServiceException e) {
			if (ServiceError.UID_IS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(UID_IS_NULL_CODE).message(UID_IS_NULL_MESSAGE).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}
	}

	/**
	 * 获得有货币<br>
	 * 使用场景如下：<br>
	 * 1.(mars points) 地点签到<br>
	 * 2.(mars points) 合格评价<br>
	 * @param reqBO
	 * @return
	 */
	@RequestMapping(params = "method=app.yohocoin.add")
	@ResponseBody
	public ApiResponse addYohoCoin(YohoCoinVO vo) {
		logger.info("call YohoCoinLogController.addYohoCoin gateway. request is {}", vo);
		YohoCoinBO bo = new YohoCoinBO();
		BeanUtils.copyProperties(vo, bo);
		YohoCurrencyRspBO rspBO = serviceCaller.call("users.addYohoCoin", bo, YohoCurrencyRspBO.class);
		logger.info("call YohoCoinLogController.addYohoCoin gateway. response is {}", rspBO);
		return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(rspBO).build();
	}
	
	/**
	 * 兑换有货币<br>
	 * 使用场景如下：<br>
	 * 1.(mars points) 兑换实物<br>
	 * 2.(mars points) 兑换话费<br>
	 * 3.(mars points) 兑换电子券<br>
	 * @param reqBO
	 * @return
	 */
	@RequestMapping(params = "method=app.yohocoin.subtract")
	@ResponseBody
	public ApiResponse subtractYohoCoin(YohoCoinVO vo) {
		logger.info("call YohoCoinLogController.subtractYohoCoin gateway. request is {}", vo);
		YohoCoinBO bo = new YohoCoinBO();
		BeanUtils.copyProperties(vo, bo);
		YohoCurrencyRspBO rspBO = serviceCaller.call("users.subtractYohoCoin", bo, YohoCurrencyRspBO.class);
		logger.info("call YohoCoinLogController.subtractYohoCoin gateway. response is {}", rspBO);
		return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(rspBO).build();
	}
	
	/**
	 * 有货币兑换失败返回被使用的有货币<br>
	 * 使用场景如下：<br>
	 * 1.(mars points) mars点数兑换失败返回被使用的mars点数<br>
	 * @param reqBO
	 * @return
	 */
	@RequestMapping(params = "method=app.yohocoin.return")
	@ResponseBody
	public ApiResponse returnYohoCoin(YohoCoinVO vo) {
		logger.info("call YohoCoinLogController.returnYohoCoin gateway. request is {}", vo);
		YohoCoinBO bo = new YohoCoinBO();
		BeanUtils.copyProperties(vo, bo);
		YohoCurrencyRspBO rspBO = serviceCaller.call("users.returnYohoCoin", bo, YohoCurrencyRspBO.class);
		logger.info("call YohoCoinLogController.returnYohoCoin gateway. response is {}", rspBO);
		return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(rspBO).build();
	}
	

	/**
	 * yoho币变更<br>
	 * 使用场景如下：<br>
	 * 1.(YOHO的APP) 购买商品的时候，赠送有YOHO币<br>
	 * 2.(YOHO的APP) 下单的时候，扣除有货币<br>
	 * @param reqBO
	 * @return
	 */
	@RequestMapping("/erp/yohocoin/change")
	@ResponseBody
	public ApiResponse changeYohoCoin(@RequestBody YohoCoinReqBO reqBO) {
		logger.info("call YohoCoinLogController.changeYohoCoin gateway. request is {}", reqBO);
		YohoCurrencyRspBO rspBO = serviceCaller.call("users.changeYohoCoin", reqBO, YohoCurrencyRspBO.class);
		logger.info("call YohoCoinLogController.changeYohoCoin gateway. response is {}", rspBO);
		return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(rspBO).build();
	}


	/**
	 * yoho币退还
	 * @param reqBO
	 * @return
	 */
	@RequestMapping("/erp/yohocoin/refund")
	@ResponseBody
	public ApiResponse refundYohoCoin(@RequestBody YohoCoinReqBO reqBO) {
		logger.info("call YohoCoinLogController.refundYohoCoin gateway. request is {}", reqBO);
		YohoCurrencyRspBO rspBO = serviceCaller.call("users.refundYohoCoin", reqBO, YohoCurrencyRspBO.class);
		logger.info("call YohoCoinLogController.changeYohoCoin gateway. response is {}", rspBO);
		return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(rspBO).build();
	}
}
