package com.yoho.gateway.cache;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.yoho.core.common.utils.MD5;
import com.yoho.core.rest.exception.ServiceNotAvaibleException;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.internel.CacheStatusStat;
import com.yoho.gateway.exception.GatewayException;

/**
 * 对controller进行AOP，实现HTTP Restful接口的cache。 请求的处理流程：
 * 1.从一级cache中找，看是否hit,如果hit，直接返回结果
 * 2. 如果一级cache miss，则调用服务，
 * 2.1 如果服务调用成功，则将服务调用结果返回，并且设置到一级cache和二级cache中；
 * 2.2 如果服务调用失败，则从二级cache中取结果返回。
 * <p/>
 * Created by chzhang@yoho.cn on 2015/11/11.
 */
@Aspect
@Component
public class ControllerCacheAop {


    private static final Logger logger = LoggerFactory.getLogger(ControllerCacheAop.class);
    @Autowired
    MemecacheClientHolder memecacheClientHolder;
    @Autowired
    private CacheStatusStat cacheStatusStat;

    //we define a pointcut for all controllers
    @Pointcut("within(@org.springframework.stereotype.Controller *)")
    public void controllerPointcut() {
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllerPointcut() {
    }

    /**
     * a pointcut for all class with annotation : {@link Cachable }
     */
    @Pointcut("@annotation(com.yoho.gateway.cache.Cachable)")
    public void cacheAnnotationPointCut() {
    }


    /**
     * Operations
     * 先从cache中，找不到则调用controller原来的实现（一般是调用服务），如果调用服务有异常 {@link ServiceNotAvaibleException}，则从二级缓存中取
     */

    @Around("(controllerPointcut()|| restControllerPointcut()) &&  cacheAnnotationPointCut() ")
    public Object cacheAop(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //方法名称
        final String methodName = signature.getMethod().getName();
        //返回类型
        final Class<?> returnType = signature.getMethod().getReturnType();

        logger.debug("Enter controller pointcut: {}", methodName);


        //some info
        Cachable cacheExpire = signature.getMethod().getAnnotation(Cachable.class);


        final String cacheKey = this.getCacheKey(joinPoint, cacheExpire);
        final String cacheKeyL2 = this.getL2CacheKey(joinPoint, cacheExpire);

        String level1_cache_key = "YH:GW:L1:" + cacheKey;
        String level2_cache_key = "YH:GW:L2:" + cacheKeyL2;
        //key 是否需要md5
        if (cacheExpire.needMD5()) {
            level1_cache_key = MD5.md5(level1_cache_key);
            level2_cache_key = MD5.md5(level2_cache_key);
        }


        //从一级缓存中获取数据
        try {
            Object level1_obj = this.memecacheClientHolder.getLevel1Cache().get(level1_cache_key, returnType);
            if (level1_obj != null) {
                logger.debug("Cache1 hit for method:{} at key:{}.", methodName, level1_cache_key);
                cacheStatusStat.report(methodName, cacheKey, CacheStatusStat.Status.Level1_Hit);
                return level1_obj;
            }
        }catch (Exception e){
            logger.warn("get from level 1 cache exception.", e);
            cacheStatusStat.report(methodName, cacheKey, CacheStatusStat.Status.Level1_exception, e);
        }


        //cache miss at level1: 调用原来的请求，然后将结果缓存到cache1&cache2中。如果调用异常，则从cache2中查找
        Object httpResponse;
        try {
            httpResponse = joinPoint.proceed();

            if (httpResponse != null) {

                //一级缓存失效的时间，如果指定了，则使用指定的值。如果没有指定，则使用配置的值
                int expire = cacheExpire.expire();
                if (expire <= 0) {
                    expire = memecacheClientHolder.getLevel1Expire();
                }
                this.memecacheClientHolder.getLevel1Cache().set(level1_cache_key, expire, httpResponse);

                //cache to level 2
                this.memecacheClientHolder.getLevel2Cache().set(level2_cache_key, memecacheClientHolder.getLevel2Expire(), httpResponse);

                cacheStatusStat.report(methodName, cacheKey, CacheStatusStat.Status.Level1_miss);
                logger.debug("Cache1 miss for method:{} at key:{}. call service and put to cache success", methodName, cacheKey);
            }

        } catch (Throwable throwable) {

            //业务逻辑正常的异常，不从二级缓存中获取，直接抛出异常
            if (!shouldGetFromLevel2(throwable)) {
                throw throwable;
            }

            //从二级缓存中获取数据，并且回填到1级缓存
            httpResponse = this.memecacheClientHolder.getLevel2Cache().get(level2_cache_key, returnType);
            if (httpResponse != null) {
                //回填到一级缓存
                this.memecacheClientHolder.getLevel1Cache().set(level1_cache_key, 120, httpResponse);

                logger.debug("Cache1 miss for {} at cache1 key : {}. Found from cache2 success", methodName, cacheKey);
                cacheStatusStat.report(methodName, cacheKey, CacheStatusStat.Status.Level2_hit);
            } else {
                logger.error("Cache1 & Cache2 miss for method:{} at key:{}.", methodName, cacheKey);
                cacheStatusStat.report(methodName, cacheKey, CacheStatusStat.Status.Level2_miss);
                throw throwable;
            }
        }

        return httpResponse;
    }

    /**
     * 只有不是ServiceException 和  GatewayException 的时候，才需要从二级缓存中获取数据
     *
     * @param ex
     * @return
     */
    private boolean shouldGetFromLevel2(Throwable ex) {
        if (ex instanceof ServiceException || ex instanceof GatewayException) {
            return false;
        }
        return true;
    }


    /**
     * 根据拦截的方法的参数，生成cache的key.  yh_gw:METHOD_NAME:METHOD_PARAM
     *
     * @param joinPoint   拦截点
     * @param cacheExpire
     * @return key
     */
    private String getCacheKey(ProceedingJoinPoint joinPoint, Cachable cacheExpire) {

        //要排除的方法参数
        int[] excludeParams = cacheExpire.excludeArgs();

        //把参数连接起来
        List<String> methodParams =  this.exclueMethodParams(joinPoint, excludeParams);

        String cacheKeyPrefix = "yh_gw:" + joinPoint.getSignature().getName();
        return cacheKeyPrefix + ":" + String.join("-", methodParams);
    }


    /**
     * 根据拦截的方法的参数，生成cache的key.  yh_gw:METHOD_NAME:METHOD_PARAM
     *
     * @param joinPoint   拦截点
     * @param cacheExpire
     * @return key
     */
    private String getL2CacheKey(ProceedingJoinPoint joinPoint, Cachable cacheExpire) {

        int[] excludeParams = cacheExpire.excludeL2Args();

        List<String> methodParams = this.exclueMethodParams(joinPoint, excludeParams);

        String cacheKeyPrefix = "yh_gw:" + joinPoint.getSignature().getName();

        return cacheKeyPrefix + ":" + String.join("-", methodParams);
    }


    /**
     *  排除方法的某些参数
     * @param joinPoint  方法的joinpoint
     * @param excludeParams 要排除的参数顺序
     * @return  排除后的参数
     */
    private  List<String> exclueMethodParams(ProceedingJoinPoint joinPoint,  int[] excludeParams  ){

        List<String> methodParams = new LinkedList<>();
        Object arguments[] = joinPoint.getArgs();
        if (ArrayUtils.isNotEmpty(arguments)) {
            for (int i = 0; i < arguments.length; i++) {
                //排除掉某些参数
                if (ArrayUtils.contains(excludeParams, i)) {
                    continue;
                }
                Object arg = arguments[i];
                //fix key contain ' ' || b == '\n' || b == '\r' || b == 0
                String param = (arg == null ? "" : String.valueOf(arg));
                methodParams.add(param);
            }
        }

        return methodParams;

    }



}
