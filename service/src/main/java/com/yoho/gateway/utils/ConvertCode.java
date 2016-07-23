package com.yoho.gateway.utils;

import java.util.HashMap;
import java.util.Map;

import com.yoho.error.GatewayError;
import com.yoho.error.ServiceError;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.response.ReturnInfoModel;

public class ConvertCode {

	public static Map<ServiceError, GatewayError> errorMap = new HashMap<ServiceError, GatewayError>();
	
	static {
//		errorMap.put(ServiceError.CODE_SUCCESS, GatewayError.CODE_SUCCESS);
//		errorMap.put(ServiceError.PLEASE_INPUT_PASSWORD, GatewayError.PLEASE_INPUT_PASSWORD);
//		errorMap.put(ServiceError.PASSWORD_NOT_RULE, GatewayError.PASSWORD_NOT_RULE);
////		errorMap.put(ServiceError.PASSWORD_IS_NULL, GatewayError.PASSWORD_IS_NULL);
//		errorMap.put(ServiceError.UPDATE_MOBILE_ERROR, GatewayError.UPDATE_MOBILE_ERROR);
//		errorMap.put(ServiceError.UPDATE_PASSWORD_ERROR, GatewayError.UPDATE_PASSWORD_ERROR);
//		errorMap.put(ServiceError.REGISTER_ERROR, GatewayError.REGISTER_ERROR);
//		
//		
//		errorMap.put(ServiceError.SOURCE_TYPE_IS_NULL, GatewayError.SOURCE_TYPE_IS_NULL);
//		errorMap.put(ServiceError.OTHER_ERROR, GatewayError.OTHER_ERROR);
//		errorMap.put(ServiceError.LOGIN_ERROR, GatewayError.LOGIN_ERROR);
//		
//		errorMap.put(ServiceError.PROFILE_IS_NULL, GatewayError.PROFILE_IS_NULL);
//		errorMap.put(ServiceError.PASSWORD_IS_NULL, GatewayError.PASSWORD_IS_NULL);
//		errorMap.put(ServiceError.PROFILE_MUST_BE_MOBILE_OR_EMAIL, GatewayError.PROFILE_MUST_BE_MOBILE_OR_EMAIL);
//		errorMap.put(ServiceError.PASSWORD_ERROR, GatewayError.PASSWORD_ERROR);
//		errorMap.put(ServiceError.USER_NOT_EXISTS, GatewayError.USER_NOT_EXISTS);
//		
//		//address code
//		errorMap.put(ServiceError.BIND_SUCCESS, GatewayError.BIND_SUCCESS);
//		errorMap.put(ServiceError.ADDRESS_LIST, GatewayError.ADDRESS_LIST);
//		errorMap.put(ServiceError.ADDRESS_ID_ERROR, GatewayError.ADDRESS_ID_ERROR);
//		errorMap.put(ServiceError.USER_ID_ERROR, GatewayError.USER_ID_ERROR);
//		errorMap.put(ServiceError.ADDRESSEENAME_ERROR, GatewayError.ADDRESSEENAME_ERROR);
//		errorMap.put(ServiceError.ADDRESS_NULL, GatewayError.ADDRESS_NULL);
//		errorMap.put(ServiceError.PROVINCE_MUST, GatewayError.PROVINCE_MUST);
//		errorMap.put(ServiceError.MOBILE_PHONE_ONE, GatewayError.MOBILE_PHONE_ONE);
//		errorMap.put(ServiceError.ADD_SUCCESS, GatewayError.ADD_SUCCESS);
//		errorMap.put(ServiceError.ADD_FALSE, GatewayError.ADD_FALSE);
//		errorMap.put(ServiceError.ID_UID_NULL, GatewayError.ID_UID_NULL);
//		errorMap.put(ServiceError.DEL_SUCCESS, GatewayError.DEL_SUCCESS);
//		errorMap.put(ServiceError.DEL_FALSE, GatewayError.DEL_FALSE);
//		errorMap.put(ServiceError.PROVINCE_LIST, GatewayError.PROVINCE_LIST);
//		errorMap.put(ServiceError.ID_IS_NULL, GatewayError.ID_IS_NULL);
//		errorMap.put(ServiceError.SET_SUCCESS, GatewayError.SET_SUCCESS);
//		errorMap.put(ServiceError.SET_FALSE, GatewayError.SET_FALSE);
		
	}
	
	/**
	 * 返回码转换
	 * @param result
	 * @param responseBean
	 */
	public static void convertUserErrorCode(ReturnInfoModel result, ApiResponse responseBean) {
		ServiceError serror = ServiceError.getServiceErrorByCode(result.getErrorCode());
		if (serror.getCode() == ServiceError.OTHER_ERROR.getCode()) {
			responseBean.setCode(GatewayError.OTHER_ERROR.getCode());
	        responseBean.setMessage(result.getMessage());
	        return;
		}
    	GatewayError gerror = ConvertCode.errorMap.get(serror);
        responseBean.setCode(gerror.getCode());
        responseBean.setMessage(gerror.getMessage());
	}
}
