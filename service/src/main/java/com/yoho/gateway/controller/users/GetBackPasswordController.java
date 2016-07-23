package com.yoho.gateway.controller.users;

import com.alibaba.fastjson.JSONObject;
import com.netflix.config.DynamicPropertyFactory;
import com.yoho.core.common.utils.AES;
import com.yoho.core.common.utils.MD5;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.gateway.model.request.BackpwdByEmailRequestVO;
import com.yoho.gateway.model.request.ChangeMobileVO;
import com.yoho.gateway.model.request.ChangePwdRequestVO;
import com.yoho.gateway.model.request.MobileCheckReqVO;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.gateway.utils.PhoneUtil;
import com.yoho.gateway.utils.StringUtils;
import com.yoho.gateway.utils.constants.Constants;
import com.yoho.service.model.consts.Constant;
import com.yoho.service.model.request.BackpwdByEmailReqBO;
import com.yoho.service.model.request.ChangePwdRequestBO;
import com.yoho.service.model.request.DESRequestBO;
import com.yoho.service.model.request.ProfileRequestBO;
import com.yoho.service.model.response.CommonRspBO;
import com.yoho.service.model.response.ProfileInfoRsp;
import com.yoho.service.model.sms.request.SMSTemplateReqBO;
import com.yoho.service.model.sms.response.SMSRspModel;
import com.yoho.service.model.users.request.auth.ResetPwdByCodeRequestVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 找回密码
 *
 * @author ping.huang
 */
@Controller
public class GetBackPasswordController {

    private Logger logger = LoggerFactory.getLogger(GetBackPasswordController.class);
    private static final String SMS_TEMPLATE_BACK = "repassword";

    @Resource
    ServiceCaller serviceCaller;

    @Resource(name = "yhRedisTemplate")
    private YHRedisTemplate<String, String> yhRedisTemplate;

    @Resource(name = "yhValueOperations")
    private YHValueOperations<String, String> yhValueOperations;

