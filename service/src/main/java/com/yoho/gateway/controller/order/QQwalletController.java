package com.yoho.gateway.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.yoho.error.event.PaymentEvent;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.payment.common.Constants;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.controller.order.payment.common.TimeUtil;
import com.yoho.gateway.controller.order.payment.tenpay.client.ClientResponseHandler;
import com.yoho.gateway.controller.order.payment.tenpay.client.TenpayHttpClient;
import com.yoho.gateway.controller.order.payment.tenpay.handlers.RequestHandler;
import com.yoho.gateway.controller.order.payment.tenpay.handlers.ResponseHandler;
import com.yoho.gateway.controller.order.payment.tenpay.util.WXUtil;
import com.yoho.service.model.order.request.OrderRequest;
import com.yoho.service.model.order.response.Orders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ming on 16/1/19.
 */
@Controller
public class QQwalletController extends AbstractController implements ApplicationEventPublisherAware {

    private static final Logger logger = LoggerFactory.getLogger("qqwalletLogger");
    private static final Logger loggerErr = LoggerFactory.getLogger("qqwalletLoggerErr");
    private ApplicationEventPublisher publisher;

    @Value("${qq.partnerid}")
    private String partner;

    @Value("${qq.partnerkey}")
    private String key;

    @Value("${qq.appid}")
    private String appId;

    @Value("${qq.appkey}")
    private String appKey;

    @Value("${qq.notifyurl}")
    private String notify_url;

