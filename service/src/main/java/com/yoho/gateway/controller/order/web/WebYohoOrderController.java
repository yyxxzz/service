package com.yoho.gateway.controller.order.web;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.order.OrderServices;
import com.yoho.service.model.order.response.CountBO;
import com.yoho.service.model.order.response.Orders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class WebYohoOrderController {

    @Autowired
    private ServiceCaller serviceCaller;

    /**
     * 获取待处理订单总数
     */
    @RequestMapping(params = "method=web.SpaceOrders.getPendingOrderCount")
    @ResponseBody
    public ApiResponse getPendingOrderCount(@RequestParam(value = "uid", required = false) Integer uid) {
        CountBO response;
        if (uid == null) {
            response = CountBO.valueOf(0);
        } else {
            Orders request = new Orders();
            request.setUid(uid);
            response = serviceCaller.call(OrderServices.findPendingOrderCountByUid, request, CountBO.class);
        }
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).build();
    }


}