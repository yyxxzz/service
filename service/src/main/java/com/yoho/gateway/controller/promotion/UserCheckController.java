package com.yoho.gateway.controller.promotion;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.gateway.utils.PhoneUtil;
import com.yoho.gateway.utils.constants.Constants;
import com.yoho.service.model.sms.request.SMSTemplateReqBO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Controller
public class UserCheckController {

    @Resource
    private ServiceCaller service;

    @Autowired
    private MemecacheClientHolder cacheClient;

    //---------------------发送短信验证码－－－－－－－－－－－－－－－－－－
    //校验手机号码是否为空
    private final static int phoneNum_NULL_CODE = 401;
    private final static String phoneNum_NULL_MSG = "手机号码错误";

    //用户已经存在
    private final static int phoneNum_USER_EXIST_CODE = 404;
    private final static String phoneNum_USER_EXIST_MSG = "此账户已存在.";

    //发送失败
    private final static int SMS_SEND_FAILED_CODE = 201;
    private final static String SMS_SEND_FAILED_MSG = "发送失败.";

    //发送成功
    private final static int SMS_SEND_SUCCESS_CODE = 200;
    private final static String SMS_SEND_SUCCESS_MSG = "发送成功.";

    //------------校验验证码-------------------------------
    //手机号码为空异常
    private final static int VALID_REG_phoneNum_ERROR_CODE = 401;
    private final static String VALID_REG_phoneNum_ERROR_MSG = "手机号码错误";
    //手机号码格式错误异常
    private final static int VALID_REG_phoneNum_INVALID_ERROR_CODE = 402;
    private final static String VALID_REG_phoneNum_INVALID_ERROR_MSG = "手机号码错误";
    //验证码为空异常
    private final static int VALID_REG_CODE_NULL_CODE = 401;
    private final static String VALID_REG_CODE_NULL_MSG = "请输入验证码！";
    //验证码输入次数太多
    private final static int VALID_REG_CODE_TIMEOUT_CODE = 405;
    private final static String VALID_REG_CODE_TIMEOUT_MSG = "输入次数太多，请重新发送！";
    //验证错误
    private final static int VALID_REG_CODE_VALID_ERROR_CODE = 404;
    private final static String VALID_REG_CODE_VALID_ERROR_MSG = "验证码错误！";
    //验证成功
    private final static int VALID_REG_CODE_VALID_SUCCESS_CODE = 200;
    private final static String VALID_REG_CODE_VALID_SUCCESS_MSG = "校验成功！";

    //发送短信接口
    private final static String SMS_SEND_REGISTER_CODE_URL = "message.smsSend";


    private final static String SMS_TEMPLATE_REGISTER = "birthdayValid";

    private Logger logger = LoggerFactory.getLogger(UserCheckController.class);


