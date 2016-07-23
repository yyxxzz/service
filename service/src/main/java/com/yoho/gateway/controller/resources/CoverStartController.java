package com.yoho.gateway.controller.resources;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.resources.CoverStartVO;
import com.yoho.service.model.resource.CoverStartBO;
import com.yoho.service.model.resource.request.CoverStartRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * qianjun 2016/3/17
 */
@Controller
public class CoverStartController {

    private static final Logger logger = LoggerFactory.getLogger(CoverStartController.class);

    @Autowired
    private ServiceCaller serviceCaller;

    /**
     * 获取首页启动页
     */
    @RequestMapping(params = "method=app.cover.getCoverStart")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.COVER_START)
    public ApiResponse getCoverStart(@RequestParam("type") String type) {
        logger.info("get cover start by type {}.", type);
        CoverStartRequest coverStartRequest = new CoverStartRequest();
        coverStartRequest.setType(type);
        CoverStartBO coverStartBO = serviceCaller.call("resources.getCoverStart", coverStartRequest, CoverStartBO.class);
        CoverStartVO coverStartVO = new CoverStartVO();
        BeanUtils.copyProperties(coverStartBO, coverStartVO);
        logger.info("get cover start by type {} success.", type);
        return new ApiResponse.ApiResponseBuilder().code(200).message("App " + type.toLowerCase() + " ADS").data(coverStartVO).build();
    }
}