    /**
     * 获取 QQ钱包支付的流水号
     * 根据调用者传入的orderCode,生成预支付Id和支付调用参数
     */
    @RequestMapping(value = "/payment/qqpay_data", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse getTenpayData(@RequestParam("order_code") Long orderCode,
                                     @RequestParam("payment_code") byte paymentCode,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("\n\n\n************************* Prepay Data");

        //创建请求对象
        RequestHandler queryReq = new RequestHandler(null, null);
        //通信对象
        TenpayHttpClient httpClient = new TenpayHttpClient();
        //应答对象
        ClientResponseHandler queryRes = new ClientResponseHandler();

        queryReq.init();
        //设置密钥
        queryReq.setKey(key);
        queryReq.setGateUrl("http://myun.tenpay.com/cgi-bin/wappayv2.0/wappay_init.cgi");

        // 根据传入的orderCode,构造请求,查询订单数据
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderCode(orderCode);
        orderRequest.setPayment(paymentCode);  // 微信支付代码为19
        Orders orderData = serviceCaller.call("order.getOrdersByCode", orderRequest, Orders.class);

        if (null == orderData) {
            loggerErr.error("[{}] No such order.", orderCode);
            return new ApiResponse.ApiResponseBuilder().code(500).message("没有要支付的订单.").build();
        }

        if(!isOrderPayable(orderData, logger)){
        	loggerErr.error("[{}] Order status is not payable", orderCode);
        	return new ApiResponse.ApiResponseBuilder().code(500).message("订单状态不可支付.").build();
        }

        // 更新订单支付方式
        serviceCaller.call("order.updateOrdersPaymentByCode", orderRequest, Void.class);

        //-----------------------------
        //设置支付参数
        //-----------------------------
        queryReq.setParameter("ver", "2.0");
        queryReq.setParameter("charset", "2");
        queryReq.setParameter("bank_type", "0");
        queryReq.setParameter("desc", "订单号:" + orderCode.toString());
        queryReq.setParameter("pay_channel", "1");
        queryReq.setParameter("bargainor_id", partner);
        queryReq.setParameter("sp_billno", orderCode.toString());
        queryReq.setParameter("total_fee", String.valueOf(orderData.getAmount().multiply(new BigDecimal(100)).intValue()));
        queryReq.setParameter("fee_type", "1");
        queryReq.setParameter("notify_url", notify_url);
        queryReq.setParameter("time_start", unixTimeStamp(orderData.getCreateTime()));
        queryReq.setParameter("time_expire", TimeUtil.getExpireTime(orderData.getCreateTime()));

        //通信对象
        httpClient.setTimeOut(5);
        //设置请求内容
        logger.info("[{}] queryReq: {}", orderCode, queryReq.getRequestURL());
        httpClient.setReqContent(queryReq.getRequestURL());

        //后台调用
        if (!httpClient.call()) {
            loggerErr.error("[{}] backend call failed: {}", orderCode, httpClient.getResponseCode());
            loggerErr.error(httpClient.getErrInfo());
            //有可能因为网络原因，请求已经处理，但未收到应答
            return new ApiResponse.ApiResponseBuilder().code(500).message("failed to fetch token").build();
        }

        queryRes.setContent(httpClient.getResContent());
        logger.info("[{}] queryRes: {}", orderCode, httpClient.getResContent());

        //获取token_id
        String token_id = queryRes.getParameter("token_id");

        JSONObject sendData = new JSONObject();
        sendData.put("token", token_id);

        // 计算签名
        String nonce = WXUtil.getNonceStr();
        String pck = "appId=" + appId + "&bargainorId=" + partner + "&nonce=" + nonce + "&pubAcc=&tokenId=" + token_id;

        // TODO: test
        String sign = sign(pck, appKey);
        if (null == sign) {
            loggerErr.error("[{}] pay data sign failed", orderCode);
            return new ApiResponse.ApiResponseBuilder().code(500).message("failed to sign data").build();
        }
        sendData.put("nonce", nonce);
        sendData.put("sign", sign);

        logger.info("[{}] sending pay info to App: {}", orderCode, sendData);
        return new ApiResponse.ApiResponseBuilder().code(200).message("pay info").data(sendData).build();
    }


    /**
     * QQ钱包支付的回调入口
     */
    @RequestMapping(value = "/payment/qqpay_notify", method = RequestMethod.GET)
    public void notifyQQPayment(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("\n\n\n************************* Notify");

        //创建支付应答对象
        ResponseHandler resHandler = new ResponseHandler(request, response);
        resHandler.setKey(key);

        //取结果参数做业务处理
        logger.debug("sp_billno:{}, transaction_id:{}, total_fee:{}",
                resHandler.getParameter("sp_billno"),
                resHandler.getParameter("transaction_id"),
                resHandler.getParameter("total_fee"));

        String orderCode = resHandler.getParameter("sp_billno");

        //publish  event
        PaymentEvent event =  new PaymentEvent("qqwallet", request.getParameter("sp_billno"),
                request.getParameter("transaction_id"), request.getParameter("total_fee"),
                request.getParameter("pay_result"));
        this.publisher.publishEvent(event);


        //判断签名
        if (!resHandler.isTenpaySign() || !"0".equals(resHandler.getParameter("pay_result"))) {
            loggerErr.error("[{}] notification signature verification failed!", orderCode);
            loggerErr.debug("[{}] send fail to wechat", orderCode);
            resHandler.sendToCFT("fail");
            event.setStatus("SIGN_WRONG");
            this.publisher.publishEvent(event);
            return;
        }

        PayResult payResult = new PayResult();
        payResult.setOrderCode(resHandler.getParameter("sp_billno"));
        payResult.setPaymentID(Constants.QQWALLET_CODE);
        payResult.setPaymentResult(200);
        payResult.setBankBillNo(resHandler.getParameter("bank_billno"));
        payResult.setBankName(resHandler.getParameter("bank_type"));
        double total_fee = Double.parseDouble(request.getParameter("total_fee"));  // 单位为分
        payResult.setTotalFeeInCent(total_fee);
        payResult.setBankCode("");
        payResult.setPaymentTime(TimeUtil.formatTime(resHandler.getParameter("time_end")));
        payResult.setCallbackTime(TimeUtil.getCurrentTime());
        payResult.setPayOrderCode(resHandler.getParameter("sp_billno"));
        payResult.setTradeNo(resHandler.getParameter("transaction_id"));

        try {
            notify(orderCode, payResult, logger);
        } catch (ServiceException e) {
            loggerErr.debug("[{}] send fail to qqwallet", orderCode);
            resHandler.sendToCFT("fail"); // 更新状态失败,让QQ重新发送通知
            event.setStatus("PROCESS_FAIL");
            this.publisher.publishEvent(event);
            return;
        }
        logger.debug("[{}] send success to qqwallet", orderCode);
        resHandler.sendToCFT("success");
        event.setStatus("SUCCESS");
        this.publisher.publishEvent(event);
    }

    public static String sign (String pck, String appKey) {
        SecretKey secretKey;
        try {
            secretKey = new SecretKeySpec((appKey + "&").getBytes("US-ASCII"), "HmacSHA1");

            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(secretKey);
            byte[] text = pck.getBytes("US-ASCII");
            byte[] finalText = mac.doFinal(text);
            return org.apache.commons.codec.binary.Base64.encodeBase64String(finalText);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            loggerErr.error("NosuchAlgorithm {}", e);
            return null;
        } catch (InvalidKeyException e) {
            e.printStackTrace();
            loggerErr.error("invalid key {}", e);
            return null;
        } catch (UnsupportedEncodingException e) {
            loggerErr.error("unsupported encoding {}", e);
            return null;
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }

    /**
     * UNIX时间戳格式转换, int(1309259070) => "20110628190430"
     */
    public static String unixTimeStamp(int sec) {
        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

        Long time = sec * 1000L;
        Date date = new Date(time);
        return format.format(date);
    }


    /**
     * 计算支付超时时间, 订单生成时间加上超时间隔 "20160120163217"
     * 传入订单生成时间, 返回字符串
     */
    public static String calcExpireTime(int createTime) {
        int expire = createTime + 300;  // 5min
        Date date = new Date(expire * 1000L);

        DateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");

        return format.format(date);
    }
}

