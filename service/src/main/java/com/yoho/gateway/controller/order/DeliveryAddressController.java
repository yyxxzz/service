package com.yoho.gateway.controller.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.order.request.DeliveryAddressRequest;

/**
 * qianjun 2016/6/14
 */
@Controller
public class DeliveryAddressController {
    private Logger logger = LoggerFactory.getLogger(DeliveryAddressController.class);

    @Autowired
    private ServiceCaller serviceCaller;
    /**
     * 修改订单收货地址
     */
    @RequestMapping(params = "method=app.SpaceOrders.updateDeliveryAddress")
    @ResponseBody
    public ApiResponse updateDeliveryAddress(@RequestParam("order_code") String orderCode,
                                             @RequestParam(value="address_id",required = false) String addressId,
                                             @RequestParam(value="user_name",required = false) String userName,
                                             @RequestParam(value="area_code",required = false) String areaCode,
                                             @RequestParam(value="address",required = false) String address,
                                             @RequestParam(value="mobile",required = false) String mobile,
                                             @RequestParam(value="phone",required = false) String phone) {
        logger.info("updateDeliveryAddress by orderCode {}, addressId {}, userName {}, areaCode {}, address {}, mobile {} and phone {}.",
                orderCode, addressId , userName , areaCode, address , mobile , phone );
        DeliveryAddressRequest deliveryAddressRequest = new DeliveryAddressRequest();
        deliveryAddressRequest.setOrderCode(orderCode);
        deliveryAddressRequest.setAddressId(addressId);
        deliveryAddressRequest.setUserName(userName);
        deliveryAddressRequest.setAreaCode(areaCode);
        deliveryAddressRequest.setAddress(address);
        deliveryAddressRequest.setMobile(mobile);
        deliveryAddressRequest.setPhone(phone);
        serviceCaller.call("order.updateDeliveryAddress", deliveryAddressRequest, void.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("修改订单收货地址成功").build();
    }
}
