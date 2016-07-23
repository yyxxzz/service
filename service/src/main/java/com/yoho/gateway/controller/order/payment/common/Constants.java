package com.yoho.gateway.controller.order.payment.common;

/**
 * Created by ming on 15/12/12.
 */
public class Constants {

	/**APP**/
    public static final byte ALIPAY_CODE = 15;
    public static final byte UNIONPAY_CODE = 14;
    public static final byte WECHAT_CODE = 19;
    public static final byte WECHATQR_CODE = 21;
    public static final byte WECHATWAP_CODE = 22;
    public static final byte ALIPAYWAP_CODE = 18;
    public static final byte APPLEPAY_CODE = 30;
    public static final byte QQWALLET_CODE = 26;
    
    /**PC/WAP**/
    public static final byte ALIPAY_PC_CODE = 2;
    public static final byte ALIPAY_BANK = 12;
    public static final byte ALIPAY_WAP = 18;

    // 手Q支付
    //商户号
    // public static String TENPAY_PARTNER = "1900000109";
    //密钥
    // public static String TENPAY_KEY = "8934e7d15453e97507ef794cf7b0519d";

    // public static String TENPAY_NOTIFYURL="http://devservice.yoho.cn:58077/payment/tenpay_notify";

    // 微信支付
    /*
    public static String WECHAT_APP_ID = "wx049fdaa3ba9cdd7a"; // "wxd930ea5d5a258f4f";//微信开发平台应用id
    public static String WECHAT_APP_SECRET = "f973fdb412307ea7b97d0252fd675104"; //"db426a9829e4b49a0dcac7b4162da6b6";//应用对应的凭证
    //应用对应的密钥
    public static String WECHAT_APP_KEY = "wGwAsgU5SeeM62glYaoC6ALBKhtOrF7Ek9LzE8trEuUG7jHeFdnSlyA1jblOYYS57QzWr8dYVsWGdeWhzeonnrKFZakgwFWPYVtyeP4XqSu9Qvxps8LEgxoFBEpRPm6C"; // "L8LrMqqeGRxST5reouB0K66CaYAWpqhAVsq7ggKkxHCOastWksvuX1uvmvQclxaHoYd3ElNBrNO2DHnnzgfVG9Qs473M3DTOZug5er46FhuGofumV8H2FVR9qkjSlC5K";
    public static String WECHAT_PARTNER = "1218934901"; // "1900000109";//财付通商户号
    public static String WECHAT_PARTNER_KEY = "b22de5cfd0ded341e0516505f72649a9"; // "8934e7d15453e97507ef794cf7b0519d";//商户号对应的密钥
    public static String WECHAT_TOKENURL = "https://api.weixin.qq.com/cgi-bin/token";//获取access_token对应的url
    public static String WECHAT_GRANT_TYPE = "client_credential";//常量固定值
    public static String WECHAT_EXPIRE_ERRCODE = "42001";//access_token失效后请求返回的errcode
    public static String WECHAT_FAIL_ERRCODE = "40001";//重复获取导致上一次获取的access_token失效,返回错误码
    public static String WECHAT_GATEURL = "https://api.weixin.qq.com/pay/genprepay?access_token=";//获取预支付id的接口url
    public static String WECHAT_ACCESS_TOKEN = "access_token";//access_token常量值
    public static String WECHAT_ERRORCODE = "errcode";//用来判断access_token是否失效的值
    public static String WECHAT_SIGN_METHOD = "sha1";//签名算法常量值
    //package常量值
    public static String WECHAT_packageValue = "bank_type=WX&body=%B2%E2%CA%D4&fee_type=1&input_charset=GBK&notify_url=http%3A%2F%2F127.0.0.1%3A8180%2Ftenpay_api_b2c%2FpayNotifyUrl.jsp&out_trade_no=2051571832&partner=1900000109&sign=10DA99BCB3F63EF23E4981B331B0A3EF&spbill_create_ip=127.0.0.1&time_expire=20131222091010&total_fee=1";
    public static String WECHAT_traceid = "testtraceid001";//测试用户id
    */


}
