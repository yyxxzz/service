package com.yoho.gateway.controller.promotion;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.promotion.UserOrderYohoCoinBo;
import com.yoho.service.model.promotion.request.UserOrderYohoCoinReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by sailing on 2016/1/8.
 */

@Controller
public class YohoCoinController {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private ServiceCaller service;

    private static final String YOHOCOIN_USERORDER = "promotion.queryYohoCoinByUserOrder";
    @RequestMapping(params = "method=app.promotion.queryUserShowYohoCoin")
    @ResponseBody
    public ApiResponse queryUserYohoCoinByOrder(@RequestParam(value = "uid") Integer userId,
                                     @RequestParam(value = "eventCode") String eventCode
    ){
        logger.info("method=app.promotion.queryUserShowYohoCoin,param: [userId {}], [eventCode {}]",userId,eventCode);
        UserOrderYohoCoinReq req = new UserOrderYohoCoinReq();
        req.setUid(userId);
        req.setEventCode(eventCode);
        UserOrderYohoCoinBo userOrderYohoCoin = service.call(YOHOCOIN_USERORDER, req, UserOrderYohoCoinBo.class);
        ApiResponse response = new ApiResponse.ApiResponseBuilder().data(userOrderYohoCoin).build();
        return response;
    }
}
