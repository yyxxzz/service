package com.yoho.gateway.controller.order;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.order.model.invoice.InvoiceLogsBo;
import com.yoho.service.model.order.response.Orders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by chenchao on 2016/7/2.
 */
@Controller
public class InvoiceLogsController {

    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private ServiceCaller serviceCaller;
    @RequestMapping(params = "method=app.invoice.logs")
    @ResponseBody
    public ApiResponse queryLogs(@RequestParam(value = "orderCode", required = true) Long orderCode){
        logger.info("queryLogs param {}", orderCode);
        Orders orders = new Orders();
        orders.setOrderCode(orderCode);
        InvoiceLogsBo[] logsArray = serviceCaller.call("order.queryInvoiceLogs", orders, InvoiceLogsBo[].class);
        logger.info("call order service queryInvoiceLogs ,{}", logsArray);
        return new ApiResponse.ApiResponseBuilder().data(logsArray).build();
    }
}
