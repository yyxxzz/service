package com.yoho.gateway.controller.users;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.helper.MobileHelper;
import com.yoho.gateway.model.request.UserAddressReqVO;
import com.yoho.service.model.request.UserAddressReqBO;
import com.yoho.service.model.response.AreaRspBo;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.response.UserAddressRspBO;
import com.yoho.service.model.response.UserAddressUpdateRspBO;

@Controller
public class AddressController {

	private Logger logger = LoggerFactory.getLogger(AddressController.class);

	@Autowired
	private ServiceCaller serviceCaller;

	// 获取用户地址列表服务
	private static final String GET_USERADDRESS_SERVICE = "users.selectAddressList";

	// 获取用户地址列表成功的code和message
	private static final int GET_SUCCESS_CODE = 200;
	private static final String GET_SUCCESS_MSG = "Address List";

	// 获取用户地址列表uid为空的code和message
	private static final int GET_UIDISNULL_CODE = 500;
	private static final String GET_UIDISNULL_MSG = "User ID Is Null.";

	// 修改用户地址服务
	private static final String UPDATE_USERADDRESS_SERVICE = "users.updateAddress";

	// 修改用户地址成功的code和message
	private static final int UPDATE_SUCCESS_CODE = 200;
	private static final String UPDATE_SUCCESS_MSG = "修改成功.";

	// 修改用户地址地址ID为空的code和message
	private static final int UPDATE_ADDRESSIDISNULL_CODE = 500;
	private static final String UPDATE_ADDRESSIDISNULL_MSG = "要修改的地址ID不正确.";

	// 修改用户地址uid为空的code和message
	private static final int UPDATE_UIDISNULL_CODE = 500;
	private static final String UPDATE_UIDISNULL_MSG = "用户ID不正确.";

	// 修改用户地址收货人姓名为空的code和message
	private static final int UPDATE_ADDRESSNAMEISNULL_CODE = 500;
	private static final String UPDATE_ADDRESSNAMEISNULL_MSG = "收货人姓名不正确.";

	// 修改用户地址收address为空的code和message
	private static final int UPDATE_ADDRESSISNULL_CODE = 500;
	private static final String UPDATE_ADDRESSISNULL_MSG = "请填写详细地址.";

	// 修改用户地址收areaCode为空的code和message
	private static final int UPDATE_AREACODEISNULL_CODE = 500;
	private static final String UPDATE_AREACODEISNULL_MSG = "省市必须选择.";

	// 修改用户地址联系方式为空的code和message
	private static final int UPDATE_PHONEANDMOBILEISNULL_CODE = 500;
	private static final String UPDATE_PHONEANDMOBILEISNULL_MSG = "手机或者电话必须填写一个.";

	// 新增用户地址服务
	private static final String ADD_USERADDRESS_SERVICE = "users.insertAddress";

	// 新增用户地址成功的code和message
	private static final int ADD_SUCCESS_CODE = 200;
	private static final String ADD_SUCCESS_MSG = "添加成功.";

	// 新增用户地址uid为空的code和message
	private static final int ADD_UIDISNULL_CODE = 500;
	private static final String ADD_UIDISNULL_MSG = "用户ID不正确.";

	// 新增用户地址收货人姓名为空的code和message
	private static final int ADD_ADDRESSNAMEISNULL_CODE = 500;
	private static final String ADD_ADDRESSNAMEISNULL_MSG = "收货人姓名不正确.";

	// 新增用户地址收address为空的code和message
	private static final int ADD_ADDRESSISNULL_CODE = 500;
	private static final String ADD_ADDRESSISNULL_MSG = "请填写详细地址.";

	// 新增用户地址收areaCode为空的code和message
	private static final int ADD_AREACODEISNULL_CODE = 500;
	private static final String ADD_AREACODEISNULL_MSG = "省市必须选择.";

	// 新增用户地址联系方式为空的code和message
	private static final int ADD_PHONEANDMOBILEISNULL_CODE = 500;
	private static final String ADD_PHONEANDMOBILEISNULL_MSG = "手机或者电话必须填写一个.";

	// 新增用户地址失败的code和message
	private static final int ADD_FAILED_CODE = 404;
	private static final String ADD_FAILED_MSG = "添加失败.";

	// 删除用户地址服务
	private static final String DEL_USERADDRESS_SERVICE = "users.delAddress";

	// 删除用户地址成功的code和message
	private static final int DEL_SUCCESS_CODE = 200;
	private static final String DEL_SUCCESS_MSG = "删除成功.";

	// 删除用户地址id或uid为空的code和message
	private static final int DEL_IDORUIDISNULL_CODE = 500;
	private static final String DEL_IDORUIDISNULL_MSG = "要修改的地址ID或者用户ID为空.";

