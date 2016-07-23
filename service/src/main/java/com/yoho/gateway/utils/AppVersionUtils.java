package com.yoho.gateway.utils;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * Created by xjipeng on 16/2/16.
 */
public class AppVersionUtils {

    /**
     *
     * @param appVersion
     * @param currentVersion
     * @return
     */
    public static EnVersionCompareResult compareVersion(String appVersion, String currentVersion){

        String app[] = appVersion.split("\\.") ;
        String current[] = currentVersion.split("\\.") ;

//        if( app.length != 4 ){
//            return EnVersionCompareResult.smaller ;
//        }

        if( NumberUtils.toInt(app[0]) > NumberUtils.toInt(current[0]) ){
            return EnVersionCompareResult.bigger ;
        } else if( NumberUtils.toInt(app[0]) < NumberUtils.toInt(current[0]) ){
            return EnVersionCompareResult.smaller;
        }

        if( NumberUtils.toInt(app[1]) > NumberUtils.toInt(current[1]) ){
            return EnVersionCompareResult.bigger ;
        } else if( NumberUtils.toInt(app[1]) < NumberUtils.toInt(current[1]) ){
            return EnVersionCompareResult.smaller;
        }

        if( NumberUtils.toInt(app[2]) > NumberUtils.toInt(current[2]) ){
            return EnVersionCompareResult.bigger ;
        } else if( NumberUtils.toInt(app[2]) < NumberUtils.toInt(current[2]) ){
            return EnVersionCompareResult.smaller;
        }

        return EnVersionCompareResult.same;

    }

    public enum EnVersionCompareResult
    {
        bigger,
        same ,
        smaller
    }

}
