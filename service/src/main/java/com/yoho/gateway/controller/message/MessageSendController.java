package com.yoho.gateway.controller.message;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.gateway.utils.PhoneUtil;
import com.yoho.service.model.sms.request.SMSTemplateReqBO;

/**
 * 发送消息统一接口
 *
 * Created by xinfei on 16/3/16.
 */
@Controller
public class MessageSendController {

    static Logger logger = LoggerFactory.getLogger(MessageSendController.class);

    //---------------------发送短信验证码－－－－－－－－－－－－－－－－－－
    //校验手机号码是否为空
    private final static int MOBILE_NULL_CODE = 401;
    private final static String MOBILE_NULL_MSG = "手机号码错误";

    //校验手机号码格式是否正确
    private final static int MOBILE_VERIFY_FAILED_CODE = 402;
    private final static String MOBILE_VERIFY_FAILED_MSG = "手机号码格式错误";

    //校验短信模版是否正确
    private final static int TEMPLATE_VERIFY_FAILED_CODE = 403;
    private final static String TEMPLATE_VERIFY_FAILED_MSG = "模板格式错误";

    //发送成功
    private final static int SMS_SEND_SUCCESS_CODE= 200;
    private final static String SMS_SEND_SUCCESS_MSG = "发送成功.";

    //发送短信接口
    private final static String SMS_SEND_REGISTER_CODE_URL = "message.smsSend";


    @Autowired
    private ServiceCaller service;

    @RequestMapping(params = "method=app.message.sendMsg")
    @ResponseBody
    public ApiResponse sendRegCodeToMobile(@RequestParam(value = "area",required = false) String area,
                                           @RequestParam(value = "mobile") String mobile,
                                           @RequestParam(value = "client_type", required = false, defaultValue = "") String client_type,
                                           @RequestParam(value = "codes")String codes,
                                           @RequestParam(value = "template")String template) throws Exception {
        logger.debug("Begin call app.message.sendMsg gateway. Param area={}, mobile={}, client_type={},code={},template={}", area, mobile, client_type, codes);
        ApiResponse apiResponse = null;
        //(1)判断手机号码是否为空
        if(null == mobile || mobile.isEmpty()){
            logger.warn("Method app.message.sendMsg gateway. mobile is null. area={}, mobile={}, client_type={}", area, mobile, client_type);
            throw new GatewayException(MOBILE_NULL_CODE, MOBILE_NULL_MSG);
        }

        if(null == template || template.isEmpty()){
            logger.warn("Method app.message.sendMsg gateway. template is null. mobile is null. area={}, mobile={}, client_type={}", area, mobile, client_type);
            throw new GatewayException(TEMPLATE_VERIFY_FAILED_CODE, TEMPLATE_VERIFY_FAILED_MSG);
        }

        //(2)校验国家码，以及根据区号，校验手机号码是否是否满足格式,
        area = (null == area)? "" : area;
        if(!PhoneUtil.areaMobileVerify(area, mobile)){
            logger.warn("Method app.message.sendMsg gateway. mobile is incorrect. area={}, mobile={}, client_type={}", area, mobile, client_type);
            throw new GatewayException(MOBILE_VERIFY_FAILED_CODE, MOBILE_VERIFY_FAILED_MSG);
        }

        //(5) 构建手机号码，如果是国际号码，手机号码区号＋号码
        mobile = PhoneUtil.makePhone(area, mobile);
        logger.debug("sendRegCodeToMobile: make mobile is {}", mobile);

        //(6)获取用户ip, 发送给短信的ip是没有转成int类型的
        String ip = RemoteIPInterceptor.getRemoteIP();
        logger.debug("Param IP is {}", ip);


        SMSTemplateReqBO smsRequestBO = new SMSTemplateReqBO(template, mobile, ip, codes, area);
        List<SMSTemplateReqBO> smsTemplateBOList = new ArrayList<SMSTemplateReqBO>();
        smsTemplateBOList.add(smsRequestBO);
        service.call(SMS_SEND_REGISTER_CODE_URL, smsTemplateBOList, String.class);

        //（10）组装返回信息
        apiResponse = new ApiResponse.ApiResponseBuilder().code(SMS_SEND_SUCCESS_CODE).message(SMS_SEND_SUCCESS_MSG).build();
        return apiResponse;
    }

}
