package com.yoho.gateway.controller.order.payment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.service.model.order.request.OrderRequest;
import com.yoho.service.model.order.response.Orders;

@Service
public class OrderPaymentService {
    @Autowired
    protected ServiceCaller serviceCaller;
    
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * 根据订单号获取订单
     * @param orderCode
     * @return
     */
    public Orders getOrderByCode(long orderCode) {
    	logger.info("getOrdersByCode begin, orderCode: {}", orderCode);
    	
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderCode(orderCode);
        Orders orderData =  serviceCaller.call("order.getOrdersByCode", orderRequest, Orders.class);

        logger.info("getOrdersByCode end, orderCode: {}", orderCode);
        return orderData;
    }
    
    /**
     * 修改订单支付方式（通常是支付前调用）
     * @param orderCode
     * @param payment
     */
    public void updateOrdersPayment(long orderCode, byte payment) {
    	logger.info("updateOrdersPayment begin, orderCode: {}, payment: {}", orderCode, payment);
    	
    	OrderRequest orderRequest = new OrderRequest();
    	orderRequest.setOrderCode(orderCode);
    	orderRequest.setPayment(payment);
    	
    	serviceCaller.call("order.updateOrdersPaymentByCode", orderRequest, Void.class);
    	logger.info("updateOrdersPayment end, orderCode: {}, payment: {}", orderCode, payment);
    }
	
    /**
     * 订单支付成功状态更新（支付成功调用）
     * @param id
     * @param bankCode
     * @param payment
     */
    public void orderPaySuccess(int id, String bankCode, byte payment){
    	logger.info("orderPaySuccess begin, id: {}, bankCode: {}, payment: {}", id, bankCode, payment);
    	
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setId(id);
        orderRequest.setBankCode(bankCode);
        orderRequest.setPayment(payment);
        
        serviceCaller.call("order.paySuccess", orderRequest, Void.class);
        logger.info("orderPaySuccess end, id: {}", id);
    }
}
