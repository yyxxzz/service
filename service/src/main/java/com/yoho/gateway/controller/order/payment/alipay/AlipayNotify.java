package com.yoho.gateway.controller.order.payment.alipay;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 *类名：AlipayNotify
 *功能：支付宝通知处理类
 *详细：处理支付宝各接口通知返回
 *版本：3.3
 *日期：2012-08-17
 */
public class AlipayNotify {

    private static final Logger logger = LoggerFactory.getLogger("alipayLogger");
    private static final Logger loggerErr = LoggerFactory.getLogger("alipayLoggerErr");

    /**
     * 支付宝消息验证地址
     */
    private static final String HTTPS_VERIFY_URL = "https://mapi.alipay.com/gateway.do?service=notify_verify&";

    /**
     * 验证消息是否是支付宝发出的合法消息
     * @param params 通知返回来的参数数组
     * @return 验证结果
     */
    public static boolean verify(Map<String, String> params) {

        //判断responsetTxt是否为true，isSign是否为true
        //responsetTxt的结果不是true，与服务器设置问题、合作身份者ID、notify_id一分钟失效有关
        //isSign不是true，与安全校验码、请求时的参数格式（如：带自定义参数等）、编码格式有关
        String responseTxt = "false";
        if(params.get("notify_id") != null) {
            String notify_id = params.get("notify_id");
            responseTxt = verifyResponse(notify_id);
        }
        String sign = "";
        if(params.get("sign") != null) {
            sign = params.get("sign");
            logger.info("Sign: " + sign);
        } else {
            logger.error("EMPTY sign!!!");
        }
        boolean isSign = getSignVerify(params, sign);

        //写日志记录
        // String sWord = "responseTxt=" + responseTxt + "\n isSign=" + isSign + "\n param returned：" + AlipayCore.createLinkString(params);
        // logger.info(sWord);

        logger.debug("Notif sign verif: {}, resp genius: {}" , isSign, responseTxt);
        // logger.debug("Alipay notify response genius: " + );
        
        // modify date: 2016-04-28，验证ATN时，如果支付宝无响应或者网络异常，responseTxt为空，此种小概率情况放通。
        if(StringUtils.isEmpty(responseTxt)){
        	responseTxt = "true";
        	logger.warn("check atn for alipay failed, notify_id: {}", params.get("notify_id"));
        }
        
        if (isSign && responseTxt.equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据反馈回来的信息，生成签名结果
     * @param Params 通知返回来的参数数组
     * @param sign 比对的签名结果
     * @return 生成的签名结果
     */
    private static boolean getSignVerify(Map<String, String> Params, String sign) {
        //过滤空值、sign与sign_type参数
        Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
        //获取待签名字符串
        String preSignStr = AlipayCore.createLinkString(sParaNew);
        //获得签名验证结果
        boolean isSign = false;
        
        String signType = "MD5".equals(Params.get("sign_type")) ? "MD5" : "RSA";
        
        if(signType.equals("RSA")){
            isSign = RSA.verify(preSignStr, sign, AlipayConfig.ali_public_key, AlipayConfig.input_charset);
        }
        else if (signType.equals("MD5")) {
//            preSignStr = "service=" + sParaNew.get("service") + "&v=" + sParaNew.get("v")
//            + "&sec_id=" + sParaNew.get("sec_id") + "&notify_data=" + sParaNew.get("notify_data");
            isSign = MD5.verify(preSignStr, sign, AlipayConfig.private_key_md5, AlipayConfig.input_charset);
        }

        return isSign;
    }

    public static boolean getSignVerifyWap(Map<String, String> Params, String sign) {
        //过滤空值、sign与sign_type参数
        Map<String, String> sParaNew = AlipayCore.paraFilter(Params);
        //获取待签名字符串
        String preSignStr = AlipayCore.createLinkString(sParaNew);
        //获得签名验证结果
        boolean isSign = false;
        if (AlipayConfig.sign_type_wap.equals("MD5")) {
            preSignStr = "service=" + sParaNew.get("service") + "&v=" + sParaNew.get("v")
                    + "&sec_id=" + sParaNew.get("sec_id") + "&notify_data=" + sParaNew.get("notify_data");
            System.out.println(preSignStr);
            isSign = MD5.verify(preSignStr, sign, AlipayConfig.private_key_md5, AlipayConfig.input_charset);
        }

        return isSign;
    }
    /**
     * 获取远程服务器ATN结果,验证返回URL
     * @param notify_id 通知校验ID
     * @return 服务器ATN结果
     * 验证结果集：
     * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     * true 返回正确信息
     * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String verifyResponse(String notify_id) {
        //获取远程服务器ATN结果，验证是否是支付宝服务器发来的请求

        String partner = AlipayConfig.partner;
        String veryfy_url = HTTPS_VERIFY_URL + "partner=" + partner + "&notify_id=" + notify_id;

        return checkUrl(veryfy_url);
    }

    /**
     * 获取远程服务器ATN结果
     * @param urlvalue 指定URL路径地址
     * @return 服务器ATN结果
     * 验证结果集：
     * invalid命令参数不对 出现这个错误，请检测返回处理中partner和key是否为空
     * true 返回正确信息
     * false 请检查防火墙或者是服务器阻止端口问题以及验证时间是否超过一分钟
     */
    private static String checkUrl(String urlvalue) {
        String inputLine = "";
        logger.debug("obtain atn begin, check url: {}", urlvalue);
        try {
            URL url = new URL(urlvalue);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection
                    .getInputStream()));
            inputLine = in.readLine().toString();
            logger.debug("obtain atn end, result: {}", inputLine);
        } catch (Exception e) {
        	loggerErr.warn("obtain atn failed, exception: {}", e);
            inputLine = "";
        }

        return inputLine;
    }
}
