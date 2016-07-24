package com.yoho.gateway.controller.order;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.yoho.core.common.utils.LocalIp;
import com.yoho.core.common.utils.YHMath;
import com.yoho.core.message.YhProducerTemplate;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.event.PaymentEvent;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.cache.UserOrderCache;
import com.yoho.gateway.controller.order.payment.common.PayEventEnum;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.controller.order.payment.common.PayTypeEnum;
import com.yoho.gateway.controller.order.payment.service.OrderPaymentService;
import com.yoho.gateway.controller.order.payment.service.PaymentEventService;
import com.yoho.service.model.order.request.OrderRequest;
import com.yoho.service.model.order.response.Orders;

/**
 * Created by ming on 16/1/19.
 * add by dh 
 */
public abstract class AbstractController {

    protected static final Logger loggerBD = LoggerFactory.getLogger("paymentBDLogger");
    //public static final int EXPIRE_INTERVAL = 7200;  // 默认支付超时时间, 2小时
    private static final String PAYMENT_FINISH_MQ_TOPTIC ="order.payment";

    @Autowired
    protected ServiceCaller serviceCaller;

    @Resource
    protected YhProducerTemplate producerTemplate;

    @Value("${erp.order.status.url}")
    private String erpOrderStatusUrl;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private UserOrderCache userOrderCache;
    
    @Autowired
    private OrderPaymentService orderPayService;
    
    @Autowired
    private PaymentEventService publisher;
    
    //@Value("${erp.message.sync.type}")
    //private String erpMessageSyncType;

    protected void notify(String orderCode, PayResult payResult, Logger logger) throws Exception {

        // 检查订单状态,支付金额, etc.
        Orders orderData = checkOrder(orderCode, payResult, logger);
        payResult.setUid(orderData.getUid());
        payResult.setOrderType(orderData.getOrderType());

        // 大数据采集日志
        logForBD(payResult);

        // 订单状态: 0 => '待付款',1 => '已付款',2 => '备货中',3 => '配货中',4 => '已发货',5 => '运输中',6 => '已完成'
        // 更新订单支付状态
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setId(orderData.getId());
        orderRequest.setBankCode(payResult.getBankCode());
        orderRequest.setPayment(payResult.getPaymentID());
        try {
            serviceCaller.call("order.paySuccess", orderRequest, Void.class);
        } catch (Exception e) {
            logger.error("[{}] order.paySuccess failed", orderCode);
            throw new ServiceException(ServiceError.ORDER_SUBMIT_CALL_ERP);
        }

        logger.info("[{}] order.paySuccess succeeded", orderCode);

        //去掉${erp.message.sync.type}开关判断，直接发送MQ
        notifyERPMQ(payResult, logger);
        logger.info("[{}] ERP MQ sent succeeded", orderCode);
        
        //支付成功后，清除各订单统计缓存
        userOrderCache.clearOrderCountCache(orderData.getUid());
        
//        // 向 ERP 发送消息
//        if (isCreateERPOrderByMQ()) {
//            // 发送 MQ 消息
//            notifyERPMQ(payResult, logger);
//            logger.info("[{}] ERP MQ sent succeeded", orderCode);
//            return;
//        }
//
//        //通知erp
//        try {
//            notifyERP(payResult, logger);
//        } catch (ServiceException e) {
//            // 如果 ERP 订单状态更新失败, 则不再更新后台订单数据,抛出异常
//            logger.error("[{}] ERP order call failed", orderCode);
//            throw new ServiceException(ServiceError.ORDER_SUBMIT_CALL_ERP);
//        }
//
//        logger.info("[{}] ERP call succeeded", orderCode);
    }
    
    /**
     * 支付回调处理
     * @param payResult
     * @param logger
     * @throws Exception
     */
    protected void notifyProcess(PayResult payResult, Logger logger) throws Exception {

    	//获取订单
    	Orders orderData = getOrder(payResult, logger);
    	
    	fillPayResultData(orderData, payResult, logger);
    	
    	//校验订单支付回调数据
    	checkOrderPayData(orderData, payResult, logger);
    	
        //回调处理
        payProcess(orderData, payResult, logger);
    }
    
    /**
     * 回调成功处理
     * @param orderData
     * @param payResult
     * @param logger
     * @throws Exception
     */
    protected void payProcess(Orders orderData, PayResult payResult, Logger logger) throws Exception {
    	logger.info("[{}] order payment process..", payResult.getOrderCode());
    	
    	//1、修改订单支付状态
    	orderPaySuccess(orderData, payResult, logger);
        
        //2、通知ERP
        notifyERPMQ(payResult, logger);
        
        //支付成功后，清除各订单统计缓存
        clearOrderCountCache(orderData.getUid());
        
        //3、大数据采集日志
        logForBD(payResult);        
    }    

