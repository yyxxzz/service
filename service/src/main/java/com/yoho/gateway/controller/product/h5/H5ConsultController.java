package com.yoho.gateway.controller.product.h5;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Lists;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.product.model.ConsultBo;
import com.yoho.product.request.ConsultAddRequest;
import com.yoho.product.request.ConsultQueryRequest;
import com.yoho.product.response.VoidResponse;

@Controller
public class H5ConsultController {
	
    private final Logger logger = LoggerFactory.getLogger(H5ConsultController.class);
    
  	//UID不能为空
  	private final static int UID_IS_NULL_CODE = 404;
  	private final static String UID_IS_NULL_MSG = "Uid Is Null.";
  		
  	//新增时产品ID不能为空
  	private final static int ADD_PRODUCT_ID_IS_NULL_CODE = 404;
  	private final static String ADD_PRODUCT_ID_IS_NULL_MSG = "产品id不能为空.";
  	
  	//新增时内容不能为空
  	private final static int CONTENT_IS_NULL_CODE = 404;
  	private final static String CONTENT_IS_NULL_MSG = "内容不能为空.";
  	
  	//查询时产品ID不能为空
  	private final static int PRODUCT_IS_NULL_CODE = 500;
  	private final static String PRODUCT_IS_NULL_MSG = "产品id不能为空.";
  	
	@Autowired
	private ServiceCaller serviceCaller;
	
	@RequestMapping(params = "method=h5.consult.li")
	@ResponseBody
	public List<ConsultBo> getConsultList(@RequestParam(value="page", required=false, defaultValue="0") int pageNum,
			@RequestParam(value="limit", required=false, defaultValue="10") int limit,
			@RequestParam(value="product_id", required=true) Integer productId) throws GatewayException {
		logger.debug("H5 Begin call getConsultList. Param page is {}, limit is {}, product_id is {}",
				pageNum, limit, productId);
		//校验参数product_id不能为空，或者为0
		if(null == productId){
			logger.warn("Parameter product_id is {}", productId);
			throw new GatewayException(PRODUCT_IS_NULL_CODE, PRODUCT_IS_NULL_MSG);
		}
		ConsultQueryRequest consultQueryRequest=new ConsultQueryRequest();
		consultQueryRequest.setPageNum(pageNum);
		consultQueryRequest.setPageSize(limit);
		consultQueryRequest.setProductId(productId);
		
		//product.queryConsults---->/consult/queryConsults
		ConsultBo[] consultBoList = serviceCaller.call("product.queryConsults", consultQueryRequest, ConsultBo[].class);
		
		return Lists.newArrayList(consultBoList);
	}
	
	/**
	 * 添加咨询信息
	 * 
	 * @param product_id 产品id
	 * @param uid 用户id
	 * @param content 咨询内容
	 * @return ApiResponse
	 * @throws GatewayException 
	 */
	@RequestMapping(params = "method=h5.consult.add")
	@ResponseBody
	public VoidResponse addConsult(@RequestParam("product_id") int product_id,
			@RequestParam("uid") int uid,
			@RequestParam("content") String content) throws GatewayException {
		logger.info("H5 Begin call getCouponsList gateway. Param product_id is {}, uid is {}, content is {}", product_id, uid, content);
		//校验参数uid，product_id以及content不能为空，或者为0
		if(uid < 1){
			logger.warn("Parameter uid is null. uid={}, product_id={}, content={}", uid, product_id, content);
			throw new GatewayException(UID_IS_NULL_CODE, UID_IS_NULL_MSG);
		}
		if(product_id < 1){
			logger.warn("Parameter product_id is null. uid={}, product_id={}, content={}", uid, product_id, content);
			throw new GatewayException(ADD_PRODUCT_ID_IS_NULL_CODE, ADD_PRODUCT_ID_IS_NULL_MSG);
		}
		if(null == content || content.isEmpty()){
			logger.warn("Parameter content is null or empty. uid={}, product_id={}, content={}", uid, product_id, content);
			throw new GatewayException(CONTENT_IS_NULL_CODE, CONTENT_IS_NULL_MSG);
		}
		
		ConsultAddRequest consultAddRequest=new ConsultAddRequest();
		consultAddRequest.setAskContent(content);
		consultAddRequest.setAskUserId(uid);
		consultAddRequest.setProductId(product_id);
		
		//调用新增咨询服务
		//product.addConsult---->/consult/addConsult
		int affectRow=serviceCaller.call("product.addConsult", consultAddRequest, Integer.class);
		if(affectRow>0)
		{
			logger.info("Add consult end and add successed");
		}
		return new VoidResponse(VoidResponse.CODE);
	}
}
