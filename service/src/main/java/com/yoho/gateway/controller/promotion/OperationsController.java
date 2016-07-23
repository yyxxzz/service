package com.yoho.gateway.controller.promotion;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.promotion.ActivityTemplateBo;
import com.yoho.service.model.promotion.request.ActivityTemplateReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 活动配置类接口
 * LIJIAN 2016-4-14 17:02:17
 */

@Controller
public class OperationsController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private ServiceCaller service;

    private static final String QUERY_ACTIVITY_TEMPLATE_INFO = "promotion.queryActivityTemplateInfo";


    //
    private final static int ACTIVITY_TEMPLATE_IS_NULL_CODE = 500;
    private final static String ACTIVITY_TEMPLATE_IS_NULL_MSG = "模板不存在";

    private final static int ACTIVITY_TEMPLATE_REQ_IS_NULL_CODE = 400;
    private final static String ACTIVITY_TEMPLATE_REQ_IS_NULL_MSG = "请选择要查看的模板.";

    //活动模板查询接口 根据id查询活动模板配置信息
    //http://service.yoho.cn/operations/api/v5/activitytemplate/getActivityTemplateInfo?
    // app_version=4.0.2.1603120001&client_secret=d68a1f325fbd2e62eeaf068f30ef21a2&client_type=iphone&id=72&os_version=9.3.1&screen_size=375x667&v=7
    @RequestMapping("/operations/api/{v}/activitytemplate/getActivityTemplateInfo")
    @ResponseBody
    public ApiResponse queryActivityTemplate(@RequestParam(value = "id") int id) {
        logger.info("queryActivityTemplate param:{}", id);
        if (id < 1) {
            logger.warn("queryActivityTemplate RequestBody is wrong {} ", id);
            throw new ServiceException(ACTIVITY_TEMPLATE_REQ_IS_NULL_CODE, ACTIVITY_TEMPLATE_REQ_IS_NULL_MSG);
        }
        ActivityTemplateReq req = new ActivityTemplateReq();
        req.setId(id);
        logger.info("promotion.queryActivityTemplateInfo req is {} ", req);
        ActivityTemplateBo activityTemplateBo = service.call(QUERY_ACTIVITY_TEMPLATE_INFO, req, ActivityTemplateBo.class);
        logger.info("promotion.queryActivityTemplateInfo result is {} ", activityTemplateBo);
        if (activityTemplateBo == null) {
            logger.warn("queryActivityTemplate result is null {} ", activityTemplateBo);
            throw new ServiceException(ACTIVITY_TEMPLATE_IS_NULL_CODE, ACTIVITY_TEMPLATE_IS_NULL_MSG);
        }
        ApiResponse response = new ApiResponse.ApiResponseBuilder().message("模板内容").data(activityTemplateBo).build();
        return response;
    }
}

