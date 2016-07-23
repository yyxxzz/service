package com.yoho.gateway.controller.guang;

import javax.annotation.Resource;

import com.yoho.service.model.sns.SnsServices;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.yoho.gateway.utils.StripTagsUtil;
import com.yoho.service.model.sns.request.PlustarGetBrandInfoReqBO;
import com.yoho.service.model.sns.request.PlustarReqBO;
import com.yoho.service.model.sns.request.ShareBrandReqBO;
import com.yoho.service.model.sns.response.ShareBrandRspBO;
import com.yoho.service.model.sns.response.plustar.PlustarBrandInfoRespBO;
import com.yoho.service.model.sns.response.plustar.PlustarRespBO;

/**
 * @author yoho
 *
 */
@Controller
@RequestMapping(value = "/guang/api")
public class ShareBrandController {

	private static Logger logger = LoggerFactory.getLogger(ShareBrandController.class);


	@Resource
	ServiceCaller serviceCaller;

	/**
	 *功能描述：获取分享品牌的详情服务；场景：点击品牌进行分享操作时触发
	 * @param id
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(value = "/*/share/brandinfo")
	@ResponseBody
	public ApiResponse getShareBrandInfo(int id) throws GatewayException {
		logger.debug("Enter ShareBrandController.getShareBrandInfo. param id is {}", id);

		// (1)初始化参数
		ShareBrandReqBO shareBrandReqBO = new ShareBrandReqBO();
		shareBrandReqBO.setId(id);

		// (2)请求服务，获取分享品牌详情
		ShareBrandRspBO shareBrandRspBO = serviceCaller.call(SnsServices.getShareBrandInfo, shareBrandReqBO, ShareBrandRspBO.class);

		// (3)组装返回
		JSONObject result = new JSONObject();
		result.put("title", shareBrandRspBO.getBrandName());
		result.put("content", StringUtils.isBlank(shareBrandRspBO.getBrandIntro()) ? "" : StripTagsUtil.parse(StringEscapeUtils.unescapeHtml(shareBrandRspBO.getBrandIntro())));
		result.put("pic", shareBrandRspBO.getBrandIcoImgUrl());
		result.put("url", shareBrandRspBO.getUrl());
		return new ApiResponse.ApiResponseBuilder().code(200).message("品牌详情分享").data(result).build();
	}

	/**
	 * 明星品牌列表(潮流优选,明星原创) 缓存10秒
	 *功能描述：获取明星原创品牌详情列表；场景：点击首页“明星原创”时触发
	 * @param brand_type
	 * @param gender
	 * @return
	 * @throws GatewayException
	 */
	@Cachable(expire = 10)
	@RequestMapping(value = "/*/plustar/getlist")
	@ResponseBody
	public ApiResponse getPlustarList(@RequestParam byte brand_type, @RequestParam(required = false) String gender) throws GatewayException {
		logger.info("Enter ShareBrandController.getPlustarList. param brand_type is {}, gender is {}", brand_type, gender);

		// (1)初始化参数
		PlustarReqBO plustarReqBO = new PlustarReqBO();
		plustarReqBO.setBrand_type(brand_type);
		plustarReqBO.setGender(gender);

		// (2)请求服务，获取明星原创平牌列表
		PlustarRespBO plustarRespBO = serviceCaller.call(SnsServices.getPlustarList, plustarReqBO, PlustarRespBO.class);

		// (3)组装返回
		return new ApiResponse.ApiResponseBuilder().code(200).message("品牌列表").data(plustarRespBO).build();
	}

	/**
	 * 功能描述：根据品牌关系id，获取品牌详情；场景：进入“明星原创”模块后，点击某一个品牌进入品牌详情时触发
	 * @param id
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(value = "/*/plustar/getbrandinfo")
	@ResponseBody
	public ApiResponse getBrandInfo(String id) throws GatewayException {
		logger.debug("Enter getBrandInfo. param id is {}", id);

		if (StringUtils.isEmpty(id) || !id.matches("\\d+")) {
			logger.warn("getBrandInfo error with id={}", id);
			throw new ServiceException(ServiceError.ID_IS_NULL);
		}

		// (1)初始化参数
		PlustarGetBrandInfoReqBO plustarReqBO = new PlustarGetBrandInfoReqBO();
		plustarReqBO.setId(Integer.parseInt(id));

		// (2)请求服务，获取明星原创平牌列表
		PlustarBrandInfoRespBO plustarRespBO = serviceCaller.call(SnsServices.getBrandInfo, plustarReqBO, PlustarBrandInfoRespBO.class);

		// (3)组装返回
		return new ApiResponse.ApiResponseBuilder().code(200).message("品牌详情").data(plustarRespBO).build();
	}

}
