package com.yoho.gateway.controller.search;

import com.yoho.gateway.cache.expire.product.ExpireTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.core.rest.exception.ServiceNotFoundException;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.search.convert.SalesCategoryConvert;
import com.yoho.gateway.model.search.SalesCategoryRsp;
import com.yoho.product.model.SalesCategoryRspBo;
import com.yoho.product.request.SalesCategoryReq;

@Controller
public class SalesCategoryController {
	/**
	 * 正常的状态
	 */
	private static final int INIT_STATE = 1;

	private final Logger logger = LoggerFactory.getLogger(SalesCategoryController.class);
	
	@Autowired
    private ServiceCaller serviceCaller;
	
	@Autowired
	private SalesCategoryConvert convert;
	
	/**
	 * 查询所有的分类列表
	 * @param gender 性别
	 * @param yh_channel 渠道
	 * @return 所有的分类列表
	 */
	@RequestMapping(params = "method=app.sort.get")
	@ResponseBody
	@Cachable(expire= ExpireTime.app_sort_get)
	public ApiResponse querySalesCategoryList() {
		SalesCategoryRspBo responseBean = null;
		
		SalesCategoryReq req = new SalesCategoryReq();
		req.setState(INIT_STATE);
		try
		{
			responseBean = serviceCaller.call("product.querySalesCategoryList", req, SalesCategoryRspBo.class);
		} catch (ServiceException e) {
			logger.warn("query querySalesCategoryList failed:{}",e);
			return new ApiResponse.ApiResponseBuilder().code(e.getCode()).message("query find wrong.").data(null).build();
		} catch (ServiceNotAvaibleException e) {
			logger.warn("query querySalesCategoryList failed:{}",e);
			return new ApiResponse.ApiResponseBuilder().code(500).message("Service not avaible.").data(null).build();
		} catch (ServiceNotFoundException e) {
			logger.warn("query querySalesCategoryList failed:{}",e);
			return new ApiResponse.ApiResponseBuilder().code(404).message("Service not found.").data(null).build();
		}
		SalesCategoryRsp salesCategoryRsp = null;
		//转换成VO
		if (null != responseBean) {
			salesCategoryRsp = convert.convert(responseBean.getData());
		} else {
			return new ApiResponse.ApiResponseBuilder().code(500).message("The data of query is null.").data(null).build();
		}
		return new ApiResponse.ApiResponseBuilder().code(200).message("category List.").data(salesCategoryRsp.getData()).build();
	}
}