    /**
     * MQ方式通知ERP
     * @param payResult
     * @param logger
     * @throws ServiceException
     */
    protected void notifyERPMQ(PayResult payResult, Logger logger) throws ServiceException {
        JSONObject statusData = new JSONObject();
        statusData.put("paymentCode", payResult.getPaymentID());
        statusData.put("bankCode", payResult.getBankCode());
        statusData.put("bankName", payResult.getBankName());
        statusData.put("amount", payResult.getTotalFee());
        statusData.put("payOrderCode", payResult.getPayOrderCode());
        statusData.put("tradeNo", payResult.getTradeNo());
        statusData.put("bankBillNo", payResult.getBankBillNo());

        JSONObject data = new JSONObject();
        data.put("orderCode", payResult.getOrderCode());
        data.put("status", 200);
        data.put("statusData", statusData);

        logger.info("[{}] send MQ message is : {}", payResult.getOrderCode(), data);

        try {
            Map<String,Object> map = new HashMap<>();
            map.put("order_code",payResult.getOrderCode());
            map.put("uid",payResult.getUid());

            producerTemplate.send(PAYMENT_FINISH_MQ_TOPTIC, data, map);
            logger.info("[{}] send MQ message success", payResult.getOrderCode());
        } catch (Exception ex) {
            logger.error("[{}] send MQ fail, json:{}", payResult.getOrderCode(), data, ex);
            throw new ServiceException(ServiceError.ORDER_SUBMIT_CALL_ERP);
        }
    }

    /**
     * 请求方式通知ERP
     * @param payResult
     * @param logger
     * @throws ServiceException
     */
    protected void notifyERP(PayResult payResult, Logger logger) throws Exception {

        JSONObject statusData = new JSONObject();
        statusData.put("paymentCode", payResult.getPaymentID());
        statusData.put("bankCode", payResult.getBankCode());
        statusData.put("bankName", payResult.getBankName());
        statusData.put("amount", payResult.getTotalFee());
        statusData.put("payment", payResult.getPaymentID());
        statusData.put("payOrderCode", payResult.getPayOrderCode());
        statusData.put("tradeNo", payResult.getTradeNo());
        statusData.put("bankBillNo", payResult.getBankBillNo());
        JSONObject data = new JSONObject();
        data.put("orderCode", payResult.getOrderCode());
        data.put("status", 200);
        data.put("statusData", statusData);
        LinkedMultiValueMap<String, Object> req = new LinkedMultiValueMap<String, Object>();
        req.add("data", data.toJSONString());

        logger.info("[{}] ERP req: {}", payResult.getOrderCode(), req.get("data"));

        String json = null;
        JSONObject jsonObject;

        try {
            json = restTemplate.postForObject(erpOrderStatusUrl, req, String.class);
            jsonObject = JSONObject.parseObject(json);
            logger.info("[{}] ERP resp: {}", payResult.getOrderCode(), json);
        }
        catch (Exception e) {
            logger.error("[{}] Erp order status call fail:{}, json:{}", payResult.getOrderCode(), e, json);
            throw new ServiceException(ServiceError.ORDER_SUBMIT_CALL_ERP);
        }
        int code = jsonObject.getIntValue("code");
        // logger.debug("ERP return code: [{}]", code);
        if (code!=200) {
            logger.warn("[{}] ERP call return invalid code", payResult.getOrderCode());
            throw new ServiceException(ServiceError.ORDER_SUBMIT_CALL_ERP);
        }
    }

    /**
     * 校验订单数据
     */
    protected Orders checkOrder(String orderCode, PayResult payResult, Logger logger) throws Exception {
        // 根据传入的orderCode,构造请求,查询订单数据
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderCode(Long.parseLong(orderCode));
        orderRequest.setPayment(payResult.getPaymentID());
        Orders orderData = serviceCaller.call("order.getOrdersByCode", orderRequest, Orders.class);

        // 如果Order服务找不到订单,不会抛异常,返回null
        if (null == orderData || null == orderData.getId() || null == orderData.getUid()) {
            logger.error("[{}] no such order: payment:{}, orderData:{}", orderCode, payResult.getPaymentID(), orderData);
            // throw new Exception("无此订单.");
            throw new ServiceException(ServiceError.ORDER_DOES_NOT_EXIST);
        }

        if (orderData.getIsCancel().equals("Y") && orderData.getPaymentStatus().equals("N")) {
            logger.error("[{}] payment succeeded, but order has been canceled: payType:{}", orderCode, payResult.getPaymentID());
            throw new ServiceException(ServiceError.ORDER_PAY_CANCELED_AND_PAID); //"支付成功，但订单已取消，需联系客服");
        }

        // 校验订单金额, 比较到元
        double orderAmount = orderData.getAmount().doubleValue();
        double respTotalFee = payResult.getTotalFee();
        if (!YHMath.compare(orderAmount, respTotalFee)) {
            logger.error("[{}] amount mismatch! payed={}, order={}", orderCode, respTotalFee, orderAmount);
            throw new ServiceException(ServiceError.ORDER_PAY_AMOUNT_NOT_EQUAL); // "支付金额与订单金额不一致"
        }
        return orderData;
    }

