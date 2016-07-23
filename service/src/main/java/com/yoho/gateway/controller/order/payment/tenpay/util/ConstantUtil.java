package com.yoho.gateway.controller.order.payment.tenpay.util;

import java.util.HashMap;
import java.util.Map;

public class ConstantUtil {
	/**
	 * 商家可以考虑读取配置文件
	 */
	// {"app_id":"wx049fdaa3ba9cdd7a",
	// "app_secret":"f973fdb412307ea7b97d0252fd675104",
	// "partner_key":"b22de5cfd0ded341e0516505f72649a9",
	// "pay_sign_key":"wGwAsgU5SeeM62glYaoC6ALBKhtOrF7Ek9LzE8trEuUG7jHeFdnSlyA1jblOYYS57QzWr8dYVsWGdeWhzeonnrKFZakgwFWPYVtyeP4XqSu9Qvxps8LEgxoFBEpRPm6C",
	// "partner_id":1218934901}
	
	/**微信商户号1，当前微信APP支付、H5使用这个商户号**/
	public static String APP_ID = "wx049fdaa3ba9cdd7a"; // "wxd930ea5d5a258f4f";//微信开发平台应用id
	public static String APP_SECRET = "f973fdb412307ea7b97d0252fd675104"; //"db426a9829e4b49a0dcac7b4162da6b6";//应用对应的凭证
	//应用对应的密钥
	public static String APP_KEY = "wGwAsgU5SeeM62glYaoC6ALBKhtOrF7Ek9LzE8trEuUG7jHeFdnSlyA1jblOYYS57QzWr8dYVsWGdeWhzeonnrKFZakgwFWPYVtyeP4XqSu9Qvxps8LEgxoFBEpRPm6C"; // "L8LrMqqeGRxST5reouB0K66CaYAWpqhAVsq7ggKkxHCOastWksvuX1uvmvQclxaHoYd3ElNBrNO2DHnnzgfVG9Qs473M3DTOZug5er46FhuGofumV8H2FVR9qkjSlC5K";
	public static String PARTNER = "1218934901"; // "1900000109";//财付通商户号
	public static String PARTNER_KEY = "b22de5cfd0ded341e0516505f72649a9"; // "8934e7d15453e97507ef794cf7b0519d";//商户号对应的密钥
	public static String TOKENURL = "https://api.weixin.qq.com/cgi-bin/token";//获取access_token对应的url
	public static String GRANT_TYPE = "client_credential";//常量固定值 
	public static String EXPIRE_ERRCODE = "42001";//access_token失效后请求返回的errcode
	public static String FAIL_ERRCODE = "40001";//重复获取导致上一次获取的access_token失效,返回错误码
	public static String GATEURL = "https://api.weixin.qq.com/pay/genprepay?access_token=";//获取预支付id的接口url
	public static String ACCESS_TOKEN = "access_token";//access_token常量值
	public static String ERRORCODE = "errcode";//用来判断access_token是否失效的值
	public static String SIGN_METHOD = "sha1";//签名算法常量值
	//package常量值
	//public static String packageValue = "bank_type=WX&body=%B2%E2%CA%D4&fee_type=1&input_charset=GBK&notify_url=http%3A%2F%2F127.0.0.1%3A8180%2Ftenpay_api_b2c%2FpayNotifyUrl.jsp&out_trade_no=2051571832&partner=1900000109&sign=10DA99BCB3F63EF23E4981B331B0A3EF&spbill_create_ip=127.0.0.1&time_expire=20131222091010&total_fee=1";
	public static String packageValue = "bank_type=WX&body=%E8%AE%A2%E5%8D%95%E5%8F%B7%3A1619199705&fee_type=1&input_charset=UTF-8&notify_url=http%3A%2F%2Fdevservice.yoho.cn%3A58077%2Fpayment%2Fwechat_notify&out_trade_no=YOHOBuy_1619199705&partner=1218934901&sign=1E967995AA1F2E5DB5B03969ADEA2FA0&spbill_create_ip=172.16.8.137&time_expire=20160121181546&total_fee=239900";
	public static String traceid = "testtraceid001";//测试用户id
	
	/**微信商户号2，当前微信扫码使用这个商户号**/
	public static String APP_ID_2 = "wx75e5a7c0c88e45c2";
	public static String APP_SECRET_2 = "ce21ae4a3f93852279175a167e54509b"; 
	public static String PARTNER_2 = "1227694201";
	public static String PARTNER_KEY_2 = "7e6f3307b64cc87c79c472814b88f7fb";
	
	private static final Map<String, String> MCH_KEY_MAP = new HashMap<String, String>();
	static {
		MCH_KEY_MAP.put(PARTNER, PARTNER_KEY);
		MCH_KEY_MAP.put(PARTNER_2, PARTNER_KEY_2);
	}
	
	//微信支付新API相关常量定义
	public class WeixinPayConstants{
		//微信预支付URL（新API）
		public static final String WEIXIN_PREPAY_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";		
		//用于签名的商户密钥字段名
		public static final String KEY = "key";
		
		public static final String DEFAULT_CHARACTER_SET = "UTF-8";
		
		//-------统一下单接口字段----------------------------------------
		//应用ID
		public static final String APPID = "appid";
		//商户号
		public static final String MCH_ID = "mch_id";
		//随机字符串
		public static final String NONCE_STR = "nonce_str";
		//签名
		public static final String SIGN = "sign";
		//商品描述
		public static final String BODY = "body";
		//商户订单号
		public static final String OUT_TRADE_NO = "out_trade_no";
		//总金额
		public static final String TOTAL_FEE = "total_fee";
		//终端IP
		public static final String SPBILL_CREATE_IP = "spbill_create_ip";
		//通知地址
		public static final String NOTIFY_URL = "notify_url";
		//交易类型
		public static final String TRADE_TYPE = "trade_type";
		//订单失效时间
		public static final String TIME_EXPIRE = "time_expire";
		
		//-------统一下单接口返回字段----------------------------------------
		public static final String RETURN_CODE = "return_code";
		
		public static final String RETURN_MSG = "return_msg";
		
		public static final String RETURN_RESULT_CODE = "result_code";
		
		public static final String RETURN_ERR_CODE = "err_code";
		
		public static final String RETURN_PREPAY_ID = "prepay_id";
		
		public static final String RETURN_NONCE_STR = "nonce_str";
		
		//-------返回结果常量------------------
		public static final String PREPAY_RESULT_SUCCESS = "SUCCESS"; 
		
		public static final String PREPAY_RESULT_FAIL = "FAIL";
	}
	
	/**
	 * 根据商户号返回key
	 * @param mchId
	 * @return
	 */
	public static String getMchKey(String mchId) {
		return MCH_KEY_MAP.get(mchId);
	}
}
