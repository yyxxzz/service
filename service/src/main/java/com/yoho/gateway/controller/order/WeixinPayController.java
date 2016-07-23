package com.yoho.gateway.controller.order;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.error.ServiceError;
import com.yoho.error.event.PaymentEvent;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.payment.common.Constants;
import com.yoho.gateway.controller.order.payment.common.PayEventEnum;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.controller.order.payment.common.PayTypeEnum;
import com.yoho.gateway.controller.order.payment.common.TimeUtil;
import com.yoho.gateway.controller.order.payment.tenpay.handlers.WeixinRequestHandler;
import com.yoho.gateway.controller.order.payment.tenpay.util.ConstantUtil;
import com.yoho.gateway.controller.order.payment.tenpay.util.WXUtil;
import com.yoho.service.model.order.response.Orders;

@Controller
public class WeixinPayController extends AbstractController {
	private static final Logger logger = LoggerFactory.getLogger("wechatLogger"); 
	//private static final Logger loggerErr = LoggerFactory.getLogger("wechatLoggerErr");
	//private ApplicationEventPublisher publisher;
	
    @Value("${wechat.app.newnotifyurl}")
    private String notifyURL;
    /**
     * 微信支付接口
     * 根据调用者传入的orderCode,生成预支付Id和支付调用参数
     */
    @RequestMapping(value = "/payment/weixin_data", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse getWechatData(@RequestParam("order_code") Long orderCode,
                                     @RequestParam("app_key") String appKey,
                                     @RequestParam("payment_code") byte paymentCode) throws Exception {
    	
    	logger.info("\n\n\n******************** Prepay Data");
    	logger.info("[{}] prepay request, paymentCode: {}", orderCode, paymentCode);
    	
        Orders orderData = null;
        try {
        	orderData = prepayCheck(orderCode, logger);
            updateOrdersPayment(orderCode, paymentCode, logger);
        } catch (Exception e) {
        	logger.error("[{}] order prepay err: {}", orderCode, e.getMessage());
            return new ApiResponse.ApiResponseBuilder().code(500).message("当前订单不可支付").build();
        }
        
        //发送预支付请求
        WeixinRequestHandler handler = WeixinRequestHandler.build(orderData, notifyURL);
        WeixinRequestHandler.PrePayResult prepayResult = handler.sendPrepay();
        if(prepayResult == null || StringUtils.isEmpty(prepayResult.getPrepayId())){
        	logger.error("[{}] Failed to get prepayId", orderCode);
        	return new ApiResponse.ApiResponseBuilder().code(500).message("获取PrepayId失败").build();
        }
        
        //按照APP约定组织返回数据
        JSONObject sendData = getPrepayData(prepayResult);        
    	return new ApiResponse.ApiResponseBuilder().code(200).message("pay info").data(sendData).build();
    }	
    

    /**
     * 微信支付的回调入口
     */
    @RequestMapping(value = "/payment/weixin_notify", method = RequestMethod.POST)
    public void notifyWechatPayment(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("\n\n\n************************* Notify");

        WXNotifyResponsHandler responseHandler = new WXNotifyResponsHandler(response);

        String nofityBody = null;
        try{
        	nofityBody = WXUtil.getWXPayRequestBody(request);
        }catch(Exception e){
        	logger.error("get notify request failed: {}", e);
        	responseHandler.sendFailToCFT("参数获取异常");
        	return;
        }
        logger.info("notify params: {}", nofityBody);
        
        Map<String, String> notifyParamsMap = WXUtil.parseWXPayXml(nofityBody);
        if(notifyParamsMap == null || notifyParamsMap.size() == 0){
        	logger.error("parse notify request failed");
        	responseHandler.sendFailToCFT("参数解析异常");
        	return;
        }
        
		String returnCode = notifyParamsMap.get(ConstantUtil.WeixinPayConstants.RETURN_CODE);
		String resultCode =  notifyParamsMap.get(ConstantUtil.WeixinPayConstants.RETURN_RESULT_CODE);
        if(!ConstantUtil.WeixinPayConstants.PREPAY_RESULT_SUCCESS.equals(returnCode)){
        	logger.error("Weixin pay notify returnCode error: {}", nofityBody);
        	responseHandler.sendFailToCFT("通信失败");
        	return;
        }        
        
        PaymentEvent event = buildEvent(PayTypeEnum.WECAHT,  notifyParamsMap.get("out_trade_no"), notifyParamsMap.get("transaction_id"), 
        		notifyParamsMap.get("total_fee"), notifyParamsMap.get("result_code"));
        publishEvent(event, PayEventEnum.INIT);        
        
        //验证回调数据的签名
        if (!WXUtil.validSign(notifyParamsMap, ConstantUtil.getMchKey(notifyParamsMap.get(ConstantUtil.WeixinPayConstants.MCH_ID)))) {
        	publishEvent(event, PayEventEnum.VER_FAILED); 
        	logger.error("[{}] sign validate failed", notifyParamsMap.get("out_trade_no"));
            responseHandler.sendFailToCFT("验证签名失败"); // 返回结果给微信
            return;
        }                
                
        if(!ConstantUtil.WeixinPayConstants.PREPAY_RESULT_SUCCESS.equals(resultCode)){
        	publishEvent(event, PayEventEnum.PROCESS_FAILED);
        	logger.error("Weixin pay notify trade failed: {}", nofityBody);
        	responseHandler.sendFailToCFT("交易失败");
        	return;
        }
 
        // 支付成功处理流程
        PayResult payResult = null;
        try {
        	payResult = getPayResult(notifyParamsMap);
        	notifyProcess(payResult, logger);
        } catch (Exception e) {
        	publishEvent(event, PayEventEnum.PROCESS_FAILED);
        	logger.error("[{}] ServiceEx error: {}", payResult.getOrderCode(), e.getMessage());
            // 更新状态失败,让微信重新发送通知
            responseHandler.sendFailToCFT("商户内部处理异常");;
            return;
        }

        publishEvent(event, PayEventEnum.SUCCESS);
        logger.info("[{}] reply success to weixinpay", payResult.getOrderCode());  
        responseHandler.sendSuccessToCFT();  // 返回结果给微信
    }

    /**
     * 根据支付回调参数组装支付数据
     * @param params
     * @param orderCode
     * @return
     */
    private PayResult getPayResult(Map<String, String> notifyParamsMap) {
        String out_no_raw = notifyParamsMap.get("out_trade_no");
        String out_trade_no;
        // 把 YOHOBuy_XXXX 形式的订单号转换为 XXXX
        try {
            String[] sp = out_no_raw.split("_");
            out_trade_no = sp[1];
        } catch (Exception e) {
            throw new ServiceException(ServiceError.ORDER_ORDER_CODE_IS_EMPTY);
        }

        PayResult payResult = new PayResult();

        payResult.setOrderCode(out_trade_no);
        //微信扫码支付也走这个接口，故这里不能写死
        //payResult.setPaymentID(Constants.WECHAT_CODE);  // 微信支付的代码
        payResult.setPaymentResult(200);
        payResult.setPaymentTime(TimeUtil.formatTime(notifyParamsMap.get("time_end")));
        payResult.setCallbackTime(TimeUtil.getCurrentTime());

        double total_fee = Double.parseDouble(notifyParamsMap.get("total_fee"));
        payResult.setTotalFeeInCent(total_fee);
        payResult.setBankCode(notifyParamsMap.get("bank_type"));  // "bank_type"
        payResult.setBankName(notifyParamsMap.get("bank_type"));  // "bank_type"
        payResult.setTradeNo(notifyParamsMap.get("transaction_id"));    
        
        return payResult;
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
    	if(isWechatPaymentType(orderData.getPayment())) {
    		payResult.setPaymentID(orderData.getPayment());
    	}
    	else{
    		payResult.setPaymentID(Constants.WECHAT_CODE);
    		logger.info("[{}] order payment type default: {}", orderData.getOrderCode(), payResult.getPaymentID());
    	}    	
    } 
    
    /**
     *	判断支付方式是否微信支付类型
     **/
    private boolean isWechatPaymentType(byte payment) {
    	if(payment == Constants.WECHAT_CODE
	    	|| payment == Constants.WECHATQR_CODE		
	    	|| payment == Constants.WECHATWAP_CODE) 
    	{
    		return true;
    	}
    	
    	return false;
    }
	
	/**
	 * 组织预支付返回给APP的数据
	 */
	private JSONObject getPrepayData(WeixinRequestHandler.PrePayResult prepayResult){
		String timeStamp = WXUtil.getTimeStamp();
		
		Map<String, String> prepaySignMap = new HashMap<String, String>();
		prepaySignMap.put("appid", ConstantUtil.APP_ID);
		//prepaySignMap.put("appkey", ConstantUtil.APP_KEY);
		prepaySignMap.put("noncestr", prepayResult.getNonceStr());
		prepaySignMap.put("package", "Sign=WXPay");
		prepaySignMap.put("partnerid", ConstantUtil.PARTNER);
		prepaySignMap.put("prepayid", prepayResult.getPrepayId());
		prepaySignMap.put("timestamp", timeStamp);        
		//对上述数据进行签名
		String sign = WXUtil.signMd5(prepaySignMap, ConstantUtil.PARTNER_KEY);
		
		// 重新组织数据格式
		Map<String, Object> prePayData = new LinkedHashMap<>();
		prePayData.put("appid", ConstantUtil.APP_ID);
		prePayData.put("partnerid", ConstantUtil.PARTNER);
		prePayData.put("prepayid", prepayResult.getPrepayId());
		prePayData.put("package", "Sign=WXPay");
		prePayData.put("noncestr", prepayResult.getNonceStr());
		prePayData.put("timestamp", timeStamp);
		prePayData.put("sign", sign);
		
		JSONObject sendData = new JSONObject();
		sendData.put("prePayUrl", ConstantUtil.WeixinPayConstants.WEIXIN_PREPAY_URL);	//实际上没用，为兼容APP，暂且保留
		sendData.put("token", "xxxxx"); //实际上没用，为兼容APP，暂且保留
		sendData.put("prePayData", prePayData);        	
		
		return sendData;
	}
	
	/**
	 * 返回处理结果给财付通服务器。
	 * @param msg: Success or fail。
	 * @throws IOException 
	 */	
	private static class WXNotifyResponsHandler{
		private HttpServletResponse response;
		
		public WXNotifyResponsHandler(HttpServletResponse response){
			this.response = response;
		}
		
		//返回成功
		public void sendSuccessToCFT() throws IOException {
			Map<String, String> respParams = new HashMap<String, String>();
			respParams.put(ConstantUtil.WeixinPayConstants.RETURN_CODE, ConstantUtil.WeixinPayConstants.PREPAY_RESULT_SUCCESS);
			respParams.put(ConstantUtil.WeixinPayConstants.RETURN_MSG, "");
			PrintWriter out = response.getWriter();
			out.println(WXUtil.createWXPayXml(respParams));
			out.flush();
			out.close();
		}
		
		//返回失败
		public void sendFailToCFT(String msg) throws IOException {
			Map<String, String> respParams = new HashMap<String, String>();
			respParams.put(ConstantUtil.WeixinPayConstants.RETURN_CODE, ConstantUtil.WeixinPayConstants.PREPAY_RESULT_FAIL);
			respParams.put(ConstantUtil.WeixinPayConstants.RETURN_MSG, msg);
			PrintWriter out = response.getWriter();
			out.println(WXUtil.createWXPayXml(respParams));
			out.flush();
			out.close();
		}

	}

}
