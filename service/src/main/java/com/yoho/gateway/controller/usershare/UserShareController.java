package com.yoho.gateway.controller.usershare;

import com.alibaba.fastjson.JSON;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.GatewayError;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.request.ShareReqVO;
import com.yoho.gateway.model.response.ShareRspVO;
import com.yoho.gateway.model.response.ThirdUserRspVO;
import com.yoho.gateway.service.ThirdShareService;
import com.yoho.service.model.profile.UserShareBO;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import weibo4j.model.Status;

import java.util.ArrayList;

/**
 * gateway 第三方社交平台接口
 *
 * @author lijian
 * @Time 2015/11/13
 */
@Controller
public class UserShareController {
    private Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    private ServiceCaller serviceCall;

    @Autowired
    private ThirdShareService thirdShareService;

    /**
     * 1、 客户端在分享之前调用接口查询是否过期以及数据库是否已经存储用户信息，否则需要客户登陆授权
     * 2、如果客户是第一次分享，这个时候要保存用户的授权信息
     * 3、自动调用新浪分享接口进行分享
     * contentType 1 文本 2 图文
     * shareType 1新浪 2 微信
     */
    //判断用户是否注册 没有则返回
    @RequestMapping(params = "method=social.register")
    @ResponseBody
    public ApiResponse shareInfoCheck(@RequestParam("parameters") String parameters) {
        ShareReqVO shareReqVO = JSON.parseObject(parameters, ShareReqVO.class);
        //获取第三方用户信息 并作是否失效判断
        ApiResponse apiResponse = new ApiResponse();
        JSONObject shareInfo = JSONObject.fromObject(shareReqVO.getAuthInfo());
        ThirdUserRspVO thirdUserInput = (ThirdUserRspVO) JSONObject.toBean(shareInfo, ThirdUserRspVO.class);

        if (shareReqVO == null || thirdUserInput == null) {
            throw new ServiceException(ServiceError.REQUEST_PAREMENT_ERROR);
        }

        //校验是否已经授权并且没有过期
        UserShareBO[] shareUserArray = null;
        try {
            UserShareBO shareUserVO = new UserShareBO();
            shareUserVO.setUid(shareReqVO.getUid());
            shareUserVO.setChannel(shareReqVO.getAuthType());
            shareUserArray = serviceCall.call("users.selectShareByUid", shareUserVO, UserShareBO[].class);
        } catch (ServiceException e) {
            logger.error("users.selectShareByUid exception ", e);
            throw new ServiceException(ServiceError.OTHER_ERROR);
        }

        if (shareUserArray != null && shareUserArray.length > 0) {
            int cnt = 0;
            for (UserShareBO shareReq : shareUserArray) {
                if (shareReq != null && shareReq.getChannel().intValue() == shareReqVO.getAuthType().intValue()) {
                    JSONObject jsonobject = JSONObject.fromObject(shareReq.getShareReqObj());
                    ThirdUserRspVO thirdUserOrg = (ThirdUserRspVO) JSONObject.toBean(jsonobject, ThirdUserRspVO.class);
                    //token不一样 更新数据
                    if (thirdUserOrg != null && thirdUserInput.getAccess_token() != null &&
                            thirdUserOrg.getAccess_token() != null && !thirdUserInput.getAccess_token().equals(thirdUserOrg.getAccess_token())) {
                        //封装参数
                        UserShareBO shareUserVO = new UserShareBO();
                        shareUserVO = thirdShareService.setUserInfo(shareReqVO);
                        Object result = null;
                        try {
                            logger.info(shareUserVO.toString());
                            result = serviceCall.call("users.updateShareMessage", shareUserVO, Object.class);
                        } catch (ServiceException e) {
                            logger.error("users.updateShareMessage exception ", e);
                            throw new ServiceException(ServiceError.OTHER_ERROR);
                        }
                        if (result == null) {
                            ShareRspVO shareRspVO = new ShareRspVO();
                            BeanUtils.copyProperties(shareReqVO, shareRspVO);
                            apiResponse = new ApiResponse(GatewayError.CODE_SUCCESS.getCode(), GatewayError.CODE_SUCCESS.getMessage(), shareReqVO);
                        } else {
                            throw new ServiceException(ServiceError.OTHER_ERROR);
                        }

                    } else if (thirdUserOrg != null && thirdUserInput.getAccess_token() != null &&
                            thirdUserOrg.getAccess_token() != null && thirdUserInput.getAccess_token().equals(thirdUserOrg.getAccess_token())) {
                        //token一样 判断比对是否是一致
                        try {
                            ShareRspVO shareRspVO = new ShareRspVO();
                            BeanUtils.copyProperties(shareReqVO, shareRspVO);
                            shareRspVO.setStatus(thirdShareService.isValidToken(thirdUserOrg, shareReq));
                            if (shareRspVO.getStatus() == 0) {
                                apiResponse = new ApiResponse(GatewayError.OTHER_ERROR.getCode(), GatewayError.OTHER_ERROR.getMessage(), shareRspVO);
                            } else {
                                apiResponse = new ApiResponse(GatewayError.CODE_SUCCESS.getCode(), GatewayError.CODE_SUCCESS.getMessage(), shareRspVO);
                            }
                            return apiResponse;

                        } catch (ServiceException e) {
                            logger.error("share exception ", e);
                            throw new ServiceException(ServiceError.OTHER_ERROR);
                        }
                    }
                } else {
                    cnt++;//判断是否都没有，没有则新增
                }
                if (shareUserArray != null && shareUserArray.length == cnt) {
                    //用户授权信息不一样，插入
                    UserShareBO shareUserVO = null;
                    shareUserVO = thirdShareService.setUserInfo(shareReqVO);
                    Object result = null;
                    try {
                        result = serviceCall.call("users.shareMessage", shareUserVO, Object.class);
                    } catch (ServiceException e) {
                        logger.error("users.shareMessage exception ", e);
                        throw new ServiceException(ServiceError.OTHER_ERROR);
                    }
                    if (result == null) {
                        ShareRspVO shareRspVO = new ShareRspVO();
                        BeanUtils.copyProperties(shareReqVO, shareRspVO);
                        apiResponse = new ApiResponse(GatewayError.CODE_SUCCESS.getCode(), GatewayError.CODE_SUCCESS.getMessage(), shareRspVO);

                    } else {
                        throw new ServiceException(ServiceError.OTHER_ERROR);
                    }

                }

            }
        } else {
            //没有用户授权信息 直接插入
            UserShareBO shareUserVO = new UserShareBO();
            shareUserVO = thirdShareService.setUserInfo(shareReqVO);
            Object result = null;
            try {
                result = serviceCall.call("users.shareMessage", shareUserVO, Object.class);
            } catch (ServiceException e) {
                logger.error("users.shareMessage exception ", e);
                throw new ServiceException(ServiceError.OTHER_ERROR);
            }
            if (result == null) {
                ShareRspVO shareRspVO = new ShareRspVO();
                BeanUtils.copyProperties(shareReqVO, shareRspVO);
                apiResponse = new ApiResponse(GatewayError.CODE_SUCCESS.getCode(), GatewayError.CODE_SUCCESS.getMessage(), shareRspVO);
            } else {
                throw new ServiceException(ServiceError.OTHER_ERROR);
            }

        }

        return apiResponse;

    }

