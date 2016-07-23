package com.yoho.gateway.controller.users;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.yoho.core.common.utils.AES;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.gateway.model.request.*;
import com.yoho.gateway.utils.IPUtil;
import com.yoho.gateway.utils.PhoneUtil;
import com.yoho.gateway.utils.constants.Constants;
import com.yoho.service.model.consts.Constant;
import com.yoho.service.model.order.response.shopping.ShoppingCartMergeRequestBO;
import com.yoho.service.model.order.response.shopping.ShoppingCartMergeResponseBO;
import com.yoho.service.model.request.*;
import com.yoho.service.model.response.*;
import com.yoho.service.model.sms.request.SMSTemplateReqBO;
import com.yoho.service.model.sms.response.SMSRspModel;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
public class PassportController {

    @Resource(name = "yhRedisTemplate")
    private YHRedisTemplate<String, String> yhRedisTemplate;

    @Resource(name = "yhValueOperations")
    private YHValueOperations<String, String> yhValueOperations;

    @Resource
    ServiceCaller serviceCaller;

    private Logger logger = LoggerFactory.getLogger(PassportController.class);
    private Logger ipLoginMoreLog = LoggerFactory.getLogger("ipLoginMoreLog");

    private Logger loginLowClientRecord = LoggerFactory.getLogger("loginLowClientRecord");

    private static final String SEND_SUCCESS_MESSAGE = "发送成功";
    private static final String LOGIN_SUCCESS_MESSAGE = "登录成功";
    private static final int LOGIN_ERROR_CODE = 500;
    private static final String LOGIN_ERROR_MESSAGE = "登录失败";
    private static final String CHANGE_CHECK_SUCCESS_MESSAGE = "手机号验证通过";
    private static final String CHECK_SUCCESS_MESSAGE = "可以绑定";
    private static final String SMS_TEMPLATE_BIND = "appbind";
    private static final String SMS_TEMPLATE_LOW_VERSION_NOTICE = "updateVersion";

    private static final int MOBILE_IS_NULL_CODE = 401;
    private static final String MOBILE_IS_NULL_MESSAGE = "手机号码错误";
    private static final int MOBILE_IS_ERROR_CODE = 402;
    private static final String MOBILE_IS_ERROR_MESSAGE = "手机号码格式错误";
    private static final String CHANGE_BIND_SUCCESS_MSG = "手机号修改成功！";
    private static final String RELATED_MOBILE_SUCCESS_MSG = "手机号关联成功！";

    // 发送短信接口
    private final static String SMS_SEND_REGISTER_CODE_URL = "message.smsSend";

    private final static String IP_TIMES = "IP_TIMES_";

    private final static String LOGIN_FAILED_TIMES = "LOGIN_FAILED_TIMES_";