    /**
     * 领券发送短信验证码
     *
     * @param area     国家码
     * @param phoneNum 手机号码
     * @return ApiResponse
     * @throws Exception
     */
    @RequestMapping(params = "method=app.promotion.sendCheckCode")
    @ResponseBody
    public ApiResponse sendCheckCode(@RequestParam(value = "area") String area,
                                     @RequestParam(value = "phoneNum") String phoneNum
    ) throws Exception {
        logger.debug("Begin call app.register.sendRegCodeTophoneNum gateway. Param area={}, phoneNum={}", area, phoneNum);
        ApiResponse apiResponse = null;
        //(1)判断手机号码是否为空
        if (null == phoneNum || phoneNum.isEmpty()) {
            throw new GatewayException(phoneNum_NULL_CODE, phoneNum_NULL_MSG);
        }

        //(2)校验区号，以及根据区号，校验手机号码是否是否满足格式,
        area = (null == area) ? "" : area;
        if (!PhoneUtil.areaMobileVerify(area, phoneNum)) {
            throw new GatewayException(VALID_REG_phoneNum_ERROR_CODE, VALID_REG_phoneNum_ERROR_MSG);
        }

        //(3) 构建手机号码，如果是国际号码，手机号码区号＋号码
        phoneNum = PhoneUtil.makePhone(area, phoneNum);


        //(6)获取用户ip, 发送给短信的ip是没有转成int类型的
        String ip = RemoteIPInterceptor.getRemoteIP();
        logger.debug("Param IP is {}", ip);

        //(7)随机生成四位验证码，并将注册验证码存入cache中, 保存时间30分
        //30分钟内重复发送相同的验证码
        String verifyCode = cacheClient.getLevel1Cache().get(Constants.BIRTHDAY_COUPON_CODE_MEM_KEY + phoneNum, String.class);
        if(StringUtils.isBlank(verifyCode)){
             verifyCode = PhoneUtil.getPhoneVerifyCode();
        }
        logger.debug("User verifyCode is {}", verifyCode);
        cacheClient.getLevel1Cache().set(Constants.BIRTHDAY_COUPON_CODE_MEM_KEY + phoneNum, 60 * 30, verifyCode);

        //(8)发送短信验证码，模版是 register
        SMSTemplateReqBO smsRequestBO = new SMSTemplateReqBO(SMS_TEMPLATE_REGISTER, phoneNum, ip, verifyCode, area);
        List<SMSTemplateReqBO> smsTemplateBOList = new ArrayList<SMSTemplateReqBO>();
        smsTemplateBOList.add(smsRequestBO);
        service.call(SMS_SEND_REGISTER_CODE_URL, smsTemplateBOList, String.class);

        //（10）组装返回信息
        apiResponse = new ApiResponse.ApiResponseBuilder().code(SMS_SEND_SUCCESS_CODE).message(SMS_SEND_SUCCESS_MSG).build();
        return apiResponse;
    }

    @RequestMapping(params = "method=app.promotion.validCode")
    @ResponseBody
    public ApiResponse validRegCode(@RequestParam(value = "code") String code,
                                    @RequestParam(value = "area", required = false) String area,
                                    @RequestParam(value = "phoneNum") String phoneNum,
                                    @RequestParam(value = "client_type", required = false) String client_type) throws Exception {
        logger.debug("Begin call validRegCode controller. Param code is {}, phoneNum is {}, area is {} and client_type is {}", code, phoneNum, client_type);
        ApiResponse apiResponse = null;
        //(1)判断国家码是否为空，如果国家码为空，则设定国家码为空字符串
        area = (null == area || area.isEmpty()) ? "" : area;

        //(2)校验手机号码格式是否正确
        phoneNum = PhoneUtil.makePhone(area, phoneNum);
        if (!PhoneUtil.areaMobileVerify(area, phoneNum)) {
            logger.info("phoneNum is invalid. Param phoneNum is {}, code is {}, area is {}", phoneNum, code, area);
            throw new GatewayException(VALID_REG_phoneNum_INVALID_ERROR_CODE, VALID_REG_phoneNum_INVALID_ERROR_MSG);
        }

        //(5)校验当前输入的验证码和cache中的验证码比对，是否是正确，
        String validCode = cacheClient.getLevel1Cache().get(Constants.BIRTHDAY_COUPON_CODE_MEM_KEY + phoneNum, String.class);
        if (null == validCode || (code != null && !code.equals(validCode))) {
            logger.info("valid code is incorrect. Param phoneNum is {}, area is {}, code is {}", phoneNum, area, code);
            throw new GatewayException(VALID_REG_CODE_VALID_ERROR_CODE, VALID_REG_CODE_VALID_ERROR_MSG);
        }

        //(6)验证正确
        apiResponse = new ApiResponse.ApiResponseBuilder().code(VALID_REG_CODE_VALID_SUCCESS_CODE).message(VALID_REG_CODE_VALID_SUCCESS_MSG).build();
        return apiResponse;
    }


}
