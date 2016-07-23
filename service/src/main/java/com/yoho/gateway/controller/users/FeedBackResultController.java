package com.yoho.gateway.controller.users;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.FeedBackReqVO;
import com.yoho.service.model.request.FeedBackReqBO;
import com.yoho.service.model.response.FeedBackRspBO;

/**添加反馈
 * 往yh_operations.feedback_result表中插入数据
 * @author gezhengwen
 *
 */
@Controller
public class FeedBackResultController {
	
	private Logger logger = LoggerFactory.getLogger(FeedBackResultController.class);
	
	@Autowired
	private ServiceCaller serviceCaller;
	/**
	 * 添加反馈
	 */
	@RequestMapping(params = "method=open.feedback.submit")
	@ResponseBody
	public ApiResponse saveFeedBack(FeedBackReqVO vo) throws GatewayException{
		logger.info("FeedBackResultController.saveFeedBack param is{}",vo);
		int question_id = vo.getQuestion_id();
		int feedback_id = vo.getFeedback_id();
		String solution = vo.getSolution();
		String answer = vo.getAnswer();
		if(question_id<1){
			logger.warn("saveFeedBack fail because question_id is null , request is {}",vo);
			throw new ServiceException(ServiceError.QUESTION_ID_ISNULL);
		}
		if(feedback_id<1){
			logger.warn("saveFeedBack fail because feedback_id is null , request is {}",vo);
			throw new ServiceException(ServiceError.FEEDBACK_ID_ISNULL);
		}
		if(StringUtils.isEmpty(solution)&&StringUtils.isEmpty(answer)){
			logger.warn("saveFeedBack fail because solution and answer is null , request is {}",vo);
			throw new ServiceException(ServiceError.ANSWER_ISNULL);
		}
		FeedBackReqBO bo = new FeedBackReqBO();
		bo.setAnswer(answer);
		bo.setREMOTE_ADDR(vo.getREMOTE_ADDR());
		bo.setFeedback_id(feedback_id);
		bo.setQuestion_id(question_id);
		bo.setUid(vo.getUid());
		bo.setSolution(solution);
		FeedBackRspBO response = serviceCaller.call("users.saveFeedBack", bo, FeedBackRspBO.class);
		return new ApiResponse.ApiResponseBuilder().message("success").data(response).build();
		
		
	}
	
}
