package com.yoho.gateway.interceptor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableListIterator;
import com.yoho.core.common.utils.MD5;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.gateway.exception.ClientNotSupportException;
import com.yoho.gateway.exception.RequestHeaderInvalidateException;
import com.yoho.gateway.exception.SecurityNotMatchException;
import com.yoho.gateway.exception.SessionExpireException;
import com.yoho.gateway.utils.AppVersionUtils;
import com.yoho.gateway.utils.ServletUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * 检查client_security中的Client_Security是否正确
 * Created by chzhang@yoho.cn on 2015/11/5.
 */

public class SecurityInterceptor implements HandlerInterceptor {


    private final Logger logger = LoggerFactory.getLogger(SecurityInterceptor.class);


    //读取配置文件中的private key 配置
    private final Map<String, String> privateKeyMap = new HashMap<>();

    @Autowired
    private YHValueOperations<String, String> valueOperations;

    @Resource(name = "yhRedisTemplate")
    private YHRedisTemplate<String, String> redisTemplate;

    // 这些url不会进行client-security校验。 例如 "/notify"
    private List<String> excludeUrls;

    //这些方法不用校验
    private List<String> excludeMethods;


    //限制本地IP访问
    private List<String> local = new LinkedList<>();


    //是否检查登录会话
    private boolean isCheckSessionEnable = true;

    //这些方法需要检查,客户端是否已经登录
    private List<String> checkSessionMethods;

    //是否启用
    private boolean isDebugEnable = false;

    public void setIsDebugEnable(boolean isDebugEnable) {
        this.isDebugEnable = isDebugEnable;
    }

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {

        Map<String, String> params = this.getRequestInfo(httpServletRequest);
        this.validVersion(params);
        //排除掉exclude和debug模式
        if (this.isIgnore(httpServletRequest)) {
            return true;
        }
        this.validateReqParams(params);
        this.validateSecutity(params, httpServletRequest);
        this.validateSession(httpServletRequest.getCookies(), params);

        return true;
    }

    private void validateSecutity(Map<String, String> reqParams, HttpServletRequest httpServletRequest) throws SecurityNotMatchException {
        String caculated_sign = this.getSign(reqParams);
        String request_sign = reqParams.get("client_secret");
        if (!request_sign.equalsIgnoreCase(caculated_sign)) {
            logger.warn("client security not match. request_sign:{}, caculate_sign:{} at:{} at params:{}", request_sign, caculated_sign, ServletUtils.getServiceName(httpServletRequest), ServletUtils.getRequestParams(httpServletRequest));
            throw new SecurityNotMatchException();
        }

    }

