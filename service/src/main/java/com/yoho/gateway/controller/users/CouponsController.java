package com.yoho.gateway.controller.users;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.response.CouponsDataRspVO;
import com.yoho.gateway.model.response.UserCouponsRspVO;
import com.yoho.service.model.request.CouponsReqBO;
import com.yoho.service.model.response.CouponsDataRspBO;
import com.yoho.service.model.response.CouponsRspBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CouponsController {

	@Resource
	private ServiceCaller serviceCaller;
	
	//请求成功
	private final static int REQUEST_SUCCESS_CODE = 200;
	private final static String REQUEST_SUCCESS_MSG = "获取优惠券信息成功.";
	
	//UID不能为空
	private final static int UID_IS_NULL_CODE = 500;
	private final static String UID_IS_NULL_MSG = "Uid Is Null.";
	
	//获取优惠券数量成功
	private final static int COUPONS_TOTAL_SUCCESS_CODE = 200;
	private final static String COUPONS_TOTAL_SUCCESS_MSG = "coupons total";

	
	//获取用户优惠信息列表请求地址
	private final static String COUPONS_LIST_URL = "users.getCouponsList";
	
	private final static String COUPONS_COUNT_URL = "users.getCouponsCount";
		
	private Logger logger = LoggerFactory.getLogger(CouponsController.class);

	//重名需要废弃
	@RequestMapping(params = "method=app.coupons.li.bak")
	@ResponseBody
	public ApiResponse getCouponsList(@RequestParam("page") int page,
			@RequestParam("limit") int limit,
			@RequestParam("status") int status, @RequestParam("uid") int uid) throws GatewayException {
		logger.debug("Begin call getCouponsList gateway. Param page is {}, limit is {}, status is {}, uid is {}", page, limit, status, uid);
		ApiResponse responseBean = null;
		//(1)校验参数uid不能为空，或者为0
		if(uid < 1){
			logger.warn("Parameter uid is {}", uid);
			throw new GatewayException(UID_IS_NULL_CODE, UID_IS_NULL_MSG);
		}
		
		//(2)组装调用服务参数
		CouponsReqBO couponsReqBO = new CouponsReqBO(limit, page, status, uid, null);
		
		//(3)调用查询优惠券列表信息服务
		CouponsDataRspBO couponsDataRspBO = serviceCaller.call(COUPONS_LIST_URL, couponsReqBO, CouponsDataRspBO.class);
//		
//		//(4)判断查询结果是否成功，成功返回优惠券列表，失败返回失败
//		if(ServiceError.CODE_SUCCESS.getCode() != returnModel.getErrorCode()){
//			logger.warn("Get user coupons list failed. Param uid={}, limit={}, page={}, status={}", uid, limit, page, status);
//			responseBean = new ApiResponse(REQUEST_FALIED_CODE, REQUEST_FALIED_MSG, null);
//			return responseBean;
//		}
		int total = couponsDataRspBO.getTotal();
		List<CouponsRspBO> couponsRspBOList = couponsDataRspBO.getInfo();
		//(4) 如果返回参数为空，返回空记录
		if(null == couponsRspBOList || couponsRspBOList.isEmpty()){
			logger.info("Get user coupons list is null.");
			CouponsDataRspVO dataRspVo = new CouponsDataRspVO(null, 0);
			responseBean = new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message(REQUEST_SUCCESS_MSG).data(dataRspVo).build();
			return responseBean;
		}
		
		//返回纪录不为空
		
		//将服务返回的参数，和网关返回的参数进行映射
		List<UserCouponsRspVO> voList = new ArrayList<UserCouponsRspVO>();
		for(CouponsRspBO couponsRspBO : couponsRspBOList){
			UserCouponsRspVO userCouponsRspVO = new UserCouponsRspVO();
			userCouponsRspVO.setCoupon_id(couponsRspBO.getCouponId());
			userCouponsRspVO.setCoupon_name((null == couponsRspBO.getCouponName())? "" : couponsRspBO.getCouponName());
			userCouponsRspVO.setCoupon_pic((null == couponsRspBO.getCouponPic())? "" : couponsRspBO.getCouponPic());
			userCouponsRspVO.setCouponValidity((null == couponsRspBO.getCouponValidity())? "" : couponsRspBO.getCouponValidity());
			userCouponsRspVO.setId(couponsRspBO.getId());
			userCouponsRspVO.setMoney(couponsRspBO.getMoney());
			userCouponsRspVO.setIs_overtime(couponsRspBO.getIsOverTime());
			userCouponsRspVO.setOrder_code((null == couponsRspBO.getOrderCode())? "": couponsRspBO.getOrderCode().toString());
			voList.add(userCouponsRspVO);
		}
		CouponsDataRspVO dataRspVo = new CouponsDataRspVO(voList, total);
		responseBean = new ApiResponse.ApiResponseBuilder().code(REQUEST_SUCCESS_CODE).message(REQUEST_SUCCESS_MSG).data(dataRspVo).build();
		return responseBean;
	}
	
	/**
	 * 获取用户优惠券数量
	 * 
	 * @param type 优惠券类型 notuse:未使用，use：已使用
	 * @param uid 用户id
	 * @return ApiResponse
	 */
	//历史遗留问题 导致此接口存在问题 已经在promotion重写
	@RequestMapping(params = "method=app.coupons.total.bak")
	@ResponseBody
	public ApiResponse getCouponsCount(@RequestParam("type") String type, @RequestParam("uid") int uid) {
		logger.debug("Begin call getCouponsList gateway. Param type is {}, uid is {}", type, uid);
		ApiResponse responseBean = null;
		//(1)组装请求参数，并提交新增请求
		CouponsReqBO couponsReqBO = new CouponsReqBO(uid, type);
		Integer count = serviceCaller.call(COUPONS_COUNT_URL, couponsReqBO, Integer.class);
		
		//(2)组装返回
		Map<String, Integer> dataMap = new HashMap<String, Integer>();
		dataMap.put("total", count);
		responseBean = new ApiResponse.ApiResponseBuilder().code(COUPONS_TOTAL_SUCCESS_CODE).message(COUPONS_TOTAL_SUCCESS_MSG).data(dataMap).build();
		return responseBean;
	}
}
