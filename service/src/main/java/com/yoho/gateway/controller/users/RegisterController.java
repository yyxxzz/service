package com.yoho.gateway.controller.users;

import com.netflix.config.DynamicPropertyFactory;
import com.yoho.core.common.utils.AES;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.gateway.utils.IPUtil;
import com.yoho.gateway.utils.PhoneUtil;
import com.yoho.gateway.utils.constants.Constants;
import com.yoho.service.model.request.AuthReqBO;
import com.yoho.service.model.request.RegisterReqBO;
import com.yoho.service.model.response.RegisterRspBO;
import com.yoho.service.model.sms.request.SMSTemplateReqBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class RegisterController {

    @Resource
    private ServiceCaller service;

    @Resource(name = "yhRedisTemplate")
    private YHRedisTemplate<String, String> yhRedisTemplate;

    @Resource(name = "yhValueOperations")
    private YHValueOperations<String, String> yhValueOperations;

    // PROFILE不能为空
    private final static int PROFILE_NOT_NULL_CODE = 421;
    private final static String PROFILE_NOT_NULL_MSG = "账号不能为空.";

    //Profile不合法
    private final static int PROFILE_INCORRECT_CODE = 10001;
    private final static String PROFILE_INCORRECT_MSG = "手机号码格式不匹配";


    // 密码不能为空
    private final static int PASSWORD_NOT_NULL_CODE = 422;
    private final static String PASSWORD_NOT_NULL_MSG = "密码不能为空.";

    // 注册成功
    private final static int REGISTER_SUCCESS_CODE = 200;
    private final static String REGISTER_SUCCESS_MSG = "注册成功.";

    // 注册失败
    private final static int REGISTER_FAILED_CODE = 200;
    private final static String REGISTER_FAILED_MSG = "注册失败.";

    //---------------------发送短信验证码－－－－－－－－－－－－－－－－－－
    //校验手机号码是否为空
    private final static int MOBILE_NULL_CODE = 401;
    private final static String MOBILE_NULL_MSG = "手机号码错误";

    //校验手机号码是否为空
    private final static int MOBILE_VERIFY_FAILED_CODE = 402;
    private final static String MOBILE_VERIFY_FAILED_MSG = "手机号码格式错误";

    //用户已经存在
    private final static int MOBILE_USER_EXIST_CODE = 404;
    private final static String MOBILE_USER_EXIST_MSG = "此账户已存在.";

    //发送失败
    private final static int SMS_SEND_FAILED_CODE = 201;
    private final static String SMS_SEND_FAILED_MSG = "发送失败.";

    //发送成功
    private final static int SMS_SEND_SUCCESS_CODE = 200;
    private final static String SMS_SEND_SUCCESS_MSG = "发送成功.";

    //------------校验验证码-------------------------------
    //手机号码为空异常
    private final static int VALID_REG_MOBILE_ERROR_CODE = 401;
    private final static String VALID_REG_MOBILE_ERROR_MSG = "手机号码错误";
    //手机号码格式错误异常
    private final static int VALID_REG_MOBILE_INVALID_ERROR_CODE = 402;
    private final static String VALID_REG_MOBILE_INVALID_ERROR_MSG = "手机号码错误";
    //验证码为空异常
    private final static int VALID_REG_CODE_NULL_CODE = 401;
    private final static String VALID_REG_CODE_NULL_MSG = "请输入验证码！";
    //验证码输入次数太多
    private final static int VALID_REG_CODE_TIMEOUT_CODE = 405;
    private final static String VALID_REG_CODE_TIMEOUT_MSG = "输入次数太多，请重新发送！";
    //验证错误
    private final static int VALID_REG_CODE_VALID_ERROR_CODE = 404;
    private final static String VALID_REG_CODE_VALID_ERROR_MSG = "验证错误！";
    //验证成功
    private final static int VALID_REG_CODE_VALID_SUCCESS_CODE = 200;
    private final static String VALID_REG_CODE_VALID_SUCCESS_MSG = "验证码正确！";
    //密码不合法
    private final static int VALID_PWD_VALID_ERROR_CODE = 422;
    private final static String VALID_PWD_VALID_ERROR_MSG = "密码不合法";

    //注册地址url
    private final static String REGISTER_URL = "users.register";

    //发送短信接口
    private final static String SMS_SEND_REGISTER_CODE_URL = "message.smsSend";

    //个人信息服务
    private final static String GET_PROFILE_BY_MOBILE_URL = "users.checkUserExist";

    //APP端的注册模版
    private final static String SMS_TEMPLATE_REGISTER = "register";

    //WEB端的注册模版
    private final static String SMS_WEB_TEMPLATE_REGISTER = "register-web";

    //WAP端的注册模版
    private final static String SMS_WAP_TEMPLATE_REGISTER = "register-wap";

    //H5端的客户端类型 wap
    private final static String CLIENT_TYPE_WAP = "wap";

    //WEB端的客户端类型 web
    private final static String CLIENT_TYPE_WEB = "web";

    private Logger logger = LoggerFactory.getLogger(RegisterController.class);

    /**
     * 用户注册
     *
     * @param profile      手机号码
     * @param password     密码
     * @param area         国际码
     * @param client_type  客户端类型，android
     * @param isCoupons    是否送优惠券
     * @param shopping_key 购物车key
     * @return ApiResponse
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.registerAES")
    @ResponseBody
    public ApiResponse registerAES(@RequestParam("profile") String profile,
                                   @RequestParam("password") String password,
                                   @RequestParam(value = "area", required = false) String area,
                                   @RequestParam("client_type") String client_type,
                                   @RequestParam(value = "isCoupons", required = false, defaultValue = "N") String isCoupons,
                                   @RequestParam(value = "shopping_key", required = false) String shopping_key,
                                   @RequestHeader(value = "User-Agent", required = false) String userAgent,
                                   HttpServletResponse httpServletResponse) throws GatewayException {
        logger.info("registerAES: password is {}", password);
        if (StringUtils.isBlank(password)) {
            return register(profile, password, area, client_type, isCoupons, shopping_key, userAgent, httpServletResponse);
        }
        try {
            password = AES.decrypt(DynamicPropertyFactory.getInstance().getStringProperty("password.aes.key", "yoho9646yoho9646").get(), password);
        } catch (Exception e) {
            logger.warn("registerAES: decrypt exception. error message is {}", e.getMessage());
        }
        return register(profile, password, area, client_type, isCoupons, shopping_key, userAgent, httpServletResponse);
    }

    /**
     * 用户注册
     *
     * @param profile      手机号码
     * @param password     密码
     * @param area         国际码
     * @param client_type  客户端类型，android
     * @param isCoupons    是否送优惠券
     * @param shopping_key 购物车key
     * @return ApiResponse
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.register")
    @ResponseBody
    public ApiResponse register(@RequestParam("profile") String profile,
                                @RequestParam("password") String password,
                                @RequestParam(value = "area", required = false) String area,
                                @RequestParam("client_type") String client_type,
                                @RequestParam(value = "isCoupons", required = false, defaultValue = "N") String isCoupons,
                                @RequestParam(value = "shopping_key", required = false) String shopping_key,
                                @RequestHeader(value = "User-Agent", required = false) String userAgent,
                                HttpServletResponse httpServletResponse) throws GatewayException {
        logger.info("Begin call register. profile is {}, area is {}, client_type is {}, isCoupons is {}, shoping_key is {}", profile, area, client_type, isCoupons, shopping_key);
        ApiResponse apiResponse = null;
        // (1)判断用户uid是否为空
        if (null == profile || profile.isEmpty()) {
            logger.warn("profile is null. profile is {}, area is {}, client_type is {}, isCoupons is {}, shoping_key is {}", profile, area, client_type, isCoupons, shopping_key);
            throw new GatewayException(PROFILE_NOT_NULL_CODE, PROFILE_NOT_NULL_MSG);
        }

        //(2)判断国家码是为空，如果为空，赋值空字符串
        area = (null == area) ? "" : area;
        logger.debug("Area value is {}", area);

        //(3)校验手机号码是否合法
        if (!PhoneUtil.areaMobileVerify(area, profile)) {
            logger.warn("Profile is incorrect. Param profile is {}, area is {}, client_type is {}, isCoupons is {}, shoping_key is {}", profile, area, client_type, isCoupons, shopping_key);
            throw new GatewayException(PROFILE_INCORRECT_CODE, PROFILE_INCORRECT_MSG);
        }

     // (4)校验密码是否为空, 以及密码是否合法, 如果密码不合法, 提示密码必须为数字和字母
     		if(null == password || password.isEmpty() || !com.yoho.gateway.utils.StringUtils.registerValidatePassword(password)){
     			logger.warn("password is null or password is invalid. password is {}", password);
     			throw new GatewayException(VALID_PWD_VALID_ERROR_CODE, VALID_PWD_VALID_ERROR_MSG);
     		}

        //(5)获取ip
        logger.debug("Param IP string type is {}", RemoteIPInterceptor.getRemoteIP());
        long ip = IPUtil.ip2Long(RemoteIPInterceptor.getRemoteIP());
        logger.debug("Param ip int is {}", ip);

        //(6)组装请求参数
        RegisterReqBO registerReqBO = new RegisterReqBO(profile, password, area, client_type, isCoupons, shopping_key, ip, userAgent);

        //(5)发送注册请求
        logger.info("Begin send register request.profile is {}, area is {}, client_type is {}, isCoupons is {}, shoping_key is {}", profile, area, client_type, isCoupons, shopping_key);
        RegisterRspBO model = service.call(REGISTER_URL, registerReqBO, RegisterRspBO.class);
        logger.debug("Send register request end.");

        //注册成功之后, 设置cookie
        Cookie cookie = new Cookie("JSESSIONID", model.getSession_key());
        cookie.setMaxAge(24 * 60 * 60); //24小时
        httpServletResponse.addCookie(cookie);

        logger.info("Register success.");
        apiResponse = new ApiResponse.ApiResponseBuilder().data(model).code(REGISTER_SUCCESS_CODE).message(REGISTER_SUCCESS_MSG).build();
        return apiResponse;
    }

    /**
     * 注册发送短信验证码
     *
     * @param area        国家码
     * @param mobile      手机号码
     * @param client_type 客户端类型
     * @return ApiResponse
     * @throws Exception
     */
    @RequestMapping(params = "method=app.register.sendRegCodeToMobile")
    @ResponseBody
    public ApiResponse sendRegCodeToMobile(@RequestParam(value = "area") String area,
                                           @RequestParam(value = "mobile") String mobile,
                                           @RequestParam(value = "client_type", required = false, defaultValue = "") String client_type,
                                           @RequestHeader(value = "User-Agent", required = false) String userAgent) throws Exception {
        logger.info("Begin call app.register.sendRegCodeToMobile gateway. Param area={}, mobile={}, client_type={},userAgent={}", area, mobile, client_type, userAgent);
        ApiResponse apiResponse = null;
        //(1)判断手机号码是否为空
        if (null == mobile || mobile.isEmpty()) {
            logger.warn("Method sendRegCodeToMobile gateway. mobile is null. area={}, mobile={}, client_type={}", area, mobile, client_type);
            throw new GatewayException(MOBILE_NULL_CODE, MOBILE_NULL_MSG);
        }

        //(2)校验区号，以及根据区号，校验手机号码是否是否满足格式,
        area = (null == area) ? "" : area;
        if (!PhoneUtil.areaMobileVerify(area, mobile)) {
            logger.warn("Method sendRegCodeToMobile gateway. mobile is incorrect. area={}, mobile={}, client_type={}", area, mobile, client_type);
            throw new GatewayException(MOBILE_VERIFY_FAILED_CODE, MOBILE_VERIFY_FAILED_MSG);
        }

        //(3)根据手机号码，获取用户基本信息,判断用户是否已经存在
        AuthReqBO authReqBO = new AuthReqBO(area, mobile);
        boolean result = service.call(GET_PROFILE_BY_MOBILE_URL, authReqBO, Boolean.class);
        logger.info("sendRegCodeToMobile with mobile={}, and check user result={}", mobile, result);
//		result = false;
        //(4)判断用户基础信息是否存在，如果存在则不能注册，则不会发送注册短信
        if (result) {
            logger.info("User already exist. send register sms failed. area={}, mobile={}, client_type={}", area, mobile, client_type);
            throw new GatewayException(MOBILE_USER_EXIST_CODE, MOBILE_USER_EXIST_MSG);
        }

        //(5) 构建手机号码，如果是国际号码，手机号码区号＋号码
        mobile = PhoneUtil.makePhone(area, mobile);
        logger.debug("sendRegCodeToMobile: make mobile is {}", mobile);

        //(6)获取用户ip, 发送给短信的ip是没有转成int类型的
        String ip = RemoteIPInterceptor.getRemoteIP();
        logger.debug("Param IP is {}", ip);

        //(7)随机生成四位验证码，并将注册验证码存入redis中, 保存时间10分钟
        String verifyCode = PhoneUtil.getPhoneVerifyCode();
        logger.info("setter register code: key:{}, verifyCode:{}", Constants.REGISTER_CODE_MEM_KEY + mobile, verifyCode);
        try {
            yhValueOperations.set(Constants.REGISTER_CODE_MEM_KEY + mobile, verifyCode);//key=yh:users:register_mobile_
            yhRedisTemplate.longExpire(Constants.REGISTER_CODE_MEM_KEY + mobile, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.warn("Redis exception. send register code. ip is {}, mobile is {}, area is {}, errorMsg is {}", ip, mobile, area, e.getMessage());
        }

        //(8)发送短信验证码，根据客户端类型选择短信模版, 如果WEB客户端:register-web, H5客户端是register-wap, APP客户端register
        String regTemplate = SMS_TEMPLATE_REGISTER;
        if (CLIENT_TYPE_WAP.equals(client_type)) { //wap客户端
            regTemplate = SMS_WAP_TEMPLATE_REGISTER;
        } else if (CLIENT_TYPE_WEB.equals(client_type)) {//WEB客户端
            regTemplate = SMS_WEB_TEMPLATE_REGISTER;
        }

        SMSTemplateReqBO smsRequestBO = new SMSTemplateReqBO(regTemplate, mobile, ip, verifyCode, area);
        List<SMSTemplateReqBO> smsTemplateBOList = new ArrayList<SMSTemplateReqBO>();
        smsTemplateBOList.add(smsRequestBO);
        service.call(SMS_SEND_REGISTER_CODE_URL, smsTemplateBOList, String.class);

        //(9)短信发送成功，删除验证码校验次数的缓存, key=yh:users:register_time_
        try {
            yhRedisTemplate.delete(Constants.REGISTER_TIME_MEM_KEY + mobile); //key=yh:users:register_time_
        } catch (Exception e) {
            logger.warn("Redis exception. delete send time record. ip is {}, mobile is {}, area is {}, errorMsg is {}", ip, mobile, area, e.getMessage());
        }

        //（10）组装返回信息
        apiResponse = new ApiResponse.ApiResponseBuilder().code(SMS_SEND_SUCCESS_CODE).message(SMS_SEND_SUCCESS_MSG).build();
        return apiResponse;
    }

    @RequestMapping(params = "method=app.register.validRegCode")
    @ResponseBody
    public ApiResponse validRegCode(@RequestParam(value = "code") String code,
                                    @RequestParam(value = "area", required = false) String area,
                                    @RequestParam(value = "mobile") String mobile,
                                    @RequestParam(value = "client_type", required = false) String client_type) throws Exception {
        logger.debug("Begin call validRegCode controller. Param code is {}, mobile is {}, area is {} and client_type is {}", code, mobile, area, client_type);
        ApiResponse apiResponse = null;
        //(1)判断国家码是否为空，如果国家码为空，则设定国家码为空字符串
        area = (null == area || area.isEmpty()) ? "" : area;

        //(2)校验手机号码格式是否正确
        if (!PhoneUtil.areaMobileVerify(area, mobile)) {
            logger.info("Mobile is invalid. Param mobile is {}, code is {}, area is {}", mobile, code, area);
            throw new GatewayException(VALID_REG_MOBILE_INVALID_ERROR_CODE, VALID_REG_MOBILE_INVALID_ERROR_MSG);
        }

        //(3)如果是国际号码组装号码格式为 1-22232323223
        mobile = PhoneUtil.makePhone(area, mobile);

        //(4)从cache中获取当前手机号码的已经校验验证码的次数，5分钟之内，如果做了15次判断，则直接抛出异常，不给注册
        try {
            long times = yhValueOperations.increment(Constants.REGISTER_TIME_MEM_KEY + mobile, 1);
            yhRedisTemplate.longExpire(Constants.REGISTER_TIME_MEM_KEY + mobile, 5, TimeUnit.MINUTES);
            if (times > 14) {//当前是第15次登录
                logger.info("mobile {} validate more than 15 times in 5 minuter. param area is {}, code is {} ", area, code);
                throw new GatewayException(VALID_REG_CODE_TIMEOUT_CODE, VALID_REG_CODE_TIMEOUT_MSG);
            }
        } catch (GatewayException e) {
            throw e; //当前号码5分钟之内做了15次判断
        } catch (Exception e) {
            logger.warn("Redis exception. check register times. area is {}, mobile is {}, exception is {}", apiResponse, mobile, e.getMessage());
        }

        //(5)校验当前输入的验证码和cache中的验证码比对，是否是正确，
        try {
            String validCode = yhValueOperations.get(Constants.REGISTER_CODE_MEM_KEY + mobile);
            logger.info("app.register.validRegCode. area is {}, mobile is {}, redis validCode is {}, request code is {}", area, mobile, validCode, code);
            if (null == validCode || !code.equals(validCode)) {
                logger.warn("valid code is incorrect. Param mobile is {}, area is {}, request code is {} validRegCode is {} key:{}", mobile, area, code, validCode, Constants.REGISTER_CODE_MEM_KEY + mobile);
                throw new GatewayException(VALID_REG_CODE_VALID_ERROR_CODE, VALID_REG_CODE_VALID_ERROR_MSG);
            }
        } catch (GatewayException e) {
            throw e; //号码的验证码错误
        } catch (Exception e) {
            logger.warn("Redis exception. check register code. area is {}, mobile is {}, exception is {}", apiResponse, mobile, e.getMessage());
        }

        //(7)验证正确
        apiResponse = new ApiResponse.ApiResponseBuilder().code(VALID_REG_CODE_VALID_SUCCESS_CODE).message(VALID_REG_CODE_VALID_SUCCESS_MSG).build();
        return apiResponse;
    }


}
