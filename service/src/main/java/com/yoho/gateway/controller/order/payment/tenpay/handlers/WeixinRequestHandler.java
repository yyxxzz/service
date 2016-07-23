package com.yoho.gateway.controller.order.payment.tenpay.handlers;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yoho.gateway.controller.order.payment.common.TimeUtil;
import com.yoho.gateway.controller.order.payment.tenpay.client.TenpayHttpClient;
import com.yoho.gateway.controller.order.payment.tenpay.util.ConstantUtil;
import com.yoho.gateway.controller.order.payment.tenpay.util.WXUtil;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.service.model.order.response.Orders;


public class WeixinRequestHandler {
	private static final Logger logger = LoggerFactory.getLogger("wechatLogger"); 
	private static final Logger loggerErr = LoggerFactory.getLogger("wechatLoggerErr");
	
	public class PrePayResult{
		
		private String prepayId = "";
		private String nonceStr = "";

		public String getPrepayId() {
			return prepayId;
		}
		public void setPrepayId(String prepayId) {
			this.prepayId = prepayId;
		}
		public String getNonceStr() {
			return nonceStr;
		}
		public void setNonceStr(String nonceStr) {
			this.nonceStr = nonceStr;
		}
	}
	
	//请求参数
	private Map<String, String> parameters = new HashMap<String, String>();
	
	private WeixinRequestHandler(){
		
	}
	
	/**
	 * 生成WeixinRequestHandler对象
	 */
	public static WeixinRequestHandler build(Orders orders, String notifyUrl)
	{
		if(orders == null || notifyUrl == null){
			return null;
		}
		
		if(orders.getOrderCode() <= 0){
			return null;
		}
		
		WeixinRequestHandler request = new WeixinRequestHandler();
		request.setParameter(ConstantUtil.WeixinPayConstants.APPID, ConstantUtil.APP_ID);
		request.setParameter(ConstantUtil.WeixinPayConstants.MCH_ID, ConstantUtil.PARTNER);
		request.setParameter(ConstantUtil.WeixinPayConstants.NONCE_STR, WXUtil.getNonceStr());
		request.setParameter(ConstantUtil.WeixinPayConstants.BODY, "订单号:" + orders.getOrderCode());
		request.setParameter(ConstantUtil.WeixinPayConstants.OUT_TRADE_NO, "YOHOBuy_" + String.valueOf(orders.getOrderCode()));
		request.setParameter(ConstantUtil.WeixinPayConstants.TOTAL_FEE, String.valueOf(orders.getAmount().multiply(new BigDecimal("100")).intValue()));
		//"X-Real-IP"可能出现"10.41.100.248, 120.197.194.125"这样的数据，微信支付会报错。需要截取一下
		request.setParameter(ConstantUtil.WeixinPayConstants.SPBILL_CREATE_IP, getSingleIp(RemoteIPInterceptor.getRemoteIP()));
		request.setParameter(ConstantUtil.WeixinPayConstants.NOTIFY_URL, notifyUrl);
		request.setParameter(ConstantUtil.WeixinPayConstants.TRADE_TYPE, "APP");
		request.setParameter(ConstantUtil.WeixinPayConstants.TIME_EXPIRE, TimeUtil.getExpireTime(orders.getCreateTime()));
		//md5签名
		request.setParameter(ConstantUtil.WeixinPayConstants.SIGN, WXUtil.signMd5(request.getParameters(), ConstantUtil.PARTNER_KEY));
		
		return request;
	}

	/**
	 * 发送预支付请求
	 * @param void
	 * @return String 
	 */	
	public PrePayResult sendPrepay(){
	
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setCharset(ConstantUtil.WeixinPayConstants.DEFAULT_CHARACTER_SET);
		String requestContent = WXUtil.createWXPayXml(this.getParameters());
		logger.info("SendPrepay request content xml: {}", requestContent);
		
		if (httpClient.callHttpPost(ConstantUtil.WeixinPayConstants.WEIXIN_PREPAY_URL, requestContent)
				== false) {
			loggerErr.error("SendPrepay http post request failed, request content: {}", requestContent);
			return null;
		}
		String responseContent = httpClient.getResContent();
		logger.info("SendPrepay response content: {}", responseContent);
		//返回结果xml解析
		Map<String, String> reponseMap = WXUtil.parseWXPayXml(responseContent);
		if(null == reponseMap || reponseMap.size() == 0){
			loggerErr.error("Prepay response content parse error: {}", responseContent);
			return null;
		}

		String returnCode = reponseMap.get(ConstantUtil.WeixinPayConstants.RETURN_CODE);
		String resultCode =  reponseMap.get(ConstantUtil.WeixinPayConstants.RETURN_RESULT_CODE);

		if(ConstantUtil.WeixinPayConstants.PREPAY_RESULT_SUCCESS.equals(returnCode))
		{	
			//验证签名
			if(!WXUtil.validSign(reponseMap, ConstantUtil.PARTNER_KEY)){
				loggerErr.error("Prepay response valid sign failed: {}", responseContent);
				return null;
			}
			//return_code和result_code都为SUCCESS时，返回prepayId			
			if(ConstantUtil.WeixinPayConstants.PREPAY_RESULT_SUCCESS.equals(resultCode)){
				PrePayResult prepayResult = new PrePayResult();
				prepayResult.setPrepayId(reponseMap.get(ConstantUtil.WeixinPayConstants.RETURN_PREPAY_ID));
				//prepayResult.setNonceStr(reponseMap.get(ConstantUtil.WeixinPayConstants.RETURN_NONCE_STR));
				
				//不传返回的Nonce，传预支付请求时生成的那个Nonce
				prepayResult.setNonceStr(this.getParameter(ConstantUtil.WeixinPayConstants.NONCE_STR));
				return prepayResult;
			}
		}

		loggerErr.error("SendPrepay obtain prepayId failed: {}", responseContent);
		return null;
	}
	
	/**
	 * 获取参数值
	 * @param parameter 参数名称
	 * @return String 
	 */
	public String getParameter(String parameter) {
		String s = this.parameters.get(parameter); 
		return (null == s) ? "" : s;
	}
	
	/**
	 * 设置参数值
	 * @param parameter 参数名称
	 * @param parameterValue 参数值
	 */
	public void setParameter(String parameter, String parameterValue) {
		String v = "";
		if(null != parameterValue) {
			v = parameterValue.trim();
		}
		this.parameters.put(parameter, v);
	}

	//------getter, setter----------
	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}

	
	public static String getSingleIp(String srcIp) {
		if(srcIp == null) 
			return "";
		String ips[] = srcIp.split(",");
		return ips[ips.length - 1].trim();
	}
}
