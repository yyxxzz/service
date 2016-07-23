package com.yoho.gateway.controller.order.payment.tenpay.handlers;

import com.yoho.gateway.controller.order.payment.tenpay.client.TenpayHttpClient;
import com.yoho.gateway.controller.order.payment.tenpay.util.ConstantUtil;
import com.yoho.gateway.controller.order.payment.tenpay.util.JsonUtil;
import com.yoho.gateway.controller.order.payment.tenpay.util.WXUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AccessTokenRequestHandler extends RequestHandler {

	private static final Logger logger = LoggerFactory.getLogger("wechatLogger");

	public AccessTokenRequestHandler(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
	}

	private static String access_token = "";

	/**
	 * 获取凭证access_token
	 * @return
	 */
	public static String getAccessToken() {
		if ("".equals(access_token)) {// 如果为空直接获取
			logger.info("No access_token, fetching");
			return getTokenReal();
		}

		if (tokenIsExpire(access_token)) {// 如果过期重新获取
			logger.info("token expired, refetching");
			return getTokenReal();
		}
		return access_token;
	}

	/**
	 * 实际获取access_token的方法
	 * @return
	 */
	protected static String getTokenReal() {
		String requestUrl = ConstantUtil.TOKENURL + "?grant_type=" + ConstantUtil.GRANT_TYPE + "&appid="
				+ ConstantUtil.APP_ID + "&secret=" + ConstantUtil.APP_SECRET;
		String resContent = "";
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setMethod("GET");
		httpClient.setReqContent(requestUrl);
		if (httpClient.call()) {
			resContent = httpClient.getResContent();
			if (resContent.indexOf(ConstantUtil.ACCESS_TOKEN) > 0) {
				access_token = JsonUtil.getJsonValue(resContent, ConstantUtil.ACCESS_TOKEN);
			} else {
				logger.error("GET access_token return error!" + httpClient.getErrInfo());
			}
		} else {
			logger.error("Get access_token communication failed! Rescode: {}, Err info: {}", httpClient.getResponseCode(), httpClient.getErrInfo());
			// 有可能因为网络原因，请求已经处理，但未收到应答。
		}

		return access_token;
	}

	/**
	 * 判断传递过来的参数access_token是否过期
	 * @return
	 */
	public static boolean tokenIsExpire(String token) {
		boolean flag = false;
		PrepayIdRequestHandler wxReqHandler = new PrepayIdRequestHandler(null, null);
		wxReqHandler.setParameter("appid", ConstantUtil.APP_ID);
		wxReqHandler.setParameter("appkey",ConstantUtil.APP_KEY);
		wxReqHandler.setParameter("noncestr", WXUtil.getNonceStr());
		wxReqHandler.setParameter("package", ConstantUtil.packageValue);
		wxReqHandler.setParameter("timestamp", WXUtil.getTimeStamp());
		wxReqHandler.setParameter("traceid", ConstantUtil.traceid);

		// 生成支付签名
		String sign = wxReqHandler.createSHA1Sign();
		wxReqHandler.setParameter("app_signature", sign);
		wxReqHandler.setParameter("sign_method", ConstantUtil.SIGN_METHOD);
		String gateUrl = ConstantUtil.GATEURL + token;
		wxReqHandler.setGateUrl(gateUrl);

		// 发送请求
		String accesstoken = wxReqHandler.sendAccessToken();
		if (ConstantUtil.EXPIRE_ERRCODE.equals(accesstoken) || ConstantUtil.FAIL_ERRCODE.equals(accesstoken)) {
			flag = true;
			logger.debug("AccessToken ErrCode: {}", accesstoken);
		}
		return flag;
	}

}
