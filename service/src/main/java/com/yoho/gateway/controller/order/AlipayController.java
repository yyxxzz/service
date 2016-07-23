package com.yoho.gateway.controller.order;

import com.google.common.base.Strings;
import com.yoho.error.event.PaymentEvent;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.payment.alipay.AlipayNotify;
import com.yoho.gateway.controller.order.payment.common.Constants;
import com.yoho.gateway.controller.order.payment.common.PayEventEnum;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.controller.order.payment.common.PayTypeEnum;
import com.yoho.service.model.order.response.Orders;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by ming on 16/1/19.
 * 接收支付宝交易异步通知
 */
@Controller
public class AlipayController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger("alipayLogger");
    //private static final Logger loggerErr = LoggerFactory.getLogger("alipayLoggerErr");

    /**
     * 支付宝通知回调
     */
    @RequestMapping(value = "/payment/alipay_notify", method = RequestMethod.POST)
    public void notifyAliPayment(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("\n\n\n************************* Notify");

        PaymentEvent event = buildEvent(PayTypeEnum.ALIPAY,  request.getParameter("out_trade_no"),
                request.getParameter("trade_no"), request.getParameter("total_fee"),request.getParameter("trade_status"));
        publishEvent(event, PayEventEnum.INIT);

        String out_trade_no = request.getParameter("out_trade_no");
        logger.info("[{}] notification received", out_trade_no);

        Map<String, String> params = parseParams(request.getParameterMap());
        logger.info("Request params: {}", params);

        //非TRADE_SUCCESS回调，直接忽略
        if(!isTradeSuccessNotify(params, out_trade_no)){
        	logger.info("[{}] not TRADE_SUCCESS notify, ignore it", out_trade_no);
            response.getWriter().print("success");
            return;
        }

        //回调合法性验证
        if (!AlipayNotify.verify(params)) {
            publishEvent(event, PayEventEnum.VER_FAILED);
            logger.error("[{}] verification failed", out_trade_no);
            response.getWriter().print("failed");
            return;
        }

        //处理回调
        try {
        	PayResult payResult = getPayResult(params, out_trade_no);
        	
        	notifyProcess(payResult, logger);
        	
        } catch (Exception e) {
            publishEvent(event, PayEventEnum.PROCESS_FAILED);
            logger.error("[{}] ServiceEx error: {}", out_trade_no, e.getMessage());
            response.getWriter().print("failed");
            return;
        }

        publishEvent(event, PayEventEnum.SUCCESS);
        logger.info("[{}] reply success to alipay", out_trade_no);        
        response.getWriter().print("success");
    }

    /**
     * 支付宝预支付（支付宝的流程本身没有预支付）
     * 在这个流程中，检查订单状态是否正常
     */    
    @RequestMapping(value = "/payment/alipay_data", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse getAlipayData(@RequestParam("order_code") Long orderCode,
            						 @RequestParam("payment_code") byte paymentCode){
    	logger.info("\n\n\n******************** Prepay Data");
    	logger.info("[{}] prepay request, payment: {}", orderCode, paymentCode);

        try {
        	prepayCheck(orderCode, logger);
            updateOrdersPayment(orderCode, paymentCode, logger);
        } catch (Exception e) {
        	logger.error("[{}] order prepay err: {}", orderCode, e.getMessage());
            return new ApiResponse.ApiResponseBuilder().code(500).message("当前订单不可支付").build();
        }

        logger.info("[{}] prepay end", orderCode);
    	return new ApiResponse.ApiResponseBuilder().code(200).message("success").build();
    }    

    /**
     * 判断是否TRADE_SUCCESS回调，只有这类回调才需要继续处理
     * @param params
     * @param orderCode
     * @return
     */
    private boolean isTradeSuccessNotify(Map<String, String> params, String orderCode){
    	if(params == null) {
    		return false;
    	}
        //交易状态
        String trade_status = params.get("trade_status");
        if(Strings.isNullOrEmpty(trade_status)) {
        	return false;
        }

        if (!trade_status.equals("TRADE_SUCCESS")) {
            logger.info("[{}] trade_status: {}", orderCode, trade_status);
            return false;
        }

        if (!Strings.isNullOrEmpty(params.get("refund_status"))) {
            logger.info("[{}] refund_status: {}", orderCode, params.get("refund_status"));
            return false;
        }

        return true;
    }
    
    /**
     * 根据支付回调参数组装支付数据
     * @param params
     * @param orderCode
     * @return
     */
    private PayResult getPayResult(Map<String, String> params, String orderCode) {
    	
        PayResult payResult = new PayResult();
        payResult.setOrderCode(orderCode);
        
        //注意：PC端支付宝也走这个回调接口，故这里不能设置死
        //payResult.setPaymentID(Constants.ALIPAY_CODE);  // 支付宝的代码

        double total_fee = Double.parseDouble(params.get("total_fee"));
        payResult.setTotalFeeInYuan(total_fee);
        payResult.setBankCode("");
        payResult.setBankName("");
        payResult.setPaymentResult(convertResult(params.get("trade_status")));
        payResult.setPaymentTime(null == params.get("gmt_payment") ? "" : params.get("gmt_payment"));
        payResult.setCallbackTime(null == params.get("notify_time") ? "" : params.get("notify_time"));
        payResult.setResultMsg(params.get("notify_type"));
        payResult.setPayOrderCode(orderCode);
        payResult.setTradeNo(params.get("trade_no"));	//支付宝交易号
        payResult.setBankBillNo("");
        
        return payResult;
    }
    
    private static int convertResult(String resultCode) {
        if ("TRADE_SUCCESS".equals(resultCode)) {
            return 200;
        }
        return 400;
    }
    
    /**
     * 订单中获取数据填充到支付结果
     * @param orderData
     * @param payResult
     * @param logger
     */
    protected void fillPayResultData(Orders orderData, PayResult payResult, Logger logger) {
    	super.fillPayResultData(orderData, payResult, logger);
    	
    	// 对于4.3版本以前的APP，支付宝没有预支付，因此没有修改订单支付方式
    	// 如果用户先选择了其他支付方式再选择支付宝，最终的支付渠道可能是错误的。
    	// 以上情况，默认设置为支付宝APP方式
    	if(isAliPaymentType(orderData.getPayment())) {
    		payResult.setPaymentID(orderData.getPayment());
    	}
    	else{
    		payResult.setPaymentID(Constants.ALIPAY_CODE);
    		logger.info("[{}] order payment type default: {}", orderData.getOrderCode(), payResult.getPaymentID());
    	}    	
    }    
    
    /**
     *	判断支付方式是否支付宝类 
     **/
    private boolean isAliPaymentType(byte payment) {
    	if(payment == Constants.ALIPAY_CODE
	    	|| payment == Constants.ALIPAY_PC_CODE		
	    	|| payment == Constants.ALIPAY_BANK
	    	|| payment == Constants.ALIPAY_WAP) 
    	{
    		return true;
    	}
    	
    	return false;
    }
    
}
