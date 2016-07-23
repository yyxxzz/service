package com.yoho.gateway.controller.users;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.RedEnvelopesReqVO;
import com.yoho.service.model.promotion.GateBo;
import com.yoho.service.model.promotion.request.GateReq;
import com.yoho.service.model.request.RedEnvelopesReqBO;
import com.yoho.service.model.response.RedPacketInfoResponseBO;
import com.yoho.service.model.response.RedpacketResponseBO;
import com.yoho.service.model.response.UserRedPacketResponseBO;

@Controller
public class RedEnvelopesController {

	static Logger logger = LoggerFactory.getLogger(RedEnvelopesController.class);
	
	@Resource
	ServiceCaller serviceCaller;
	
	/**
	 * 获取红包开关
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.yoho.redpacket")
	@ResponseBody
	public ApiResponse redpacket() throws GatewayException {
		logger.debug("enter redpacket");
		logger.info("Begin call redpacket gateway.");
		GateReq req = new GateReq();
		req.setMetaKey("cartUseRedEnvelope");
		GateBo result = serviceCaller.call("promotion.queryGateInfo", req, GateBo.class);
		logger.info("call users.redpacket with result is {}", result);
		RedpacketResponseBO red = new RedpacketResponseBO();
		red.setOpen((result != null && result.getStatus() != null && result.getStatus().intValue() == 1) ? "Y" : "N");
		ApiResponse response = new ApiResponse.ApiResponseBuilder().data(red).build();
		return response;
	}
	
	/**
	 * 获取红包数量和有效期
	 * @param vo
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.yoho.redpacketInfo")
	@ResponseBody
	public ApiResponse redpacketInfo(RedEnvelopesReqVO vo) throws GatewayException {
		logger.debug("enter redpacketInfo");
		logger.info("Begin call redpacketInfo gateway. with param is {}", vo);
		RedEnvelopesReqBO bo = new RedEnvelopesReqBO();
		BeanUtils.copyProperties(vo, bo);
		RedPacketInfoResponseBO result = serviceCaller.call("users.redpacketInfo", bo, RedPacketInfoResponseBO.class);
		logger.info("call users.redpacketInfo with result is {}", result);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().data(result).build();
		return response;
	}
	
	/**
	 * 获取用户的红包列表
	 * @param vo
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.yoho.redpacketList")
	@ResponseBody
	public ApiResponse redpacketList(RedEnvelopesReqVO vo) throws GatewayException {
		logger.debug("enter redpacketList");
		logger.info("Begin call redpacketList gateway. with param is {}", vo);
		RedEnvelopesReqBO bo = new RedEnvelopesReqBO();
		BeanUtils.copyProperties(vo, bo);
		UserRedPacketResponseBO[] result = serviceCaller.call("users.redpacketList", bo, UserRedPacketResponseBO[].class);
		logger.info("call users.redpacketList with result.length is {}", result == null ? 0 : result.length);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().data(result).build();
		return response;
	}
}