	// 删除用户地址失败的code和message
	private static final int DEL_FAILED_CODE = 404;
	private static final String DEL_FAILED_MSG = "删除失败.";

	// 设置用户默认地址服务
	private static final String SETDEFAULT_USERADDRESS_SERVICE = "users.setAddressDefault";

	// 设置用户默认地址成功的code和message
	private static final int SETDEFAULT_SUCCESS_CODE = 200;
	private static final String SETDEFAULT_SUCCESS_MSG = "设置成功！";

	// 设置用户默认地址uid为空的code和message
	private static final int SETDEFAULT_UIDISNULL_CODE = 404;
	private static final String SETDEFAULT_UIDISNULL_MSG = "uid 不存在！";

	// 设置用户默认地址id为空的code和message
	private static final int SETDEFAULT_IDISNULL_CODE = 405;
	private static final String SETDEFAULT_IDISNULL_MSG = "id 不存在！";

	// 设置用户默认地址失败的code和message
	private static final int SETDEFAULT_FAILED_CODE = 201;
	private static final String SETDEFAULT_FAILED_MSG = "设置失败！";

	/**
	 * 获取用户地址信息列表
	 * 
	 * @param userAddressReqVO
	 * @return
	 */
	@RequestMapping(params = "method=app.address.get")
	@ResponseBody
	public ApiResponse get(UserAddressReqVO userAddressReqVO) {
		logger.debug("Begin call AddressController.get gateway. uid is {}", userAddressReqVO.getUid());

		return getUserProfile(userAddressReqVO, false);
	}
	
	/**
	 * 获取用户地址信息列表<br>
	 * 返回的手机号码中间四位用*隐藏
	 * 
	 * @param userAddressReqVO
	 * @return
	 */
	@RequestMapping(params = "method=app.address.gethidden")
	@ResponseBody
	public ApiResponse getHidden(UserAddressReqVO userAddressReqVO) {
		logger.debug("Begin call AddressController.getHidden gateway. uid is {}", userAddressReqVO.getUid());

		return getUserProfile(userAddressReqVO, true);
	}

