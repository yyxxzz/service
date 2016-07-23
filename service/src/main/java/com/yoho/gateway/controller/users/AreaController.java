package com.yoho.gateway.controller.users;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.request.AreaReqBO;
import com.yoho.service.model.response.AreaRspBo;

@Controller
public class AreaController {

	private Logger logger = LoggerFactory.getLogger(AreaController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	// 获取省、市、区列表服务
	private static final String PROVINCES_SERVICE = "users.areaProvinces";

	// 获取省、市、区列表成功的code和message
	private static final int PROVINCES_SUCCESS_CODE = 200;
	private static final String PROVINCES_SUCCESS_MSG = "provinces list.";

	// 获取省、市、区列表服务
	private static final String GETAREALIST_SERVICE = "users.getAreaList";

	// 获取省、市、区列表成功的code和message
	private static final int GETAREALIST_SUCCESS_CODE = 200;
	private static final String GETAREALIST_SUCCESS_MSG = "Area list.";
	
	// 获取国家码服务
	private static final String NATONALITY_SERVICE = "users.getNationality";
	
	// 获取国家码成功的code和message
	private static final int NATONALITY_SUCCESS_CODE = 200;
	private static final String NATONALITY_SUCCESS_MSG = "获取地区区号成功";

	/**
	 * 根据地址id 获得省、市、地区列表
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(params = "method=app.address.provinces")
	@ResponseBody
	@Cachable
	public ApiResponse provinces(@RequestParam(name = "id") int id) {
		logger.debug("Begin call AreaController.provinces gateway. id is {}", id);

		// (1)构造请求对象
		AreaReqBO areaReqBO = new AreaReqBO();
		areaReqBO.setId(id);

		try {
			// (2)调用服务
			AreaRspBo[] result = serviceCaller.call(PROVINCES_SERVICE, areaReqBO, AreaRspBo[].class);

			// (3)构造返回
			JSONArray jsonArray = new JSONArray();
			for (AreaRspBo areaRspBo : result) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("id", String.valueOf(areaRspBo.getId()));
				jsonObject.put("caption", areaRspBo.getCaption());
				jsonObject.put("is_support_express", areaRspBo.getIsSupport());
				jsonArray.add(jsonObject);
			}

			// (4)返回
			return new ApiResponse.ApiResponseBuilder().code(PROVINCES_SUCCESS_CODE).message(PROVINCES_SUCCESS_MSG).data(jsonArray).build();
		} catch (Exception e) {
			throw new ServiceNotAvaibleException(PROVINCES_SERVICE, e);
		}
	}

	/**
	 * 根据地址id 获得省、市、地区列表
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(params = "method=app.address.getlist")
	@ResponseBody
	@Cachable
	public ApiResponse getAreaList() {
		logger.debug("Begin call AreaController.getAreaList gateway.");

		try {
			// (1)调用服务
			AreaRspBo[] result = serviceCaller.call(GETAREALIST_SERVICE, "", AreaRspBo[].class);

			// (2)返回
			return new ApiResponse.ApiResponseBuilder().code(GETAREALIST_SUCCESS_CODE).message(GETAREALIST_SUCCESS_MSG).data(result).build();
		} catch (ServiceException e) {
			throw new ServiceNotAvaibleException(GETAREALIST_SERVICE, e);
		}
	}
	
	/**
	 * 获取国家码
	 * 
	 * @return
	 */
	@RequestMapping(params = "method=app.passport.getArea")
	@ResponseBody
	public ApiResponse getArea(){
		logger.debug("Begin call AreaController.getArea gateway.");
		try {
			// (2)调用服务
			List<Map<String,String>> areaList = serviceCaller.call(NATONALITY_SERVICE, null, List.class);

			// (3)返回
			return new ApiResponse.ApiResponseBuilder().code(NATONALITY_SUCCESS_CODE).message(NATONALITY_SUCCESS_MSG).data(areaList).build();
		} catch (Exception e) {
			throw new ServiceNotAvaibleException(NATONALITY_SERVICE, e);
		}
	}

}