    /*
    *
    * 用户信息分享发布
    * 暂时未用
    *
    * */
    @RequestMapping(params = "method=social.share")
    @ResponseBody
    public ApiResponse shareInfo(@ModelAttribute ShareReqVO shareReqVO) {
        ThirdUserRspVO thirdUser = null;
        ApiResponse apiResponse = new ApiResponse();
        Status status = null;
        try {
            if (shareReqVO != null) {
                status = thirdShareService.submitMsg(shareReqVO, thirdUser);
            }
            if (status != null && status.getText() != null) {
                apiResponse.setData(status);
                apiResponse.setCode(GatewayError.CODE_SUCCESS.getCode());
                apiResponse.setMessage(GatewayError.CODE_SUCCESS.getMessage());
            } else {
                apiResponse.setCode(ServiceError.OTHER_ERROR.getCode());
                apiResponse.setMessage(ServiceError.OTHER_ERROR.getMessage());
            }

        } catch (Exception e) {
            logger.error("shareQueryInfo exception", e);
        }

        return apiResponse;
    }


    /*
*
* 用户授权信息查询
*
* */

    @RequestMapping(params = "method=social.grant.query")
    @ResponseBody
    public ApiResponse shareQueryInfo(@RequestParam(name = "uid") Integer uid) {

        //    查询数据库中用户的授权信息，如果有就返回true 如果是微信的同时把微信的分享信息返回

        ApiResponse apiResponse = new ApiResponse();
        ArrayList<ShareRspVO> shareOutList = new ArrayList<ShareRspVO>();
        UserShareBO[] shareUserArray = null;

        try {
            UserShareBO userShareBO = new UserShareBO();
            userShareBO.setUid(uid);
            shareUserArray = serviceCall.call("users.selectShareByUid", userShareBO, UserShareBO[].class);
        } catch (ServiceException e) {
            logger.error("users.shareMessage call exception ", e);
            throw new ServiceException(ServiceError.OTHER_ERROR);
        }

        if (shareUserArray == null) {
            throw new ServiceException(ServiceError.OTHER_ERROR);
        }
        try {

            for (UserShareBO userShareBO : shareUserArray) {
                JSONObject jsonobject = JSONObject.fromObject(userShareBO.getShareReqObj());
                ThirdUserRspVO thirdUserOrg = (ThirdUserRspVO) JSONObject.toBean(jsonobject, ThirdUserRspVO.class);

                ShareRspVO shareVo = new ShareRspVO();
                shareVo.setUid(userShareBO.getUid());
                shareVo.setAuthType(userShareBO.getChannel());
                shareVo.setAuthInfo(userShareBO.getShareReqObj());
                shareVo.setStatus(thirdShareService.isValidToken(thirdUserOrg, userShareBO));

                shareOutList.add(shareVo);
            }
            apiResponse = new ApiResponse(GatewayError.CODE_SUCCESS.getCode(), GatewayError.CODE_SUCCESS.getMessage(), shareOutList);

        } catch (ServiceException e) {
            logger.error("shareQueryInfo exception", e);
            throw new ServiceException(ServiceError.OTHER_ERROR);
        }

        return apiResponse;
    }



}


