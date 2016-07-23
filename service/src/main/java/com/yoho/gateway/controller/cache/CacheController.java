package com.yoho.gateway.controller.cache;

import com.yoho.gateway.cache.MemecacheClientHolder;
import com.yoho.gateway.controller.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * query and clear memcache or redis
 */
@Controller
public class CacheController {

    private final Logger logger = LoggerFactory.getLogger(CacheController.class);


    @Autowired
    private MemecacheClientHolder memecacheClientHolder;


    @RequestMapping(params = "method=get.memcache")
    public
    @ResponseBody
    ApiResponse getMem(@RequestParam String key) {
        String value = memecacheClientHolder.getLevel1Cache().get(key, String.class);
        ApiResponse rsp = new ApiResponse.ApiResponseBuilder().data(value).build();

        logger.info("get memecache key: {} value: {}", key, value);

        return rsp;
    }

    @RequestMapping(params = "method=clear.memcache")
    public
    @ResponseBody
    ApiResponse clearMem(@RequestParam String key) {
        memecacheClientHolder.getLevel1Cache().delete(key);
        ApiResponse rsp = new ApiResponse.ApiResponseBuilder().build();

        logger.info("clear memecache key: {}", key);
        return rsp;
    }


}
