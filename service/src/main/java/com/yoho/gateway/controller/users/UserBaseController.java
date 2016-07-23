package com.yoho.gateway.controller.users;

import com.yoho.service.model.response.CommonRspBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.UserBaseVO;
import com.yoho.gateway.model.response.HeadModifyRspVO;
import com.yoho.gateway.service.UploadService;
import com.yoho.service.model.request.ModifyHeadReqBO;
import com.yoho.service.model.request.UserBaseReqBO;
import com.yoho.service.model.response.ReturnInfoModel;

@Controller
public class UserBaseController {

	@Autowired
	private UploadService uploadService;

	@Autowired
	private ServiceCaller serviceCaller;

	// 网关接口返回成功，成功码
	private final static int SUCCESS_CODE = 200;
	// 头像修改成功返回的成功信息
	private final static String MODIFY_HEAD_SUCCESS = "头像修改成功";

	// UID不能为空
	private final static int MODIFY_HEAD_UID_NULL_CODE = 400;
	private final static String MODIFY_HEAD_UID_NULL_MSG = "Uid Is Null.";

	// 上传对象不能为空
	private final static int UPLOAD_OBJECT_IS_NULL_CODE = 400;
	private final static String UPLOAD_OBJECT_IS_NULL_MSG = "上传对象不能为空.";

	// 上传文件失败
	private final static int UPLOAD_FAILED_CODE = 500;
	private final static String UPLOAD_FAILED_MSG = "上传文件失败.";

	// 修改基本信息uid为空的code和message
	private final static int MODIFY_BASE_UID_NULL_CODE = 500;
	private final static String MODIFY_BASE_UID_NULL_MSG = "Uid Is Null.";

	// 修改基本信息nick_name为空的code和message
	private final static int MODIFY_BASE_NULLNICKNAME_CODE = 500;
	private final static String MODIFY_BASE_NULLNICKNAME_MSG = "Nick_name Is Null.";

	// 修改基本信息失败的code和message
	private final static int MODIFY_BASE_FAILED_CODE = 500;
	private final static String MODIFY_BASE_FAILED_MSG = "基本资料修改失败!";

	// 修改基本信息成功的code和message
	private final static int MODIFY_BASE_SUCCESS_CODE = 200;
	private final static String MODIFY_BASE_SUCCESS_MSG = "基本资料修改成功!";

	// 默认的图片模式
	private final static int DEFAULT_IMAGE_MODE = 1;

	// 修改头像服务地址
	private final static String MODIFY_HEAD_URL = "users.modifyUserBaseHead";

	// 修改基本信息服务
	private final static String MODIFY_BASE_SERVICE = "users.modifyUserBase";

	private Logger logger = LoggerFactory.getLogger(UserBaseController.class);

	@RequestMapping(params = "method=app.passport.modifyHead")
	@ResponseBody
	public ApiResponse modifyHead(@RequestParam("file_data") MultipartFile file_data, 
			@RequestParam("uid") int uid, @RequestParam("bucket") String bucket, 
			@RequestParam(value = "head_ico", required = false) String head_ico) throws Exception {
		logger.info("Begin call modifyHead gateway. userId is {} and bucket is {}, head_icon is {}", uid, bucket, head_ico);
		ApiResponse responseBean = null;
		// (1)校验参数，校验uid和上传文件不能为空
		if (uid < 1) {
			logger.warn("Param uid is null. Param bucket={}, head_icon={}", bucket, head_ico);
			throw new GatewayException(MODIFY_HEAD_UID_NULL_CODE, MODIFY_HEAD_UID_NULL_MSG);
		}
		
		//(2)校验上传文件是否为空
		if (null == file_data) {
			logger.warn("upload file is null. Param uid={}, bucket={}, head_icon={}", uid, bucket, head_ico);
			throw new GatewayException(UPLOAD_OBJECT_IS_NULL_CODE, UPLOAD_OBJECT_IS_NULL_MSG);
		}

		// (2)上传图片
		String imagePath = uploadService.upload(file_data, bucket, String.valueOf(uid));
		logger.info("modifyHead. imagePath is {}, uid is {}", imagePath, uid);
		if (null == imagePath) {
			logger.warn("upload file failed. Param uid={}, bucket={}, head_icon={}", uid, bucket, head_ico);
			responseBean = new ApiResponse(UPLOAD_FAILED_CODE, UPLOAD_FAILED_MSG, null);
			return responseBean;
		}

		// (3)调用服务接口,修改用户头像
		// 组装请求参数
		ModifyHeadReqBO modifyHeadReqBO = new ModifyHeadReqBO(uid, imagePath);
		serviceCaller.call(MODIFY_HEAD_URL, modifyHeadReqBO, ReturnInfoModel.class);
		
		// (4)组装头像的绝对路径
		String imageUrl = ImagesHelper.template(imagePath, bucket, DEFAULT_IMAGE_MODE);

		// (5)组装返回参数
		HeadModifyRspVO rspVo = new HeadModifyRspVO(bucket, imagePath, imageUrl);
		responseBean = new ApiResponse.ApiResponseBuilder().data(rspVo).code(SUCCESS_CODE).message(MODIFY_HEAD_SUCCESS).build();
		return responseBean;
	}

	@RequestMapping(params = "method=app.passport.modifyBase")
	@ResponseBody
	public ApiResponse modifyUserBase(UserBaseVO userBaseVO) {
		logger.debug("Begin call modifyUserBase gateway. uid is {}, nick_name is {}, birthday is {}, gender is {}", userBaseVO.getUid(), userBaseVO.getNick_name(), userBaseVO.getBirthday(), userBaseVO.getGender());

		// (1)调用服务接口,修改用户基本信息
		UserBaseReqBO baseBO = new UserBaseReqBO();
		BeanUtils.copyProperties(userBaseVO, baseBO);
		
		try {
			serviceCaller.call(MODIFY_BASE_SERVICE, baseBO, CommonRspBO.class);
			return new ApiResponse.ApiResponseBuilder().code(MODIFY_BASE_SUCCESS_CODE).message(MODIFY_BASE_SUCCESS_MSG).data(new JSONObject()).build();
		} catch (ServiceException e) {
			if (ServiceError.UID_IS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(MODIFY_BASE_UID_NULL_CODE).message(MODIFY_BASE_UID_NULL_MSG).data(new JSONObject()).build();
			} else if (ServiceError.NICK_NAME_IS_NULL.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(MODIFY_BASE_NULLNICKNAME_CODE).message(MODIFY_BASE_NULLNICKNAME_MSG).data(new JSONObject()).build();
			} else if (ServiceError.CODE_FAILED.getCode() == e.getCode()) {
				return new ApiResponse.ApiResponseBuilder().code(MODIFY_BASE_FAILED_CODE).message(MODIFY_BASE_FAILED_MSG).data(new JSONObject()).build();
			} else {
				throw e;
			}
		}

	}

}
