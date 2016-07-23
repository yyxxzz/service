package com.yoho.gateway.test;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xjipeng on 16/2/16.
 */
public class TestAppVersion {

    private static final Logger logger = LoggerFactory.getLogger(TestAppVersion.class);

    @Test
    public void testVersion(){

        logger.info("appVersion {}, currentVersion {}, result:{}", "4.0.0.21212312", "4.0.1", compareVersion("4.0.0.21212312","4.0.1")) ;

        logger.info("appVersion {}, currentVersion {}, result:{}", "3.7.2", "4.0.1", compareVersion("3.7.2","4.0.1") ) ;

        logger.info("appVersion {}, currentVersion {}, result:{}", "4.1.0.9001233", "4.0.1",compareVersion("4.1.0.9001233", "4.0.1"));

        logger.info("appVersion {}, currentVersion {}, result:{}", "4.0.1.9001233", "4.0.1", compareVersion("4.0.1.9001233", "4.0.1"));

        logger.info("appVersion {}, currentVersion {}, result:{}", "4.1.0.9001233","4.0.1" , compareVersion("4.1.0.9001233","4.0.1") ) ;

    }

    private EnResult compareVersion(String appVersion, String currentVersion){

        String app[] = appVersion.split("\\.") ;
        String current[] = currentVersion.split("\\.") ;

        if( app.length != 4 ){
            logger.debug("length of app version is too short");
            return EnResult.smaller ;
        }

        logger.debug("version0 app {}, current {} ", NumberUtils.toInt(app[0]) , NumberUtils.toInt(current[0]));
        if( NumberUtils.toInt(app[0]) > NumberUtils.toInt(current[0]) ){
            return EnResult.bigger ;
        } else if( NumberUtils.toInt(app[0]) < NumberUtils.toInt(current[0]) ){
            return EnResult.smaller;
        }

        logger.debug("version1 app {}, current {} ", NumberUtils.toInt(app[1]) , NumberUtils.toInt(current[1]));
        if( NumberUtils.toInt(app[1]) > NumberUtils.toInt(current[1]) ){
            return EnResult.bigger ;
        } else if( NumberUtils.toInt(app[1]) < NumberUtils.toInt(current[1]) ){
            return EnResult.smaller;
        }

        logger.debug("version2 app {}, current {} ", NumberUtils.toInt(app[2]) , NumberUtils.toInt(current[2]));
        if( NumberUtils.toInt(app[2]) > NumberUtils.toInt(current[2]) ){
            return EnResult.bigger ;
        } else if( NumberUtils.toInt(app[2]) < NumberUtils.toInt(current[2]) ){
            return EnResult.smaller;
        }

        return EnResult.same;

    }

    enum EnResult
    {
        bigger,
        same ,
        smaller
    };
}