    /**
     * 校验订单支付数据
     * @param orderData
     * @param payResult
     * @param logger
     * @throws Exception
     */
    protected void checkOrderPayData(Orders orderData, PayResult payResult, Logger logger) throws Exception {
    	logger.info("[{}] checkOrderPayData, paymentStatus:{}, isCancel:{}, status:{}, pament:{}, amount:{}", 
    			orderData.getOrderCode(), orderData.getPaymentStatus(), orderData.getIsCancel(), orderData.getStatus(), 
    			orderData.getPayment(), payResult.getTotalFee());
    	
    	//检查订单是否已取消
        if (orderData.getIsCancel().equals("Y") && orderData.getPaymentStatus().equals("N")) {
            logger.error("[{}] payment succeeded, but order has been canceled", orderData.getOrderCode());
            throw new ServiceException(ServiceError.ORDER_PAY_CANCELED_AND_PAID); //"支付成功，但订单已取消，需联系客服");
        }

        // 校验订单金额, 比较到元
        double orderAmount = orderData.getAmount().doubleValue();
        double respTotalFee = payResult.getTotalFee();
        if (!YHMath.compare(orderAmount, respTotalFee)) {
            logger.error("[{}] amount mismatch! payed={}, order={}", orderData.getOrderCode(), respTotalFee, orderAmount);
            throw new ServiceException(ServiceError.ORDER_PAY_AMOUNT_NOT_EQUAL); // "支付金额与订单金额不一致"
        }
        
        //logger.info("[{}] checkOrderPayData end", orderData.getOrderCode());
    }
    
    /**
     * 根据支付数据获取订单
     * @param payResult
     * @param logger
     * @return
     */
    protected Orders getOrder(PayResult payResult, Logger logger) {
    	long orderCode = 0;    	
    	try {
    		orderCode = Long.parseLong(payResult.getOrderCode());
		} catch (Exception e) {
		} 
    	
    	return getOrder(orderCode, logger);
    }
    
    /**
     * 根据订单号获取订单
     * @param orderCode
     * @param logger
     * @return
     */
    protected Orders getOrder(long orderCode, Logger logger) {
    	if(orderCode < 1){
    		logger.error("[{}] invalid orderCode", orderCode);
    		throw new ServiceException(ServiceError.ORDER_ORDER_CODE_IS_EMPTY);
    	}
    		
   		Orders orderData = null;
   		try {
   			orderData = orderPayService.getOrderByCode(orderCode);
   		} catch (Exception e) {
   			logger.error("[{}] getOrderByCode failed, ex: {}", orderCode, e.getMessage());
   			throw new ServiceException(ServiceError.ORDER_SERVICE_ERROR);
   		}
   		
        if (null == orderData) {
            logger.error("[{}] no such order", orderCode);
            throw new ServiceException(ServiceError.ORDER_DOES_NOT_EXIST);
        }

        return orderData;   	
    }
    
    /**
     * 修改订单支付方式（预支付调用）
     * @param orderCode
     * @param payment
     * @param logger
     */
    protected void updateOrdersPayment(long orderCode, byte payment, Logger logger) {
    	try {
    		orderPayService.updateOrdersPayment(orderCode, payment);
    		logger.info("[{}] update order payment: {}", orderCode, payment);
		} catch (Exception e) {
   			logger.error("[{}] order.updateOrdersPaymentByCode failed, ex: {}", orderCode, e.getMessage());
   			throw new ServiceException(ServiceError.ORDER_SERVICE_ERROR);
   		}
    }
    
    /**
     * 订单支付成功状态更新
     * @param orderData
     * @param payResult
     * @param logger
     */
    protected void orderPaySuccess(Orders orderData, PayResult payResult, Logger logger) {
    	try {
    		orderPayService.orderPaySuccess(orderData.getId(), payResult.getBankCode(), payResult.getPaymentID());
    		logger.info("[{}] update order payment status, payment: {}", payResult.getOrderCode(), payResult.getPaymentID());
		} catch (Exception e) {
   			logger.error("[{}] order.paySuccess failed, ex: {}", payResult.getOrderCode(), e.getMessage());
   			throw new ServiceException(ServiceError.ORDER_SERVICE_ERROR);
   		}
    } 

    
    /**
     * 订单中获取数据填充到支付结果数据
     * @param orderData
     * @param payResult
     * @param logger
     */
    protected void fillPayResultData(Orders orderData, PayResult payResult, Logger logger) {
        payResult.setUid(orderData.getUid());
        payResult.setOrderType(orderData.getOrderType());
        //走到回调，订单的payment属性应该有值才对。APP都是通过预支付接口修改，PC端都是通过app.SpaceOrders.updateOrdersPaymentByCode修改
        if(payResult.getPaymentID() == 0) {
        	payResult.setPaymentID(orderData.getPayment());
        }
    }

