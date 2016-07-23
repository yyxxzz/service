package com.yoho.gateway.controller.order.shopping;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.cache.UserOrderCache;
import com.yoho.service.model.order.request.ShoppingTicketRequest;
import com.yoho.service.model.order.response.shopping.ShoppingSubmitResponse;
import com.yoho.service.model.order.response.shopping.ShoppingTicketQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * qianjun 2016/5/30
 */
@Controller
public class TicketShoppingController {
    public final static String SHOPPING_SUBMIT_TICKET = "order.submitTicket";
    public final static String SHOPPING_ADDQUERY_TICKET = "order.addAndQueryTicket";

    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
    private UserOrderCache userOrderCache;

    private final Logger logger = LoggerFactory.getLogger(TicketShoppingController.class);

    /**
     * 电子票添加
     *
     * @return
     */
    @RequestMapping(params = "method=app.shopping.ticket")
    @ResponseBody
    public ApiResponse ticket(@RequestParam(value = "uid", required = true) int uid,
                              @RequestParam(value = "product_sku", required = true) int product_sku,
                              @RequestParam(value = "buy_number", required = false) Integer buy_number,
                              @RequestParam(value = "use_yoho_coin", required = false, defaultValue = "0") double use_yoho_coin,
                              @RequestParam(value = "client_type", required = false) String client_type,
                              @RequestParam(value = "app_version", required = false) String app_version) {
        logger.info("call app.Shopping.ticket, uid:{}, product_sku:{},buy_number:{},use_yoho_coin:{},client_type:{}", uid, product_sku, buy_number, use_yoho_coin, client_type);
        ShoppingTicketRequest request = new ShoppingTicketRequest();
        request.setUid(uid);
        request.setUse_yoho_coin(use_yoho_coin);
        request.setProduct_sku(product_sku);
        request.setBuy_number(buy_number);
        request.setClient_type(client_type);
        request.setApp_version(app_version);
        ShoppingTicketQueryResult response = serviceCaller.call(SHOPPING_ADDQUERY_TICKET, request, ShoppingTicketQueryResult.class);

        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("ticket ok.").build();
    }

    /**
     * 电子票下单
     *
     * @return
     */
    @RequestMapping(params = "method=app.shopping.submitTicket")
    @ResponseBody
    public ApiResponse submitticket(HttpServletRequest httpServletRequest,
                                    @RequestParam(value = "uid", required = true) Integer uid,
                                    @RequestParam(value = "product_sku", required = true) Integer product_sku,
                                    @RequestParam(value = "buy_number", required = false) Integer buy_number,
                                    @RequestParam(value = "mobile", required = true) String mobile,
                                    @RequestParam(value = "use_yoho_coin", required = false, defaultValue = "0") double use_yoho_coin,
                                    @RequestParam(value = "client_type", required = false, defaultValue = "iphone") String client_type,
                                    @RequestParam(value = "qhy_union", required = false) String qhy_union,
                                    @RequestParam(value = "app_version", required = false) String app_version) {
        logger.info("call app.Shopping.submitTicket,uid:{}, product_sku:{}, buy_number:{},mobile:{},use_yoho_coin:{},client_type:{}",
                uid, product_sku, buy_number, mobile, use_yoho_coin, client_type);
        ShoppingTicketRequest request = new ShoppingTicketRequest();
        request.setUid(uid);
        request.setUse_yoho_coin(use_yoho_coin);
        request.setUser_agent(httpServletRequest.getHeader("User-Agent"));
        request.setProduct_sku(product_sku);
        request.setBuy_number(buy_number);
        request.setMobile(mobile);
        request.setClient_type(client_type);
        request.setQhy_union(qhy_union);
        request.setApp_version(app_version);
        ShoppingSubmitResponse response = serviceCaller.call(SHOPPING_SUBMIT_TICKET, request, ShoppingSubmitResponse.class);

        //下单后，清除各类订单统计缓存
        userOrderCache.clearOrderCountCache(uid);
        return new ApiResponse.ApiResponseBuilder().data(response).code(200).message("submit ticket order ok.").build();
    }
}
