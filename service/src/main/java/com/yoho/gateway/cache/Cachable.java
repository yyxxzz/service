package com.yoho.gateway.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记Controller要进行Cache的方法.
 * Created by chzhang@yoho.cn on 2015/11/11.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cachable {

    /**
     * Expire time in seconds. default 取配置文件中的expire时间
     * @return  expire time
     */
    int expire() default  -1;

    /**
     * 是否需要对key进行md5。如果key 超长，memcache会有问题
     * @return 是否需要对key进行md5
     */
    boolean needMD5() default  false;

    /**
     * 一级缓存排除掉那些参数
     * @return 要排除的参数次序，从0开始
     */
    int[] excludeArgs() default {};


    /**
     * 二级缓存排除掉那些参数
     * @return 要排除的参数次序，从0开始
     */
    int[] excludeL2Args() default {};

}