	/**
	 * 获取用户地址信息<br>
	 * 
	 * @param userAddressReqVO
	 * @param isHidden true的场合：将手机号码用*隐藏,false的场合，不需要隐藏
	 * @return
	 */
	private ApiResponse getUserProfile(UserAddressReqVO userAddressReqVO, boolean isHidden) {
		// (1)构造服务层参数
		UserAddressReqBO userAddressReqBO = new UserAddressReqBO();
		BeanUtils.copyProperties(userAddressReqVO, userAddressReqBO);

		// (2)调用服务层接口获取数据
		try {
			UserAddressRspBO[] result = serviceCaller.call(GET_USERADDRESS_SERVICE, userAddressReqBO, UserAddressRspBO[].class);

			// (3)构造返回
			JSONArray jsonArray = new JSONArray();
			for (UserAddressRspBO userAddressRspBO : result) {
				// (3.1)拼装区域信息
				AreaRspBo areaRspBo = userAddressRspBO.getArea();
				String area = null == areaRspBo ? "" : areaRspBo.getCaption();
				if (null != areaRspBo) {
					AreaRspBo parentAreaRspBo = areaRspBo.getParent();
					area = null == parentAreaRspBo ? area : parentAreaRspBo.getCaption() + " " + area;

					if (null != parentAreaRspBo) {
						AreaRspBo parentParentAreaRspBo = parentAreaRspBo.getParent();
						area = null == parentParentAreaRspBo ? area : parentParentAreaRspBo.getCaption() + " " + area;
					}
				}

				// (3.2)构造返回数据json
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("uid", userAddressRspBO.getUid());
				jsonObject.put("address", userAddressRspBO.getAddress());
				jsonObject.put("area_code", userAddressRspBO.getAreaCode());
				jsonObject.put("zip_code", userAddressRspBO.getZipCode());
				
				if(isHidden){
					jsonObject.put("mobile", MobileHelper.coverMobile(userAddressRspBO.getMobile()));
				}else{
					jsonObject.put("mobile", userAddressRspBO.getMobile());
				}
				
				if(isHidden){
					jsonObject.put("phone", MobileHelper.coverMobile(userAddressRspBO.getPhone()));
				}else{
					jsonObject.put("phone", userAddressRspBO.getPhone());
				}
				
				
				jsonObject.put("is_default", userAddressRspBO.getIsDefault());
				jsonObject.put("email", userAddressRspBO.getEmail());
				jsonObject.put("address_id", userAddressRspBO.getId());
				jsonObject.put("area", area);
				jsonObject.put("consignee", userAddressRspBO.getAddresseeName());
				jsonObject.put("is_delivery", null == areaRspBo ? "" : areaRspBo.getIsDelivery());
				jsonObject.put("is_support", null == areaRspBo ? "" : areaRspBo.getIsSupport());
				jsonArray.add(jsonObject);
			}
			return new ApiResponse.ApiResponseBuilder().code(GET_SUCCESS_CODE).message(GET_SUCCESS_MSG).data(jsonArray).build();
		} catch (ServiceException e) {
			if (ServiceError.UID_IS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(GET_UIDISNULL_CODE).message(GET_UIDISNULL_MSG).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}
	}

	/**
	 * 修改用户地址信息
	 * 
	 * @param userAddressReqVO
	 * @return
	 */
	@RequestMapping(params = "method=app.address.update")
	@ResponseBody
	public ApiResponse update(UserAddressReqVO userAddressReqVO) {
		logger.debug("Begin call AddressController.update gateway. userAddressReqVO is {}", userAddressReqVO);

		if (StringUtils.isNotEmpty(userAddressReqVO.getMobile()) && userAddressReqVO.getMobile().length() > 20) {
			return new ApiResponse.ApiResponseBuilder().code(500).message("手机号码过长.").build();
		}

		// (1)构造服务层参数
		UserAddressReqBO userAddressReqBO = new UserAddressReqBO();
		BeanUtils.copyProperties(userAddressReqVO, userAddressReqBO);
		userAddressReqBO.setAddressee_name(userAddressReqVO.getConsignee());

		// (2)调用服务层接口获取数据
		try {
			UserAddressUpdateRspBO result = serviceCaller.call(UPDATE_USERADDRESS_SERVICE, userAddressReqBO, UserAddressUpdateRspBO.class);

			// (3)构造返回
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("area_code", result.getData().getAreaCode());
			jsonObject.put("is_delivery", null == result.getData().getArea() ? "N" : result.getData().getArea().getIsDelivery());
			jsonObject.put("is_support", null == result.getData().getArea() ? "N" : result.getData().getArea().getIsSupport());
			return new ApiResponse.ApiResponseBuilder().code(UPDATE_SUCCESS_CODE).message(UPDATE_SUCCESS_MSG).data(jsonObject).build();
		} catch (ServiceException e) {
			if (ServiceError.ADDRESS_ID_ERROR.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(UPDATE_ADDRESSIDISNULL_CODE).message(UPDATE_ADDRESSIDISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.USER_ID_ERROR.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(UPDATE_UIDISNULL_CODE).message(UPDATE_UIDISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.ADDRESSEENAME_ERROR.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(UPDATE_ADDRESSNAMEISNULL_CODE).message(UPDATE_ADDRESSNAMEISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.ADDRESS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(UPDATE_ADDRESSISNULL_CODE).message(UPDATE_ADDRESSISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.PROVINCE_MUST.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(UPDATE_AREACODEISNULL_CODE).message(UPDATE_AREACODEISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.MOBILE_PHONE_ONE.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(UPDATE_PHONEANDMOBILEISNULL_CODE).message(UPDATE_PHONEANDMOBILEISNULL_MSG).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}
	}

	/**
	 * 添加地址
	 * 
	 * @param address
	 * @return
	 */
	@RequestMapping(params = "method=app.address.add")
	@ResponseBody
	public ApiResponse add(UserAddressReqVO userAddressReqVO) {
		logger.debug("Begin call AddressController.add gateway. userAddressReqVO is {}", userAddressReqVO);

		if (StringUtils.isNotEmpty(userAddressReqVO.getMobile()) && userAddressReqVO.getMobile().length() > 20) {
			return new ApiResponse.ApiResponseBuilder().code(500).message("mobile too long.").build();
		}

		// (1)构造服务层参数
		UserAddressReqBO userAddressReqBO = new UserAddressReqBO();
		BeanUtils.copyProperties(userAddressReqVO, userAddressReqBO);
		userAddressReqBO.setAddressee_name(userAddressReqVO.getConsignee());

		// (2)调用服务层接口获取数据
		try {
			UserAddressUpdateRspBO result = serviceCaller.call(ADD_USERADDRESS_SERVICE, userAddressReqBO, UserAddressUpdateRspBO.class);

			// (3)拼装区域信息
			AreaRspBo areaRspBo = result.getData().getArea();
			String area = null == areaRspBo ? "" : areaRspBo.getCaption();
			if (null != areaRspBo) {
				AreaRspBo parentAreaRspBo = areaRspBo.getParent();
				area = null == parentAreaRspBo ? area : parentAreaRspBo.getCaption() + " " + area;

				if (null != parentAreaRspBo) {
					AreaRspBo parentParentAreaRspBo = parentAreaRspBo.getParent();
					area = null == parentParentAreaRspBo ? area : parentParentAreaRspBo.getCaption() + " " + area;
				}
			}

			// (4)构造返回
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("id", result.getData().getId());
			jsonObject.put("address_id", result.getData().getId());
			jsonObject.put("is_delivery", null == result.getData().getArea() ? "N" : result.getData().getArea().getIsSupport());
			jsonObject.put("is_support", null == result.getData().getArea() ? "N" : result.getData().getArea().getIsDelivery());
			jsonObject.put("uid", result.getData().getUid());
			jsonObject.put("consignee", result.getData().getAddresseeName());
			jsonObject.put("address", result.getData().getAddress());
			jsonObject.put("area_code", result.getData().getAreaCode());
			jsonObject.put("phone", MobileHelper.coverMobile(result.getData().getPhone()));
			jsonObject.put("mobile", MobileHelper.coverMobile(result.getData().getMobile()));
			jsonObject.put("email", result.getData().getEmail());
			jsonObject.put("zip_code", result.getData().getZipCode());
			jsonObject.put("area", area);
			return new ApiResponse.ApiResponseBuilder().code(ADD_SUCCESS_CODE).message(ADD_SUCCESS_MSG).data(jsonObject).build();
		} catch (ServiceException e) {
			if (ServiceError.USER_ID_ERROR.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(ADD_UIDISNULL_CODE).message(ADD_UIDISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.ADDRESSEENAME_ERROR.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(ADD_ADDRESSNAMEISNULL_CODE).message(ADD_ADDRESSNAMEISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.ADDRESS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(ADD_ADDRESSISNULL_CODE).message(ADD_ADDRESSISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.PROVINCE_MUST.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(ADD_AREACODEISNULL_CODE).message(ADD_AREACODEISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.MOBILE_PHONE_ONE.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(ADD_PHONEANDMOBILEISNULL_CODE).message(ADD_PHONEANDMOBILEISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.ADD_FALSE.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(ADD_FAILED_CODE).message(ADD_FAILED_MSG).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}
	}

	/**
	 * 删除地址
	 * 
	 * @param id
	 * @param uid
	 * @return
	 */
	@RequestMapping(params = "method=app.address.del")
	@ResponseBody
	public ApiResponse del(UserAddressReqVO userAddressReqVO) {
		logger.debug("Begin call AddressController.del gateway. id is {}, uid is {}", userAddressReqVO.getId(), userAddressReqVO.getUid());

		// (1)构造服务层参数
		UserAddressReqBO userAddressReqBO = new UserAddressReqBO();
		BeanUtils.copyProperties(userAddressReqVO, userAddressReqBO);

		// (2)调用服务层接口获取数据
		try {
			serviceCaller.call(DEL_USERADDRESS_SERVICE, userAddressReqBO, CommonRspBO.class);

			// (3)返回
			return new ApiResponse.ApiResponseBuilder().code(DEL_SUCCESS_CODE).message(DEL_SUCCESS_MSG).data(new JSONObject()).build();
		} catch (ServiceException e) {
			if (ServiceError.ID_UID_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(DEL_IDORUIDISNULL_CODE).message(DEL_IDORUIDISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.DEL_FALSE.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(DEL_FAILED_CODE).message(DEL_FAILED_MSG).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}
	}

	/**
	 * 根据id和uid设置默认地址
	 * 
	 * @param id
	 * @param uid
	 * @return
	 */
	@RequestMapping(params = "method=app.address.setdefault")
	@ResponseBody
	public ApiResponse setDefault(UserAddressReqVO userAddressReqVO) {
		logger.debug("Begin call AddressController.setDefault gateway. id is {}, uid is {}", userAddressReqVO.getId(), userAddressReqVO.getUid());

		// (1)构造服务层参数
		UserAddressReqBO userAddressReqBO = new UserAddressReqBO();
		BeanUtils.copyProperties(userAddressReqVO, userAddressReqBO);

		// (2)调用服务层接口获取数据
		try {
			serviceCaller.call(SETDEFAULT_USERADDRESS_SERVICE, userAddressReqBO, CommonRspBO.class);

			// (3)返回
			return new ApiResponse.ApiResponseBuilder().code(SETDEFAULT_SUCCESS_CODE).message(SETDEFAULT_SUCCESS_MSG).data(new JSONObject()).build();
		} catch (ServiceException e) {
			if (ServiceError.UID_IS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(SETDEFAULT_UIDISNULL_CODE).message(SETDEFAULT_UIDISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.ID_IS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(SETDEFAULT_IDISNULL_CODE).message(SETDEFAULT_IDISNULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.SET_FALSE.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(SETDEFAULT_FAILED_CODE).message(SETDEFAULT_FAILED_MSG).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}
	}

}