    private void validateSession(Cookie[] cookies, Map<String, String> params) throws SessionExpireException {
    	logger.debug("enter validateSession");
        // ------------------- 1 不需要检查登录会话的接口,直接返回 ------------------
    	logger.debug("enter validateSession");
        //1.1 , 功能总开关, 关闭后不需要校验
        // params为空,说明接口无参数, 无需校验
        if (!isCheckSessionEnable || params == null || params.size() == 0) {
            logger.debug("no need to check session info, maybe flag:isCheckSessionEnable:{} is closed ", isCheckSessionEnable);
            return;
        }

        //1.2 , h5 调用不校验
        if ("h5".equalsIgnoreCase(params.get("client_type")) || "web".equalsIgnoreCase(params.get("client_type"))) {
            logger.debug("client type is h5 or web, no need to check session info ");
            return;
        }

        //1.3 客户端接口必须携带版本号, 并且 版本号必须大于 4.0.5 ,才启用此功能
        //    两个月后,删除此代码, 所有客户端版本,都需要判断版本号
        if (params.get("app_version") == null || params.get("app_version").compareTo("4.0.5") <= 0) {
            logger.debug("app version {} is too low, no need to check session info ", params.get("app_version"));
            return;
        }

        //1.4 , 不需要检查会话对 接口, 直接返回
        String method = params.get("method");
        boolean isNeedCheckForMethod = checkSessionMethods.contains(method);
        if (method == null || !isNeedCheckForMethod) {
            logger.debug("method {} is not exist in checkSessionMethods, no need to check session info.  ", method);
            return;
        }

        // -------------- 2 对指定接口 校验会话, 比如 订单列表,订单详情,个人中心, 校验失败, 返回客户端 HTTP 401 ------------------

        String uid = params.get("uid");
        //2.1 解析sessionid
        String jSessionID = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                //解析sessionid
                if ("JSESSIONID".equals(cookie.getName())) {
                    jSessionID = cookie.getValue();
                    break;
                }
            }
        }

        //2.2 , 如果cookie中没有jSessionID , 但接口又必须校验会话, 则返回 HTTP 401, 需要重新登录
        if (jSessionID == null) {
            logger.info("check session failed, can not find session id in cookies, check session info failed, method {}, uid {}.  ", method, uid);
            throw new SessionExpireException();
        }

        // 2.3 校验会话信息
        // 会话不存在, 则返回 HTTP 401, 需要重新登录
        // 会话存在, 校验 客户端上报的 sessionid和uid, 与cached中保存的会话必须一致
        String  sessionInfo;
        try {
            sessionInfo = valueOperations.get("yh:sessionid:" + jSessionID);
            if(null == sessionInfo){
                sessionInfo = redisTemplate.opsForValue().get("yh:sessionid:" + jSessionID);
            }
        }catch (Exception redisException){
            //如果redis异常，直接放通
            logger.warn("redis exception {} when get session", redisException.getMessage());
            return;
        }

        if (uid == null || sessionInfo == null || !StringUtils.equals(sessionInfo, uid)) {
            logger.warn("check session failed, session unmatched uid, session id {}, uid {} , session info {}, method {} ",
                    jSessionID, params.get("uid"), sessionInfo, method);
            throw new SessionExpireException();
        }

    }




    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        //do nothing
    }


    private boolean isIgnore(HttpServletRequest request) {

        //如果请求url包含在过滤的url，则直接返回. 请求url可能是 "/gateway/xxx”这种包含了context的。
        //
    	logger.debug("enter isIgnore");
        if (excludeUrls != null) {
            final String requestUri = request.getRequestURI();
            if (this.urlContains(requestUri, excludeUrls)) {
            	logger.debug("isIgnore check url in excludeUrls");
                return true;
            }
        }


        //如果请求method包含这些，则不校验
        if (excludeMethods != null) {
            final String method = request.getParameter("method");
            if (StringUtils.isNotEmpty(method) && excludeMethods.contains(method)) {
            	logger.debug("isIgnore check method in excludeMethods");
                return true;
            }
        }

        //如果请求是本地请求（来自私有网络）
        if (this.isLocalRequestMatch(request)) {
        	logger.debug("isIgnore check ip is local");
              return true;
        }


        //配置文件配置为 is_debug_enable 为true，并且请求携带参数debug为XYZ，就放行
        if (isDebugEnable && "XYZ".equals(request.getParameter("debug"))) {
        	logger.debug("isIgnore check debug model");
            return true;
        }
        logger.debug("end to isIgnore check");

        return false;
    }

    /**
     * 验证头 必须包含 client_secret
     *
     * @param params 请求参数
     * @throws RequestHeaderInvalidateException
     */
    private void validateReqParams(Map<String, String> params) throws RequestHeaderInvalidateException {
        if(!params.containsKey("client_secret")){
            logger.warn("header {} not exist or empty", "client_secret");
            throw new RequestHeaderInvalidateException("client_secret");
        }

    }

    /**
     * 校验客户端版本, 4.1.1
     * @param params
     */
    private void validVersion(Map<String, String> params) throws ClientNotSupportException {
        String appVersion = params.get("app_version");
        String clientType = params.get("client_type");
        if("android".equals(clientType) && null != appVersion && appVersion.startsWith("4.1.1")){
            logger.info("valid client version. appVersion is {}, clientType is {}", appVersion, clientType);
            throw new ClientNotSupportException();
        }
    }


    /**
     * spring setter from security-keyyml
     *
     * @param keyConfigMap security key 的配置
     */
    public void setKeyConfigMap(Map<String, Object> keyConfigMap) {

        List<Map<String, Object>> keys = (List<Map<String, Object>>) keyConfigMap.get("client_keys");

        for (Map<String, Object> one : keys) {
            privateKeyMap.put((String) one.get("type"), (String) one.get("key"));
        }
    }


    private String getSign(Map<String, String> reqParams) {

        //remove some reqParams
        ImmutableList list = ImmutableList.of("/api", "client_secret", "q", "debug_data");
        SortedMap<String, String> filtedMap = new TreeMap<>();
        for (Map.Entry<String, String> entry : reqParams.entrySet()) {
            String k = entry.getKey();
            if (!list.contains(k)) {
                filtedMap.put(k, entry.getValue());
            }
        }

        //put private
        String clientType = reqParams.get("client_type");
        String privateKey = privateKeyMap.get(clientType);
        filtedMap.put("private_key", privateKey);


        //string: k1=v1&k2=v2
        List<String> array = new LinkedList<>();
        for (Map.Entry<String, String> entry : filtedMap.entrySet()) {
            String pair = entry.getKey() + "=" + entry.getValue();
            array.add(pair.trim());
        }
        String signStr = String.join("&", array);

        logger.debug("md5 str is: {}", signStr);

        //sign md5
        String sign = MD5.md5(signStr);

        return sign.toLowerCase();

    }


    /**
     * 获取请求信息： requestParam
     *
     * @param httpServletRequest
     * @return
     */
    private Map<String, String> getRequestInfo(HttpServletRequest httpServletRequest) {
        return ServletUtils.getRequestParams(httpServletRequest);
    }


    /**
     * 本地IP限制
     * @param request
     * @return
     */
    private boolean isLocalRequestMatch(HttpServletRequest request) {
        if (CollectionUtils.isEmpty(this.local)) {
            return false;
        }
        final String requestUri = request.getRequestURI();
        final String ip = this.getIP(request);
        try {
            InetAddress  inetAddress = InetAddress.getByName(ip);
            if (this.urlContains(requestUri, local) && (inetAddress.isSiteLocalAddress() || inetAddress.isLoopbackAddress())) {
                return true;
            }
        } catch (UnknownHostException e) {
            logger.error("unknown ip", e);
        }

        return false;


    }

    private boolean urlContains(String requestUri, List<String> excludeUrls) {

        for (String excludeUri : excludeUrls) {
            if (requestUri.equals(excludeUri) || requestUri.startsWith(excludeUri) || requestUri.startsWith("/gateway" + excludeUri) || requestUri.endsWith(excludeUri)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 设置不校验的url地址
     */
    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    public void setExcludeMethods(List<String> excludeMethods) {
        this.excludeMethods = excludeMethods;
    }

    public void setCheckSessionMethods(List<String> checkSessionMethods) {
        this.checkSessionMethods = checkSessionMethods;
    }

    public void setIsCheckSessionEnable(boolean isCheckSessionEnable) {
        this.isCheckSessionEnable = isCheckSessionEnable;
    }

    private String getIP(HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getHeader("X-Real-IP");
        if (StringUtils.isEmpty(ip)) {
            ip = httpServletRequest.getRemoteAddr();
        }
        return ip;
    }

    public void setLocal(List<String> local) {
        this.local = local;
    }
}