    /**
     * 第三方登录
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.signinByOpenID")
    @ResponseBody
    public ApiResponse signinByOpenID(SigninByOpenIDVO vo, HttpServletResponse httpServletResponse) throws GatewayException {
        logger.debug("enter signinByOpenID");
        logger.info("Begin call PassportController.signinByOpenID gateway. with param is {}", vo);
        SigninByOpenIDReqBO bo = new SigninByOpenIDReqBO();
        BeanUtils.copyProperties(vo, bo);
        bo.setIpStr(RemoteIPInterceptor.getRemoteIP());
        bo.setIp(IPUtil.ip2Long(bo.getIpStr()));
        SigninByOpenIDRespBO result = serviceCaller.call("users.signinByOpenID", bo, SigninByOpenIDRespBO.class);
        logger.info("call users.signinByOpenID with param is {}, with result is {}", vo, result);

        ApiResponse response = null;
        if (result == null) {
            logger.warn("call users.signinByOpenID with param is {} result is null", bo);
            response = new ApiResponse.ApiResponseBuilder().code(LOGIN_ERROR_CODE).message(LOGIN_ERROR_MESSAGE).build();
        } else {
            response = new ApiResponse.ApiResponseBuilder().message(LOGIN_SUCCESS_MESSAGE).data(result).build();
        }
        // 合并购物车，捕获异常，不能影响正常登陆
        try {
            ShoppingCartMergeRequestBO mergeRequest = new ShoppingCartMergeRequestBO();
            mergeRequest.setUid(result.getUid());
            mergeRequest.setShopping_key(vo.getShopping_key());
            ShoppingCartMergeResponseBO shop = serviceCaller.call("order.mergeShoppingCart", mergeRequest, ShoppingCartMergeResponseBO.class);
            logger.debug("mergeShoppingCart result is {}", shop);
        } catch (Exception e) {
            logger.error("mergeShoppingCart error", e);
        }

        //第三方登录成功后, 设置cookie, 如果第三方首次登录, 那么不会生成SESSION_KEY
        if (null != result && !StringUtils.isEmpty(result.getSession_key())) {
            Cookie cookie = new Cookie("JSESSIONID", result.getSession_key());
            cookie.setMaxAge(24 * 60 * 60); //24小时
            httpServletResponse.addCookie(cookie);
        }
        return response;
    }

    /**
     * PC端微信账号登录
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.signinByWechat")
    @ResponseBody
    public ApiResponse signinByWechat(SigninByOpenIDVO vo, HttpServletResponse httpServletResponse) throws GatewayException {
        logger.debug("enter signinByWechat");
        logger.info("Begin call PassportController.signinByWechat gateway. with param is {}", vo);
        SigninByOpenIDReqBO bo = new SigninByOpenIDReqBO();
        BeanUtils.copyProperties(vo, bo);
        bo.setIpStr(RemoteIPInterceptor.getRemoteIP());
        bo.setIp(IPUtil.ip2Long(bo.getIpStr()));
        SigninByOpenIDRespBO result = serviceCaller.call("users.signinByWechat", bo, SigninByOpenIDRespBO.class);
        logger.info("call users.signinByWechat with param is {}, with result is {}", vo, result);

        ApiResponse response = null;
        if (result == null) {
            logger.warn("call users.signinByWechat with param is {} result is null", bo);
            response = new ApiResponse.ApiResponseBuilder().code(LOGIN_ERROR_CODE).message(LOGIN_ERROR_MESSAGE).build();
        } else {
            response = new ApiResponse.ApiResponseBuilder().message(LOGIN_SUCCESS_MESSAGE).data(result).build();
        }
        // 合并购物车，捕获异常，不能影响正常登陆
        try {
            ShoppingCartMergeRequestBO mergeRequest = new ShoppingCartMergeRequestBO();
            mergeRequest.setUid(result.getUid());
            mergeRequest.setShopping_key(vo.getShopping_key());
            ShoppingCartMergeResponseBO shop = serviceCaller.call("order.mergeShoppingCart", mergeRequest, ShoppingCartMergeResponseBO.class);
            logger.debug("mergeShoppingCart result is {}", shop);
        } catch (Exception e) {
            logger.error("mergeShoppingCart error", e);
        }

        //第三方登录成功后, 设置cookie, 如果第三方首次登录, 那么不会生成SESSION_KEY
        if (null != result && !StringUtils.isEmpty(result.getSession_key())) {
            Cookie cookie = new Cookie("JSESSIONID", result.getSession_key());
            cookie.setMaxAge(24 * 60 * 60); //24小时
            httpServletResponse.addCookie(cookie);
        }
        return response;
    }

    /**
     * 合并购物车
     *
     * @param uid
     * @param shopping_key
     */
    private void mergeShoppingCart(int uid, String shopping_key) {
        logger.info("begin mergeCart with uid={}, Shopping_key={}", uid, shopping_key);
        if (uid <= 0) {
            logger.warn("mergeCart error with uid={}, shopping_key={}", uid, shopping_key);
            return;
        }
        if (StringUtils.isEmpty(shopping_key)) {
            logger.warn("mergeCart error with uid={}, shopping_key={}", uid, shopping_key);
            return;
        }
        try {
            ShoppingCartMergeRequestBO mergeRequest = new ShoppingCartMergeRequestBO();
            mergeRequest.setUid(uid);
            mergeRequest.setShopping_key(shopping_key);
            ShoppingCartMergeResponseBO shop = serviceCaller.call("order.mergeShoppingCart", mergeRequest, ShoppingCartMergeResponseBO.class);
            logger.info("mergeShoppingCart result is {}", shop);
        } catch (Exception e) {
            logger.error("mergeShoppingCart error", e);
        }
        logger.info("end mergeCart with uid={}, Shopping_key={}", uid, shopping_key);
    }