    /**
     * 发送短信验证码
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.register.sendBackpwdCodeToMobile")
    @ResponseBody
    public ApiResponse sendBackpwdCodeToMobile(MobileCheckReqVO vo) throws GatewayException {
        logger.debug("enter sendBackpwdCodeToMobile");
        logger.info("Begin call sendBackpwdCodeToMobile gateway. with param={}", vo);
        if (StringUtils.isEmpty(vo.getMobile())) {
            logger.warn("sendBackpwdCodeToMobile error reason MOBILE_IS_NULL with param is {}", vo);
            throw new ServiceException(ServiceError.MOBILE_IS_NULL);
        }
        if (!PhoneUtil.areaMobileVerify(vo.getArea(), vo.getMobile())) {
            logger.warn("sendBackpwdCodeToMobile error reason MOBILE_IS_ERROR with param is {}", vo);
            throw new ServiceException(ServiceError.MOBILE_IS_ERROR);
        }
        ProfileRequestBO bo = new ProfileRequestBO();
        BeanUtils.copyProperties(vo, bo);
        bo.setCheckSSO(true);
        ProfileInfoRsp result = serviceCaller.call("users.getUserprofileByEmailOrMobile", bo, ProfileInfoRsp.class);
        if (result == null || result.getUid() == 0) {
            logger.warn("sendBackpwdCodeToMobile error reason USER_NOT_EXISTS with param is {}", vo);
            return new ApiResponse.ApiResponseBuilder().message(Constant.DEFAULT_SUCCESS_MESSAGE).build();
        }
        logger.info("call sendBackpwdCodeToMobile with result uid={}, with param is {}", result.getUid(), vo);

        // 随机生成四位验证码，
        String mobile = PhoneUtil.makePhone(vo.getArea(), vo.getMobile());
        String verifyCode = PhoneUtil.getPhoneVerifyCode();
        try {
            yhValueOperations.set(Constants.BACK_PASSWORD_MEM_KEY + mobile, verifyCode); //yh:users:repassword_mobile_
            yhRedisTemplate.longExpire(Constants.BACK_PASSWORD_MEM_KEY + mobile, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.warn("Redis exception. send sendBackpwdCodeToMobile. area is {}, mobile is {}, verifyCode is {}", vo.getArea(), vo.getMobile(), verifyCode);
        }

        String ip = RemoteIPInterceptor.getRemoteIP();

        // (8)发送短信验证码，模版是
        SMSTemplateReqBO smsRequestBO = new SMSTemplateReqBO(SMS_TEMPLATE_BACK, mobile, ip, verifyCode, vo.getArea());
        List<SMSTemplateReqBO> smsTemplateBOList = new ArrayList<SMSTemplateReqBO>();
        smsTemplateBOList.add(smsRequestBO);
        serviceCaller.call("message.smsSend", smsTemplateBOList, SMSRspModel.class);

        // (9)短信发送成功，删除验证码校验次数
        try {
            yhRedisTemplate.delete(Constants.BACK_PASSWORD_TIME_MEM_KEY + mobile);//KEY=yh:users:repassword_times_
        } catch (Exception e) {
            logger.warn("Redis exception. delete back passport time redis failed. area is {}, key is {}", vo.getArea(), Constants.BACK_PASSWORD_TIME_MEM_KEY + mobile);
        }
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message(Constant.DEFAULT_SUCCESS_MESSAGE).build();
        return response;
    }

    /**
     * 修改密码时的验证校验码
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.register.validBackpwdCode")
    @ResponseBody
    public ApiResponse validBackpwdCode(ChangeMobileVO vo) throws GatewayException {
        logger.info("Begin call validBackpwdCode gateway. with param={}", vo);
        if (StringUtils.isEmpty(vo.getMobile())) {
            logger.warn("sendBackpwdCodeToMobile error reason MOBILE_IS_NULL with param is {}", vo);
            throw new ServiceException(ServiceError.MOBILE_IS_NULL);
        }
        if (!PhoneUtil.areaMobileVerify(vo.getArea(), vo.getMobile())) {
            logger.warn("sendBackpwdCodeToMobile error reason MOBILE_IS_ERROR with param is {}", vo);
            throw new ServiceException(ServiceError.MOBILE_IS_ERROR);
        }

        String mobile1 = PhoneUtil.makePhone(vo.getArea(), vo.getMobile());
        // 首先检查验证码是否正确
        // (1)从cache中获取当前手机号码的已经校验验证码的次数，5分钟之内，如果做了15次判断，则直接抛出异常,防止恶意攻击
        try {
            long times = yhValueOperations.increment(Constants.BACK_PASSWORD_TIME_MEM_KEY + mobile1, 1);
            yhRedisTemplate.longExpire(Constants.BACK_PASSWORD_TIME_MEM_KEY + mobile1, 5, TimeUnit.MINUTES);
            if (times > 14) {
                logger.warn("mobile {} validate more than 15 times in 5 minuter. param area is {}, code is {} ", vo.getArea(), vo.getCode());
                throw new ServiceException(ServiceError.VALID_CODE_TIMEOUT_CODE);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("Redis exception. get value of backpwd times. area is {}, mobile is {}, error msg is {}", vo.getArea(), mobile1, e.getMessage());
        }

        // (2)校验当前输入的验证码和cache中的验证码比对，是否是正确，
        try {
            String validCode = yhValueOperations.get(Constants.BACK_PASSWORD_MEM_KEY + mobile1);
            if (null == validCode || !vo.getCode().equals(validCode)) {
                logger.warn("valid code is incorrect. Param mobile is {}, area is {}, code is {}, validCode in redis is {}", vo.getMobile(), vo.getArea(), vo.getCode(), validCode);
                throw new ServiceException(ServiceError.VERIFY_CODE_ERROR);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("Redis exception. check valid code failed. mobile is {}, area is {}, error msg is {}", mobile1, vo.getArea(), e.getMessage());
        }

        //(3)保存是否校验的表示
        try {
            yhValueOperations.set("back_ok" + mobile1, "1");
            yhRedisTemplate.longExpire("back_ok" + mobile1, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            logger.warn("Redis exception. mobile is {}, error msg is {}", mobile1, e.getMessage());
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("token", MD5.md5(MD5.md5("back_ok" + mobile1)));
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message("验证码正确").data(map).build();
        return response;
    }

    /**
     * 通过手机号码，修改密码
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.register.changepwdByMobileCodeAES")
    @ResponseBody
    public ApiResponse changepwdByMobileCodeAES(ChangePwdRequestVO vo) throws GatewayException {
        logger.info("changepwdByMobileCodeAES: param is {}", vo);
        if (null == vo) {
            return changepwdByMobileCode(vo);
        }
        String password = vo.getNewpwd();
        if (StringUtils.isBlank(password)) {
            return changepwdByMobileCode(vo);
        }
        try {
            password = AES.decrypt(DynamicPropertyFactory.getInstance().getStringProperty("password.aes.key", "yoho9646yoho9646").get(), password);
        } catch (Exception e) {
            logger.warn("changepwdByMobileCodeAES: decrypt exception. error message is {}", e.getMessage());
        }
        vo.setNewpwd(password);
        return changepwdByMobileCode(vo);
    }

    /**
     * 通过手机号码，修改密码
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.register.changepwdByMobileCode")
    @ResponseBody
    public ApiResponse changepwdByMobileCode(ChangePwdRequestVO vo) throws GatewayException {
        logger.debug("enter changepwdByMobileCode");
        logger.info("Begin call changepwdByMobileCode gateway. with mobile={}, area={}, token={}", vo.getMobile(), vo.getArea(), vo.getToken());
        String mobile1 = PhoneUtil.makePhone(vo.getArea(), vo.getMobile());
        //(1)检查用户是否调用过验证接口
        try {
            String is_ok_mobile = yhValueOperations.get("back_ok" + mobile1);
            if (StringUtils.isEmpty(is_ok_mobile)) {
                logger.warn("changepwdByMobileCode error with not check mobile with mobile={}, area={}, token={}", vo.getMobile(), vo.getArea(), vo.getToken());
                throw new ServiceException(ServiceError.NOT_CHECK_MOBILE);
            }
            yhRedisTemplate.delete("back_ok" + mobile1);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("Redis exception. area is {}, mobile is {}, error msg is {}", vo.getArea(), vo.getMobile(), e.getMessage());
        }
        ChangePwdRequestBO bo = new ChangePwdRequestBO();
        BeanUtils.copyProperties(vo, bo);
        serviceCaller.call("users.changepwdByMobileCode", bo, CommonRspBO.class);
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message("修改成功！").build();
        return response;
    }

    /**
     * 通过邮箱，找回密码
     *
     * @param vo
     * @return
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.register.backpwdByEmail")
    @ResponseBody
    public ApiResponse backpwdByEmail(BackpwdByEmailRequestVO vo) throws GatewayException {
        logger.debug("enter backpwdByEmail");
        logger.info("Begin call backpwdByEmail gateway. with param is {}", vo);
        if (StringUtils.isEmpty(vo.getEmail())) {
            logger.warn("backpwdByEmail error because email is null");
            throw new ServiceException(ServiceError.EMAIL_NULL);
        }
        if (!com.yoho.gateway.utils.StringUtils.validateMail(vo.getEmail())) {
            logger.warn("backpwdByEmail error because email is error with email is {}", vo.getEmail());
            throw new ServiceException(ServiceError.EMAIL_ERROR);
        }
        //检查该email的用户是否存在
        ProfileRequestBO bo = new ProfileRequestBO();
        bo.setEmail(vo.getEmail());
        bo.setCheckSSO(true);
        ProfileInfoRsp result = serviceCaller.call("users.getUserprofileByEmailOrMobile", bo, ProfileInfoRsp.class);
        if (result == null || result.getUid() == 0) {
            logger.warn("backpwdByEmail error reason USER_NOT_EXISTS with param is {}", vo);
            return new ApiResponse.ApiResponseBuilder().message("发送成功！").build();
        }
        logger.info("call getUserprofileByEmailOrMobile with result uid={}, with param is {}", result.getUid(), vo);

        //检查该IP，在一天内的用邮箱找回，密码次数
        String ip = RemoteIPInterceptor.getRemoteIP();
        try {
            long times = yhValueOperations.increment(Constants.BACK_PASSWORD_TIME_MEM_KEY_EMAIL + ip, 1);
            yhRedisTemplate.longExpire(Constants.BACK_PASSWORD_TIME_MEM_KEY_EMAIL + ip, 24, TimeUnit.HOURS);
            if (times > 300) {
                logger.warn("ip {} back pass 300 times in one day. param email is {}", ip, vo.getEmail());
                throw new ServiceException(ServiceError.SEND_ERROR);
            }
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            logger.warn("Redis exception. get back passport by email times in a day failed. email is {}, error message is {}", vo.getEmail(), e.getMessage());
        }

        //发送邮件
        BackpwdByEmailReqBO b = new BackpwdByEmailReqBO();
        b.setToEmail(vo.getEmail());
        b.setUid(result.getUid());
        serviceCaller.call("users.backpwdByEmail", b, CommonRspBO.class);
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message("发送成功！").build();
        return response;
    }

    /**
     * 用通过加密的code，重置密码
     *
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.register.resetPwdByCodeAES")
    @ResponseBody
    public ApiResponse resetPwdByCodeAES(ResetPwdByCodeRequestVO vo) throws GatewayException {
        logger.info("resetPwdByCodeAES: param is {}", vo);
        if (null == vo) {
            return resetPwdByCode(vo);
        }
        String password = vo.getNewPwd();
        if (StringUtils.isBlank(password)) {
            return resetPwdByCode(vo);
        }
        try {
            password = AES.decrypt(DynamicPropertyFactory.getInstance().getStringProperty("password.aes.key", "yoho9646yoho9646").get(), password);
        } catch (Exception e) {
            logger.warn("resetPwdByCodeAES: decrypt exception. error message is {}", e.getMessage());
        }
        vo.setNewPwd(password);
        return resetPwdByCode(vo);
    }

    /**
     * 用通过加密的code，重置密码
     *
     * @throws GatewayException
     */
    @RequestMapping(params = "method=app.register.resetPwdByCode")
    @ResponseBody
    public ApiResponse resetPwdByCode(ResetPwdByCodeRequestVO vo) throws GatewayException {
        logger.info("resetPwdByCode with code={}", vo.getCode());
        try {
            if (StringUtils.isEmpty(vo.getCode())) {
                logger.warn("resetPwdByCode with empty code");
                throw new ServiceException(ServiceError.CODE_IS_EMPTY);
            }
            if (StringUtils.isEmpty(vo.getNewPwd())) {
                logger.warn("resetPwdByCode error newPwd is null with code={}", vo.getCode());
                throw new ServiceException(ServiceError.NEW_PWD_IS_NULL);
            }
            if (!StringUtils.registerValidatePassword(vo.getNewPwd())) {
                logger.warn("resetPwdByCode error newpwd is not rule with code={}, newpwd={}", vo.getCode(), vo.getNewPwd());
                throw new ServiceException(ServiceError.NEW_PWD_IS_NOT_RULE);
            }

            DESRequestBO desRequestBO = new DESRequestBO();
            desRequestBO.setCode(vo.getCode());
            String desCode = serviceCaller.call("users.desDecrypt", desRequestBO, String.class);
            JSONObject json = JSONObject.parseObject(desCode);
            int uid = json.getIntValue("uid");
            int time = json.getIntValue("time");
            //验证邮件发送超过一天都没有打开，则验证信息无效
            if (uid == 0 || time + 86400 < DateUtil.getCurrentTimeSecond()) {
                logger.warn("resetPwdByCode time over with code={}, uid={}, time={}", vo.getCode(), uid, time);
                throw new ServiceException(ServiceError.CODE_IS_INVALID);
            }

            ChangePwdRequestBO bo = new ChangePwdRequestBO();
            bo.setUid(uid);
            bo.setNewpwd(vo.getNewPwd());
            serviceCaller.call("users.changePwd", bo, CommonRspBO.class);
        } catch (Exception e) {
            logger.error("resetPwdByCode error with code=" + vo.getCode(), e);
            throw new ServiceException(ServiceError.CODE_IS_INVALID);
        }
        return new ApiResponse.ApiResponseBuilder().message(Constant.DEFAULT_SUCCESS_MESSAGE).build();
    }


}
