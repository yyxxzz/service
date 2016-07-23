package com.yoho.gateway.controller.order.payment.tenpay.handlers;

import com.alibaba.fastjson.JSONException;
import com.yoho.gateway.controller.order.payment.tenpay.client.TenpayHttpClient;
import com.yoho.gateway.controller.order.payment.tenpay.util.ConstantUtil;
import com.yoho.gateway.controller.order.payment.tenpay.util.JsonUtil;
import com.yoho.gateway.controller.order.payment.tenpay.util.Sha1Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PrepayIdRequestHandler extends RequestHandler {

	private static final Logger logger = LoggerFactory.getLogger("wechatLogger");
	private static final Logger loggerErr = LoggerFactory.getLogger("wechatLoggerErr");

	public PrepayIdRequestHandler(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	/**
	 * 创建签名SHA1
	 * 
	 * @return
	 * @throws Exception
	 */
	public String createSHA1Sign() {
		StringBuffer sb = new StringBuffer();
		Set es = super.getAllParameters().entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			sb.append(k + "=" + v + "&");
		}
		String params = sb.substring(0, sb.lastIndexOf("&"));
		String appsign = Sha1Util.getSha1(params);
		// this.setDebugInfo(this.getDebugInfo() + "\r\n" + "sha1 sb:" + params);
		// this.setDebugInfo(this.getDebugInfo() + "\r\n" + "app sign:" + appsign);

		logger.debug("createSHA1Sign SHA1 PreSign: {}", params);
		logger.debug("createSHA1Sign APP SIGN: {}", appsign);

		return appsign;
	}

	// 提交预支付
	public String sendPrepay() throws JSONException {
		String prepayid = "";
		StringBuffer sb = new StringBuffer("{");
		Set es = super.getAllParameters().entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"appkey".equals(k)) {
				sb.append("\"" + k + "\":\"" + v + "\",");
			}
		}
		String params = sb.substring(0, sb.lastIndexOf(","));
		params += "}";

		String requestUrl = super.getGateUrl();
		logger.info("SendPrepay requestUrl: {}", requestUrl);
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setReqContent(requestUrl);
		String resContent = "";
		logger.info("SendPrepay post data: {}", params);
		if (httpClient.callHttpPost(requestUrl, params)) {
			resContent = httpClient.getResContent();
			if (2 == resContent.indexOf("prepayid")) {
				prepayid = JsonUtil.getJsonValue(resContent, "prepayid");
			}
			logger.info("SendPrepay resContent: {}", resContent);
		}
		return prepayid;
	}

	// 判断access_token是否失效
	public String sendAccessToken() {
		String accesstoken = "";
		StringBuffer sb = new StringBuffer("{");
		Set es = super.getAllParameters().entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"appkey".equals(k)) {
				sb.append("\"" + k + "\":\"" + v + "\",");
			}
		}
		String params = sb.substring(0, sb.lastIndexOf(","));
		params += "}";

		String requestUrl = super.getGateUrl();
		logger.debug("SendAccessToken requestUrl: {}", requestUrl);
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setReqContent(requestUrl);
		String resContent = "";
		logger.debug("SendAccessToken post date: {}", params);
		if (httpClient.callHttpPost(requestUrl, params)) {
			resContent = httpClient.getResContent();
			logger.debug("SendAccessToken received: {}", resContent);
			if (2 == resContent.indexOf(ConstantUtil.ERRORCODE)) {
				accesstoken = resContent.substring(11, 16);//获取对应的errcode的值
			}
		}
		return accesstoken;
	}
}