    /**
     * 绑定手机号码
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.bind")
    @ResponseBody
    public ApiResponse bind(BindVO vo, HttpServletResponse httpServletResponse, @RequestHeader(value = "User-Agent", required = false) String userAgent) throws GatewayException {
        logger.debug("enter bind");
        logger.info("Begin call PassportController.bind gateway. with mobile={}, open_id={}, email={}, source_type={}", vo.getMobile(), vo.getOpen_id(), vo.getEmail(), vo.getSource_type());
        BindMobileReqBO bo = new BindMobileReqBO();
        bo.setUserAgent(userAgent);
        BeanUtils.copyProperties(vo, bo);
        bo.setIpStr(RemoteIPInterceptor.getRemoteIP());
        bo.setIp(IPUtil.ip2Long(bo.getIpStr()));
        BindMobileRespBO result = serviceCaller.call("users.bind", bo, BindMobileRespBO.class);
        logger.info("call users.bind with mobile={}, open_id={}, email={}, source_type={}, with result is {}", vo.getMobile(), vo.getOpen_id(), vo.getEmail(), vo.getSource_type(), result);


        //合并购物车
        mergeShoppingCart(result.getUid(), vo.getShopping_key());


        ApiResponse response = new ApiResponse.ApiResponseBuilder().message(Constant.DEFAULT_SUCCESS_MESSAGE).data(result).build();

        //第三方首次登录, 需要绑定手机号码, 会生成COOKIE, 重新设置COOKIE
        Cookie cookie = new Cookie("JSESSIONID", result.getSession_key());
        cookie.setMaxAge(24 * 60 * 60); //24小时
        httpServletResponse.addCookie(cookie);
        return response;
    }

    /**
     * 登录
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.signinAES")
    @ResponseBody
    public ApiResponse signinAES(SigninVO vo, HttpServletResponse httpServletResponse, HttpServletRequest request) throws GatewayException {
        logger.info("signinAES: param is {}", vo);
        if (null == vo) {
            return signin(vo, httpServletResponse, request);
        }
        String password = vo.getPassword();
        if (StringUtils.isBlank(password)) {
            return signin(vo, httpServletResponse, request);
        }
        try {
            password = AES.decrypt(DynamicPropertyFactory.getInstance().getStringProperty("password.aes.key", "yoho9646yoho9646").get(), password);
        } catch (Exception e) {
            logger.warn("signinAES: decrypt exception. error message is {}", e.getMessage());
        }
        vo.setPassword(password);
        return signin(vo, httpServletResponse, request);
    }

    /**
     * 登录
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.signin")
    @ResponseBody
    public ApiResponse signin(SigninVO vo, HttpServletResponse httpServletResponse, HttpServletRequest request) throws GatewayException {
        //(1)判断请求参数是否为空
        if (null == vo) {
            logger.warn("signin: request param is null");
            throw new GatewayException(500, "request param is null");
        }
        logger.info("Begin call PassportController.signin gateway. with area={}, profile={}, client_type={}", vo.getArea(), vo.getProfile(), vo.getClient_type());

        //(2)获取透传的IP, 并且获取当前的IP的登录次数,并且登录次数+1, 如果1分钟之内,超过设定的登录次数, 不允许登录
        String ip = RemoteIPInterceptor.getRemoteIP();
        String clientType = vo.getClient_type();
        //(3)登录判断用户的版本,如果版本低于3.8.1,记录用户的记录
        String appVersion = request.getParameter("app_version");
        try {
            logger.debug("signin controller. appVersion is {}", appVersion);
            if (null != clientType && !"web".equals(clientType) && !"h5".equals(clientType) && !StringUtils.isEmpty(appVersion) && appVersion.compareTo("4.0.0") < 0) {
                loginLowClientRecord.info("Low version. client_type is {}, app_version is {}, area is {}, mobile is {}", vo.getClient_type(), appVersion, vo.getArea(), vo.getProfile());
                //发送短信验证码
                //对于vo.getProfile增加校验，判断是否是数字
                if (PhoneUtil.isMobile(vo.getProfile())) {
                    SMSTemplateReqBO smsRequestBO = new SMSTemplateReqBO(SMS_TEMPLATE_LOW_VERSION_NOTICE, vo.getProfile(), ip, "", vo.getArea());
                    serviceCaller.asyncCall("message.noticeSMSSend", smsRequestBO, SigninRespBO.class);
                }
            }
        } catch (Exception e) {
            logger.warn("Check client type version failed. client_type is {}, app_version is {}, area is {}, mobile is {}, client_type is {}, error msg is {}", vo.getClient_type(), appVersion, vo.getArea(), vo.getProfile(), vo.getClient_type(), e.getMessage());
        }

        //(4)校验同一个IP的登录次数, 如果同一个IP登录次数过多,则不允许登录
        try {
            long ipLimitTimes = yhValueOperations.increment(Constants.LOGIN_IP_LIMIT_TIMES + ip, 1);
            yhRedisTemplate.longExpire(Constants.LOGIN_IP_LIMIT_TIMES + ip, 1, TimeUnit.MINUTES);
            logger.info("signin: ip is {}, times is {}, mobile is {}, area is {}", ip, ipLimitTimes, vo.getProfile(), vo.getArea());

            DynamicIntProperty loginIpLimit = DynamicPropertyFactory.getInstance().getIntProperty("login.ip.limit.times", -1);
            if (null != loginIpLimit && -1 != loginIpLimit.get() && ipLimitTimes > loginIpLimit.get()) {
                ipLoginMoreLog.error("signin error: current ip login times more than 20 times in 1 minuter. ip is {}, profile is {}, client_type is {}", ip, vo.getProfile(), vo.getClient_type());
                throw new GatewayException(500, "当前IP请求次数太多");
            }
        } catch (GatewayException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("redis exception. signin limit ip login times. ip is {}, error msg is {}", ip, e.getMessage());
        }

        //(5)获取同一个号码登录失败的次数,如果10分钟登录失败10次, 不允许登录
        try {
            String loginFailedStr = yhValueOperations.get(Constants.LOGIN_FAILED_TIMES + vo.getProfile());//key=yh:users:loginFailed:
            long loginFailedTimes = (StringUtils.isEmpty(loginFailedStr) ? 0 : Long.valueOf(loginFailedStr));
            DynamicIntProperty loginFailedLimit = DynamicPropertyFactory.getInstance().getIntProperty("login.loginfailed.limit.time", -1);//获取设置的登录次数
            logger.debug("sign in. loginFailed times is {}, mobile is {}, area is {}, loginFailedLimit is {}", loginFailedStr, vo.getProfile(), vo.getArea(), loginFailedLimit);

            if (null != loginFailedLimit && -1 != loginFailedLimit.get() && loginFailedTimes > loginFailedLimit.get()) {
                logger.error("signin error: current user login failed times more than 10 times in 10 minutes. profile is {}, ip is {}", vo.getProfile(), ip);
                throw new GatewayException(10010, "用户名或密码错误");
            }
        } catch (GatewayException e) {
            throw e; //同一个号码登录失败次数过多
        } catch (Exception e) {
            logger.warn("redis exception. signin limit ip login times. ip is {}, error msg is {}", ip, e.getMessage());
        }

        //(6)设置请求的参数
        SigninBO bo = new SigninBO();
        BeanUtils.copyProperties(vo, bo);
        bo.setIpStr(ip);
        bo.setIp(IPUtil.ip2Long(ip));

        //(5)调用登录接口登录
        SigninRespBO result = null;
        try {
            result = serviceCaller.call("users.login", bo, SigninRespBO.class);
        } catch (ServiceException e) {
            //记录登录失败次数, 失败次数+1
            try {
                yhValueOperations.increment(Constants.LOGIN_FAILED_TIMES + vo.getProfile(), 1); //KEY=yh:users:loginFailed:
                yhRedisTemplate.longExpire(Constants.LOGIN_FAILED_TIMES + vo.getProfile(), 10, TimeUnit.MINUTES);
                logger.warn("signin: login failed.profile is {}, ip is {}, area is {}", vo.getProfile(), ip, vo.getArea());
            } catch (Exception e1) {
                logger.warn("redis exception. login failed and load redis failed. mobile is {}, exception is {}", vo.getProfile(), e.getMessage());
            }
            throw e;
        }
        logger.info("call users.login with profile={}, client_type={}, with result is {}", vo.getProfile(), vo.getClient_type(), result);

        //(7)合并购物车，捕获异常，不能影响正常登陆
        try {
            if (result != null && result.getUid() > 0 && StringUtils.isNotEmpty(vo.getShopping_key())) {
                logger.info("begin mergeCart with uid={}, Shopping_key={}", result.getUid(), vo.getShopping_key());
                ShoppingCartMergeRequestBO mergeRequest = new ShoppingCartMergeRequestBO();
                mergeRequest.setUid(result.getUid());
                mergeRequest.setShopping_key(vo.getShopping_key());
                ShoppingCartMergeResponseBO shop = serviceCaller.call("order.mergeShoppingCart", mergeRequest, ShoppingCartMergeResponseBO.class);
                logger.debug("mergeShoppingCart result is {}", shop);
            }
        } catch (Exception e) {
            logger.error("mergeShoppingCart error", e);
        }
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message(LOGIN_SUCCESS_MESSAGE).data(result).build();

        //(8)登录成功后, 设置cookie
        if (result != null) {
            Cookie cookie = new Cookie("JSESSIONID", result.getSession_key());
            cookie.setMaxAge(24 * 60 * 60); //24小时
            httpServletResponse.addCookie(cookie);
        }
        return response;
    }

    /**
     * 修改绑定手机号前的检查
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.changeCheck")
    @ResponseBody
    public ApiResponse changeCheck(MobileCheckReqVO vo) throws GatewayException {
        logger.info("Begin call changeCheck gateway with param is {}", vo);
        MobileCheckReqBO bo = new MobileCheckReqBO();
        BeanUtils.copyProperties(vo, bo);
        serviceCaller.call("users.changeCheck", bo, CommonRspBO.class);
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message(CHANGE_CHECK_SUCCESS_MESSAGE).build();
        return response;
    }

    // smsbind 绑定时发送的验证短信
    @RequestMapping(params = "method=app.passport.smsbind")
    @ResponseBody
    public ApiResponse smsbind(SmsbindVO vo) throws GatewayException {
        logger.debug("Begin call PassportController.smsbind gateway. with param is  {}", vo);
        //(1)校验手机号码和国家或者地区码, 而且号码要和地区码匹配
        if (StringUtils.isEmpty(vo.getMobile())) {
            return new ApiResponse.ApiResponseBuilder().code(MOBILE_IS_NULL_CODE).message(MOBILE_IS_NULL_MESSAGE).build();
        }
        String area = StringUtils.defaultString(vo.getArea());
        boolean isMobile = PhoneUtil.areaMobileVerify(area, vo.getMobile());
        if (!isMobile) {
            return new ApiResponse.ApiResponseBuilder().code(MOBILE_IS_ERROR_CODE).message(MOBILE_IS_ERROR_MESSAGE).build();
        }

        //(2)根据国家或者地区码组装手机号码, 并生成四位验证码, 将验证码存放在REDIS中.
        String mobile1 = PhoneUtil.makePhone(area, vo.getMobile());
        String ip = RemoteIPInterceptor.getRemoteIP();
        String verifyCode = PhoneUtil.getPhoneVerifyCode();
        logger.info("smsbind: mobile is {}, ip is {}, verifyCode is {}", mobile1, ip, verifyCode);
        try {
            yhValueOperations.set(Constants.BIND_MOBILE_MEM_KEY + mobile1, verifyCode);
            yhRedisTemplate.longExpire(Constants.BIND_MOBILE_MEM_KEY + mobile1, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.warn("smsbind: redis exception. mobile is {}, area is {}, verifyCode is {}. error msg is {}", mobile1, vo.getArea(), verifyCode, e.getMessage());
        }

        // (3)发送短信验证码，模版是
        SMSTemplateReqBO smsRequestBO = new SMSTemplateReqBO(SMS_TEMPLATE_BIND, mobile1, ip, verifyCode, area);
        List<SMSTemplateReqBO> smsTemplateBOList = new ArrayList<SMSTemplateReqBO>();
        smsTemplateBOList.add(smsRequestBO);
        serviceCaller.call(SMS_SEND_REGISTER_CODE_URL, smsTemplateBOList, SMSRspModel.class);

        //短信发送成功，删除验证码校验次数
        try {
            yhRedisTemplate.delete(Constants.BIND_MOBILE_TIME_MEM_KEY + mobile1);
        } catch (Exception e) {
            logger.warn("smsbind. delete message send times. read redis failed. mobile is {}", mobile1);
        }
        return new ApiResponse.ApiResponseBuilder().message(SEND_SUCCESS_MESSAGE).build();
    }

    // check 第三方绑定检查账号
    @RequestMapping(params = "method=app.passport.check")
    @ResponseBody
    public ApiResponse check(BindVO vo) throws GatewayException {
        logger.debug("Begin call PassportController.check gateway. with mobile={}, open_id={}", vo.getMobile(), vo.getOpen_id());
        BindMobileReqBO bo = new BindMobileReqBO();
        BeanUtils.copyProperties(vo, bo);
        CheckMobileRespBO result = serviceCaller.call("users.check", bo, CheckMobileRespBO.class);
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message(CHECK_SUCCESS_MESSAGE).data(result).build();
        return response;
    }

    // changeMobile 修改绑定的手机号
    @RequestMapping(params = "method=app.passport.changeMobile")
    @ResponseBody
    public ApiResponse changeMobile(ChangeBindVO vo) throws GatewayException {
        logger.debug("Begin call PassportController.changeMobile gateway. with client_type={}, mobile={}, open_id={}, source_type={}", vo.getClient_type(), vo.getMobile(), vo.getOpen_id(), vo.getSource_type());

        String mobile1 = PhoneUtil.makePhone(vo.getArea(), vo.getMobile());
        // 首先检查验证码是否正确
        // (1)从cache中获取当前手机号码的已经校验验证码的次数，5分钟之内，如果做了15次判断，则直接抛出异常
        try {
            long times = yhValueOperations.increment(Constants.BIND_MOBILE_TIME_MEM_KEY + mobile1, 1);
            yhRedisTemplate.longExpire(Constants.BIND_MOBILE_TIME_MEM_KEY + mobile1, 5, TimeUnit.MINUTES);
            if (times > 14) {
                logger.info("mobile {} validate more than 15 times in 5 minuter. param area is {}, code is {} ", vo.getMobile(), vo.getArea(), vo.getCode());
                throw new ServiceException(ServiceError.VALID_CODE_TIMEOUT_CODE);
            }
        } catch (ServiceException e) {
            throw e; //绑定重试次数超过15次
        } catch (Exception e) {
            logger.warn("changeMobile. read redis failed. mobile is {}, errorMsg is {}", mobile1, e.getMessage());
        }

        // (2)校验当前输入的验证码和cache中的验证码比对，是否是正确，
        try {
            String validCode = yhValueOperations.get(Constants.BIND_MOBILE_MEM_KEY + mobile1);
            logger.info("changeMobile, app.passport.changeMobile. mobile is {}, validCode is {}, vo code is {}", mobile1, validCode, vo.getCode());
            if (null == validCode || !vo.getCode().equals(validCode)) {
                logger.info("valid code is incorrect. Param mobile is {}, area is {}, code is {}", vo.getMobile(), vo.getArea(), vo.getCode());
                throw new ServiceException(ServiceError.VALID_ERROR);
            }
        } catch (ServiceException e) {
            throw e; //修改绑定时校验短信验证码
        } catch (Exception e) {
            logger.warn("changeMobile redis exception. mobile is {}, error msg is {}", mobile1, e.getMessage());
        }

        //(3)组装返回
        ChangeBindMobileReqBO bo = new ChangeBindMobileReqBO();
        BeanUtils.copyProperties(vo, bo);
        bo.setIp(IPUtil.ip2Long(RemoteIPInterceptor.getRemoteIP()));
        ChangeBindMobileRespBO result = serviceCaller.call("users.changeMobile", bo, ChangeBindMobileRespBO.class);
        return new ApiResponse.ApiResponseBuilder().message(CHANGE_BIND_SUCCESS_MSG).data(result).build();
    }

    //解决第三方登录时，需要绑定的手机已经注册问题，增加手机关联
    @RequestMapping(params = "method=app.passport.relatedMobile")
    @ResponseBody
    public ApiResponse relatedMobile(RelatedMobileVO vo, HttpServletResponse httpServletResponse) throws GatewayException {
        logger.debug("Begin call PassportController.relatedMobile gateway.param is{}", vo);

        String mobile = vo.getMobile();
        String area = vo.getArea();
        String openId = vo.getOpenId();
        String source_type = vo.getSource_type();

        //请求校验
        if (StringUtils.isEmpty(mobile)) {
            logger.warn("relatedMobile error reason MOBILE_IS_NULL with param is {}", vo);
            throw new ServiceException(ServiceError.MOBILE_IS_NULL);
        }
        if (!PhoneUtil.areaMobileVerify(area, mobile)) {
            logger.warn("bindMobileCheck error reason MOBILE_IS_ERROR with param is {}", vo);
            throw new ServiceException(ServiceError.MOBILE_IS_ERROR);
        }
        if (StringUtils.isEmpty(openId)) {
            logger.warn("relatedMobile error reason OPEN_ID_IS_NULL with param is {}", vo);
            throw new ServiceException(ServiceError.OPEN_ID_IS_NULL);
        }
        if (StringUtils.isEmpty(source_type)) {
            logger.warn("signinByOpenID error because SOURCE_TYPE_IS_NULL with param is {}", vo);
            throw new ServiceException(ServiceError.SOURCE_TYPE_IS_NULL);
        }

        RelatedMobileReqBO bo = new RelatedMobileReqBO();
        bo.setArea(area);
        bo.setMobile(mobile);
        bo.setOpenId(openId);
        bo.setSource_type(source_type);
        //bo.setIpStr(RemoteIPInterceptor.getRemoteIP());
        //bo.setIp(IPUtil.ip2Long(bo.getIpStr()));
        RelatedMobileRspBO result = serviceCaller.call("users.relatedMobile", bo, RelatedMobileRspBO.class);

        //合并购物车
        mergeShoppingCart(result.getUid(), vo.getShopping_key());

        ApiResponse response = new ApiResponse.ApiResponseBuilder().message(RELATED_MOBILE_SUCCESS_MSG).data(result).build();
        //绑定成功之后，APP自动跳转到个人中心，接口会校验Session
        //Cookie cookie = new Cookie("JSESSIONID",result.getSession_key());
        //cookie.setMaxAge(24 * 60 * 60); //24小时
        //httpServletResponse.addCookie(cookie);
        return response;
    }

    /**
     * 第三方登录检查
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.passport.signCheck")
    @ResponseBody
    public ApiResponse signCheck(BindVO vo) throws GatewayException {
        logger.debug("Begin call PassportController.check gateway. with mobile={}, open_id={}", vo.getMobile(), vo.getOpen_id());

        vo.setMobile(StringUtils.defaultString(vo.getMobile()));
        vo.setArea(StringUtils.defaultString(vo.getArea()));
        vo.setOpen_id(StringUtils.defaultString(vo.getOpen_id()));
        vo.setEmail(StringUtils.defaultString(vo.getEmail()));

        if (StringUtils.isEmpty(vo.getMobile())) {
            logger.warn("checkBindParam error because MOBILE_NUMBER_ERROR with mobile={}, open_id={}", vo.getMobile(), vo.getOpen_id());
            throw new ServiceException(ServiceError.MOBILE_NUMBER_ERROR);
        }

        boolean isMobile = PhoneUtil.areaMobileVerify(vo.getArea(), vo.getMobile());
        if (!isMobile) {
            // 402, '手机号码格式错误'
            logger.warn("checkBindParam error because MOBILE_IS_ERROR mobile={}, open_id={}", vo.getMobile(), vo.getOpen_id());
            throw new ServiceException(ServiceError.MOBILE_IS_ERROR);
        }
        // 如果open_id和email都为空，则使用第三方登录
        if (StringUtils.isEmpty(vo.getOpen_id()) && StringUtils.isEmpty(vo.getEmail())) {
            // 400, '请使用第三方登录'
            logger.warn("checkBindParam error because THIRD_LOGIN mobile={}, open_id={}", vo.getMobile(), vo.getOpen_id());
            throw new ServiceException(ServiceError.THIRD_LOGIN);
        }
        String source_type = StringUtils.defaultString(vo.getSource_type());
        if (StringUtils.isEmpty(source_type)) {
            // 400, '请选择登录方式'
            logger.warn("checkBindParam error because LOGIN_METHOD mobile={}, open_id={}", vo.getMobile(), vo.getOpen_id());
            throw new ServiceException(ServiceError.LOGIN_METHOD);
        }
        BindMobileReqBO bo = new BindMobileReqBO();
        BeanUtils.copyProperties(vo, bo);
        CheckMobileRespBO result = serviceCaller.call("users.signCheck", bo, CheckMobileRespBO.class);
        ApiResponse response = new ApiResponse.ApiResponseBuilder().data(result).build();
        return response;
    }

}
