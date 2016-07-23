package com.yoho.gateway.controller.order;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.error.event.PaymentEvent;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.payment.common.PayEventEnum;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.controller.order.payment.common.PayTypeEnum;
import com.yoho.gateway.controller.order.payment.common.TimeUtil;

@Controller
public class PcPayController extends AbstractController{
    private static final Logger logger = LoggerFactory.getLogger("pcpayLogger");
    //private static final Logger loggerErr = LoggerFactory.getLogger("pcpayLoggerErr");
	
    /**
     * 支付宝虚拟预支付（支付宝的流程本身没有预支付）
     * 在这个流程中，检查订单状态是否正常
     */    
    @RequestMapping(params = "method=web.SpaceOrders.pcpayNotify")
    @ResponseBody
    public ApiResponse notifyPcPayment(@RequestParam("order_code") String orderCode,
							           @RequestParam("payment") Byte payment,
							           @RequestParam("bank_name") String bankName,
							           @RequestParam("bank_code") String bankCode,
							           @RequestParam("amount") String amount,
							           @RequestParam("trade_no") String tradeNo,
							           @RequestParam("bank_bill_no") String bankBillNo){
    	logger.info("\n\n\n************************* Notify");
        logger.info("[{}] notification received", orderCode);        
        
        StringBuilder reqParams = new StringBuilder();
        reqParams.append("order_code").append("=").append(orderCode).append("&")
        		.append("payment").append("=").append(payment).append("&")
        		.append("bank_name").append("=").append(bankName).append("&")
        		.append("bank_code").append("=").append(bankCode).append("&")
        		.append("amount").append("=").append(amount).append("&")
        		.append("trade_no").append("=").append(tradeNo).append("&")
        		.append("bank_bill_no").append("=").append(bankBillNo);
        
        logger.info("Request params: {}", reqParams);

        PaymentEvent event = buildEvent(PayTypeEnum.PCPAY,  orderCode,
        		tradeNo, amount, "TRADE_SUCCESS");        
        publishEvent(event, PayEventEnum.INIT);

        String currentTime = TimeUtil.getCurrentTime();
        
        PayResult payResult = new PayResult();
        payResult.setOrderCode(orderCode);
        payResult.setPaymentID(payment);  
        payResult.setTotalFeeInYuan(Double.parseDouble(amount));
        payResult.setBankCode(bankCode);
        payResult.setBankName(bankName);
        payResult.setPaymentResult(200);
        payResult.setPaymentTime(currentTime);
        payResult.setCallbackTime(currentTime);
        payResult.setResultMsg("trade_status_sync");
        payResult.setPayOrderCode(orderCode);
        payResult.setTradeNo(orderCode);
        payResult.setBankBillNo(bankBillNo);

        try {
        	notifyProcess(payResult, logger);
        } catch (Exception e) {
        	publishEvent(event, PayEventEnum.PROCESS_FAILED);        	
        	logger.error("[{}] Pcpay nofity process failed, ex: {}", orderCode, e);
            return new ApiResponse.ApiResponseBuilder().code(500).message(e.getMessage()).build();
        }
        
        publishEvent(event, PayEventEnum.SUCCESS);  
        logger.info("[{}] notify process end", orderCode);
        
    	return new ApiResponse.ApiResponseBuilder().code(200).message("success").build();
    }	
    
}
