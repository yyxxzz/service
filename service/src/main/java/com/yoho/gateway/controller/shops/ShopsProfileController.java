package com.yoho.gateway.controller.shops;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.netflix.config.DynamicIntProperty;
import com.netflix.config.DynamicPropertyFactory;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.gateway.model.shops.LoginReqVO;
import com.yoho.gateway.model.shops.PwdReqVo;
import com.yoho.gateway.utils.constants.Constants;
import com.yoho.service.model.shops.request.LoginReqBo;
import com.yoho.service.model.shops.request.PwdReqBo;
import com.yoho.service.model.shops.response.LoginRespBo;

@Controller
public class ShopsProfileController {
 
	@Autowired
	private YHRedisTemplate<String, String> yhRedisTemplate;

	@Autowired
	private YHValueOperations<String, String> yhValueOperations;

	@Resource
	ServiceCaller serviceCaller;

	private Logger logger = LoggerFactory.getLogger(ShopsProfileController.class);

	private static final String LOGIN_SUCCESS_MESSAGE = "登录成功";

	/**
	 * 修改密码<br>
	 * 使用场景：修改密码页面用，页面有原密码，新密码，新密码确认<br>
	 * 入口参数有:商家端pid,旧密码，新密码
	 * 
	 * @param vo 密码修改情报
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.shops.changePwd")
	@ResponseBody
	public ApiResponse changePwd(PwdReqVo vo) throws GatewayException {
		
		// 参数检查
		if(vo == null){
			logger.warn("changePwd: request param is null");
			throw new GatewayException(500, "request param is null");	
		}
		logger.info("Begin call ShopsProfileController.changePwd gateway. with pid={}", vo.getPid());
		
		PwdReqBo bo = new PwdReqBo();
		BeanUtils.copyProperties(vo, bo);
		
		serviceCaller.call("platform.changePwd", bo, PwdReqBo.class);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("修改成功！").build();
		return response;
	}

	/**
	 * 登录<br>
	 * 使用场景：商家端手机APP的登录按钮用<br>
	 * 入口参数：account(账号),password(密码)
	 * 
	 * @param vo 账号登录情报
	 * @return
	 * @throws GatewayException
	 */
	@RequestMapping(params = "method=app.shops.login")
	@ResponseBody
	public ApiResponse login(LoginReqVO vo, HttpServletResponse httpServletResponse, HttpServletRequest request) throws GatewayException {
		
		//(1)判断请求参数是否为空
		if(null == vo){
			logger.warn("login: request param is null");
			throw new GatewayException(500, "request param is null");
		}
		logger.info("Begin call ShopsProfileController.login gateway. with account={}", vo.getAccount());

		//(2)获取透传的IP, 并且获取当前的IP的登录次数,并且登录次数+1, 如果1分钟之内,超过设定的登录次数, 不允许登录
		String ip = RemoteIPInterceptor.getRemoteIP();
		
		//(3)校验同一个IP的登录次数, 如果同一个IP登录次数过多,则不允许登录
		try {
			long shopsIpLimitTimes = yhValueOperations.increment(Constants.LOGIN_SHOPS_ID_LIMIT_TIMES + ip, 1);
			yhRedisTemplate.longExpire(Constants.LOGIN_SHOPS_ID_LIMIT_TIMES + ip, 1, TimeUnit.MINUTES);
			logger.info("login: ip is {}, times is {}, account is {}", ip, shopsIpLimitTimes, vo.getAccount());

			DynamicIntProperty loginIpLimit = DynamicPropertyFactory.getInstance().getIntProperty("login.shops.ip.limit.times", -1);
			if(null != loginIpLimit && -1 != loginIpLimit.get() && shopsIpLimitTimes > loginIpLimit.get()){
				logger.error("login error: current ip login times more than 20 times in 1 minuter. ip is {}, account is {}, client_type is {}", ip, vo.getAccount(), vo.getClient_type());
				throw new GatewayException(500, "当前IP请求次数太多");
			}
		}catch (GatewayException e){
			throw e;
		}catch (Exception e){
			logger.warn("redis exception. login limit ip login times. ip is {}, error msg is {}", ip, e.getMessage());
		}

		//(4)获取同一个号码登录失败的次数,如果10分钟登录失败10次, 不允许登录
		try{
			String loginFailedStr = yhValueOperations.get(Constants.LOGIN_SHOPS_FAILED_TIMES + vo.getAccount());//key=yh:shops:loginFailed:
			long loginFailedTimes = (StringUtils.isEmpty(loginFailedStr)? 0 : Long.valueOf(loginFailedStr));
			DynamicIntProperty loginFailedLimit = DynamicPropertyFactory.getInstance().getIntProperty("login.shops.loginfailed.limit.time", -1);//获取设置的登录次数
			logger.debug("login in. loginFailed times is {}, mobile is {}, loginFailedLimit is {}", loginFailedStr, vo.getAccount(), loginFailedLimit);

			if(null != loginFailedLimit && -1 != loginFailedLimit.get() && loginFailedTimes > loginFailedLimit.get()){
				logger.error("login error: current user login failed times more than 10 times in 10 minutes. account is {}, ip is {}", vo.getAccount(), ip);
				throw new GatewayException(10010, "用户名或密码错误");
			}
		}catch (GatewayException e){
			throw e; //同一个号码登录失败次数过多
		}catch (Exception e){
			logger.warn("redis exception. signin limit ip login times. ip is {}, error msg is {}", ip, e.getMessage());
		}

		//(5)设置请求的参数
		LoginReqBo bo = new LoginReqBo();
		BeanUtils.copyProperties(vo, bo);

		//(6)调用登录接口登录
		LoginRespBo result = null;
		try{
			result = serviceCaller.call("platform.login", bo, LoginRespBo.class);
	    }catch(ServiceException e){
			//记录登录失败次数, 失败次数+1
			try {
				yhValueOperations.increment(Constants.LOGIN_SHOPS_FAILED_TIMES + vo.getAccount(), 1); //KEY=yh:users:loginFailed:
				yhRedisTemplate.longExpire(Constants.LOGIN_SHOPS_FAILED_TIMES + vo.getAccount(), 10, TimeUnit.MINUTES);
				logger.warn("login: login failed.account is {}, ip is {}", vo.getAccount(), ip);
			}catch (Exception e1){
				logger.warn("redis exception. login failed and load redis failed. account is {}, exception is {}", vo.getAccount(), e.getMessage());
			}
			throw e;
		}
		logger.info("call shops.login with account={}, client_type={}, with result is {}", vo.getAccount(), vo.getClient_type(), result);

		ApiResponse response = new ApiResponse.ApiResponseBuilder().message(LOGIN_SUCCESS_MESSAGE).data(result).build();

		//(7)登录成功后, 设置cookie
		if (result != null) {
			Cookie cookie = new Cookie("JSESSIONID", result.getSession_key());
			cookie.setMaxAge(24 * 60 * 60); //24小时
			httpServletResponse.addCookie(cookie);
		}
		return response;
	}



}
