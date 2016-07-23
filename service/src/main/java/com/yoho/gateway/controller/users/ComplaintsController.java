package com.yoho.gateway.controller.users;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.service.model.order.response.CountBO;
import com.yoho.service.model.request.ComplaintAddReqBO;
import com.yoho.service.model.request.ComplaintGetReqBO;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.response.ComplaintsListRspBO;

@Controller
public class ComplaintsController {

	@Resource
	ServiceCaller serviceCaller;

	private static final Logger logger = LoggerFactory.getLogger(ComplaintsController.class);

	/**
	 * 获取我的投诉数量接口
	 * 
	 * @param uId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.complaints.getCount")
	@ResponseBody
	public ApiResponse getMyComlaintCount(@RequestParam(defaultValue = "0") Integer uid) throws GatewayException {
		logger.info("Begin call getMyComlaintCount. with param uid is {}", uid);
		ComplaintGetReqBO complaintGetReqBO = new ComplaintGetReqBO(uid);
		CountBO result = serviceCaller.call("users.getComplaintCount", complaintGetReqBO, CountBO.class);
		return new ApiResponse.ApiResponseBuilder().code(200).message("成功").data(result).build();
	}

	/**
	 * 获取我的投诉列表
	 * 
	 * @param uId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.complaints.getList")
	@ResponseBody
	public ApiResponse getMyComlaintList(@RequestParam(defaultValue = "0") Integer uid, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer limit) throws GatewayException {
		logger.info("Begin call getMyComlaintList. with param uid is {}", uid);
		ComplaintGetReqBO reqBO = new ComplaintGetReqBO(uid, page, limit);
		ComplaintsListRspBO list = serviceCaller.call("users.getComplaintList", reqBO, ComplaintsListRspBO.class);
		return new ApiResponse.ApiResponseBuilder().code(200).message("成功").data(list).build();
	}

	/**
	 * 取消投诉
	 * 
	 * @param uId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.complaints.cancel")
	@ResponseBody
	public ApiResponse cancelComlaint(@RequestParam(defaultValue = "0") Integer uid, @RequestParam(defaultValue = "0") Integer id) throws GatewayException {
		logger.info("Begin call cancelComlaint. with param uid is {},id is {}", uid, id);
		ComplaintGetReqBO complaintGetReqBO = new ComplaintGetReqBO(uid, id);
		serviceCaller.call("users.cancelComplaint", complaintGetReqBO, CommonRspBO.class);
		return new ApiResponse.ApiResponseBuilder().code(200).message("取消投诉成功").build();
	}

	/**
	 * 新增投诉
	 * 
	 * @param uId
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=web.complaints.add")
	@ResponseBody
	public ApiResponse addComlaint(@RequestParam(defaultValue = "0") Integer uid, @RequestParam(defaultValue = "") String title,
			@RequestParam(defaultValue = "") String customer, @RequestParam(defaultValue = "0") Integer complaintsType, @RequestParam(defaultValue = "") String orderCode,
			@RequestParam(defaultValue = "") String content) throws GatewayException {
		logger.info("Begin call addComlaint. with param uid is {},caption is{},customer is{},type is{},order is {},comment is{}", uid, title, customer, complaintsType, orderCode, content);
		ComplaintAddReqBO complaintAddReqBO = new ComplaintAddReqBO();
		complaintAddReqBO.setUid(uid);
		complaintAddReqBO.setTitle(title);
		complaintAddReqBO.setCustomer(customer);
		complaintAddReqBO.setComplaintsType(complaintsType.byteValue());
		complaintAddReqBO.setOrderCode(orderCode);
		complaintAddReqBO.setContent(content);
		serviceCaller.call("users.addComplaint", complaintAddReqBO, CommonRspBO.class);
		return new ApiResponse.ApiResponseBuilder().code(200).message("新增投诉成功").build();
	}

}