    /**
     * 支付成功后，清除各订单统计缓存
     * @param uid
     */
    protected void clearOrderCountCache(int uid) {
    	userOrderCache.clearOrderCountCache(uid);
    }
    
    /**
     * 写大数据日志
     * @param payResult
     */
    protected void logForBD(PayResult payResult) {

        JSONObject data = new JSONObject();
        data.put("order_code", payResult.getOrderCode());
        data.put("pay_amount", String.valueOf(payResult.getTotalFee()));
        data.put("pay_channel", String.valueOf(payResult.getPaymentID()));
        data.put("uid", String.valueOf(payResult.getUid()));
        data.put("call_time", payResult.getCallbackTime());
        data.put("pay_time", payResult.getPaymentTime());
        data.put("user_agent", "2");
        data.put("order_type", String.valueOf(payResult.getOrderType()));
        data.put("collect_ip", LocalIp.getLocalIp());
        data.put("service_key", "payment_log");

        loggerBD.info("{}", data);
    }

    public static Map<String, String> parseParams(Map<String, String[]> requestParams) {

        Map<String, String> params = new HashMap<String, String>();

        for (Map.Entry<String, String[]> entry : requestParams.entrySet()) {
            String name = entry.getKey();
            String[] values = entry.getValue();
            String valueStr = String.join(",", values);
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }
        return params;
    }

    public static void logRequestParams(HttpServletRequest request, Logger logger) {
        Enumeration params = request.getParameterNames();
        StringBuffer sb = new StringBuffer();
        
        while(params.hasMoreElements()){
            String paramName = (String)params.nextElement();
            sb.append(paramName);
            sb.append("=");
            sb.append(request.getParameter(paramName));
            sb.append(",");
            // System.out.println("Attribute Name - "+paramName+", Value - "+request.getParameter(paramName));
        }

        logger.debug("Request params: {}", sb.toString());
    }

//    /**
//     * 开关
//     * @return
//     */
//    private boolean isCreateERPOrderByMQ() {
//        if ("mq".equalsIgnoreCase(erpMessageSyncType)) {
//            return true;
//        }
//        return false;
//    }
    
    /**
     * 预支付获取订单并检测其支付状态
     * @param orderCode
     * @param logger
     * @return
     */
    Orders prepayCheck(long orderCode, Logger logger) {
    	Orders order = getOrder(orderCode, logger);
    	
        if(!isOrderPayable(order, logger)){
        	logger.error("[{}] Order is not payable", orderCode);
        	throw new ServiceException(ServiceError.ORDER_PAY_NOT_ALLOW);
        }
        
        return order;
    }
    
    /**
     * 检查订单是否可支付状态（当前是预支付接口调用）
     * @param Orders
     * @return
     */    
    protected boolean isOrderPayable(Orders orderData, Logger logger){
    	if(orderData == null){
    		return false;
    	}
    	logger.info("[{}] check order: status={}, paymentStatus={}, isCancel={}",
    			orderData.getOrderCode(), orderData.getStatus(), orderData.getPaymentStatus(), orderData.getIsCancel());
    	
    	if(orderData.getStatus() != 0 || "Y".equals(orderData.getPaymentStatus())){
    		logger.error("[{}] order has been paid", orderData.getOrderCode());
    		return false;
    	}
    	
    	if("Y".equals(orderData.getIsCancel())){
    		logger.error("[{}] order has been cancel", orderData.getOrderCode());
    		return false;
    	}
    	
    	//logger.info("order is payable, orderCode: {}", orderData.getOrderCode());
    	return true;
    }	
    
    
    /**
     * 生成支付事件
     * @param payType
     * @param orderCode
     * @param tradeNo
     * @param totalFee
     * @param tradeStatus
     * @return
     */
    protected PaymentEvent buildEvent(PayTypeEnum payType, String orderCode, String tradeNo, String totalFee, String tradeStatus) {
        return new PaymentEvent(payType.getName(), orderCode, tradeNo, totalFee, tradeStatus);
    }

    /**
     * 发布支付事件
     * @param event
     * @param eventType
     */
    protected void publishEvent(PaymentEvent event, PayEventEnum eventType) {
    	if(event == null)
    		return;
    	
    	publisher.publishEnvent(event, eventType);
    }
}
