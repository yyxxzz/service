package com.yoho.gateway.controller.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.yoho.core.redis.YHRedisTemplate;
import com.yoho.core.redis.YHValueOperations;
import com.yoho.error.event.PaymentEvent;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.payment.common.Constants;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.controller.order.payment.common.TimeUtil;
import com.yoho.gateway.controller.order.payment.tenpay.handlers.*;
import com.yoho.gateway.controller.order.payment.tenpay.util.ConstantUtil;
import com.yoho.gateway.controller.order.payment.tenpay.util.Sha1Util;
import com.yoho.gateway.controller.order.payment.tenpay.util.WXUtil;
import com.yoho.gateway.interceptor.RemoteIPInterceptor;
import com.yoho.service.model.order.request.OrderRequest;
import com.yoho.service.model.order.response.Orders;
import com.yoho.service.model.order.response.PaymentData;

import org.apache.commons.lang3.StringUtils;
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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by ming on 16/1/19.
 */
@Controller
public class WechatController extends AbstractController implements ApplicationEventPublisherAware {

    private static final Logger logger = LoggerFactory.getLogger("wechatLogger");
    private static final Logger loggerErr = LoggerFactory.getLogger("wechatLoggerErr");
    private ApplicationEventPublisher publisher;

    private final int EXPIRE_INTERVAL = 7200;  // 默认支付超时时间, 2小时

    @Value("${wechat.app.notifyurl}")
    private String notifyURLApp;

    @Resource
    private YHValueOperations<String, String> valueOperations;

    @Resource(name = "yhRedisTemplate")
    private YHRedisTemplate redis;

    /**
     * 微信支付接口
     * 根据调用者传入的orderCode,生成预支付Id和支付调用参数
     */
    @RequestMapping(value = "/payment/wechat_data", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse getWechatData(@RequestParam("order_code") Long orderCode,
                                     @RequestParam("app_key") String appKey,
                                     @RequestParam("payment_code") byte paymentCode,
                                     HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.debug("\n\n\n******************** Prepay Data");

        // 初始化wechat sdk
        PackageRequestHandler packageReqHandler = new PackageRequestHandler(request, response);//生成package的请求类
        PrepayIdRequestHandler prepayReqHandler = new PrepayIdRequestHandler(request, response);//获取prepayid的请求类
        ClientRequestHandler clientHandler = new ClientRequestHandler(request, response); //返回客户端支付参数的请求类
        packageReqHandler.setKey(ConstantUtil.PARTNER_KEY);

        // 根据传入的orderCode,构造请求,查询订单数据
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderCode(orderCode);
        orderRequest.setPayment(paymentCode);  // 微信支付代码为19

        Orders orderData = null;
        try {
            orderData = serviceCaller.call("order.getOrdersByCode", orderRequest, Orders.class);
        } catch (Exception e) {
            loggerErr.error("[{}] getOrdersByCode err: {}", orderCode, e);
        }

        if (null == orderData) {
            loggerErr.error("[{}] No such order", orderCode);
            return new ApiResponse.ApiResponseBuilder().code(500).message("没有要支付的订单.").build();
        }
        
        if(!isOrderPayable(orderData, logger)){
        	loggerErr.error("[{}] Order status is not payable", orderCode);
        	return new ApiResponse.ApiResponseBuilder().code(500).message("订单状态不可支付.").build();
        }
        // 更新订单支付方式
        serviceCaller.call("order.updateOrdersPaymentByCode", orderRequest, Void.class);

        // 查询该订单的支付数据,如果存在则返回该数据,否则重新构造
        PaymentData paymentData = null;
        try {
            paymentData = serviceCaller.call("order.getPaymentData", orderRequest, PaymentData.class);
        } catch (Exception e) {
            loggerErr.error("[{}] getPaymentData error [{}]", orderCode, e);
        }

        // 支付数据
        String paymentStr = "";

        if (null != paymentData) {
            // 已有支付数据,直接返回该数据
            paymentStr = paymentData.getPaymentData();
            logger.info("[{}] found paymentData from db, payData:{}", orderCode, paymentStr);
            JSONObject obj = JSONObject.parseObject(paymentStr);
            return new ApiResponse.ApiResponseBuilder().code(200).message("pay info").data(obj).build();
        }

        // 该笔订单的支付数据不存在,则重新获取token,生成支付参数
        // 第一步: 获取token
        // String token = AccessTokenRequestHandler.getAccessToken();
        String token = getAccessToken();

        if (Strings.isNullOrEmpty(token)) {
            loggerErr.error("[{}] Failed to get token", orderCode);
            return new ApiResponse.ApiResponseBuilder().code(500).message("获取token失败.").build();
        }

        // 第二步: 生成package订单参数
        // 2.1 设置参数
        packageReqHandler.setParameter("bank_type", "WX");//银行渠道
        packageReqHandler.setParameter("body", "订单号:" + orderData.getOrderCode()); //商品描述 $this->orderData->orderName;
        packageReqHandler.setParameter("notify_url", notifyURLApp); //接收财付通通知的URL
        packageReqHandler.setParameter("partner", ConstantUtil.PARTNER); //商户号
        packageReqHandler.setParameter("out_trade_no", "YOHOBuy_" + String.valueOf(orderData.getOrderCode())); //商家订单号 $this->orderData->orderCodeString;
        packageReqHandler.setParameter("total_fee", String.valueOf(orderData.getAmount().multiply(new BigDecimal("100")).intValue())); //商品金额,以分为单位 $this->orderData->feeTotal * 100;
        packageReqHandler.setParameter("spbill_create_ip", RemoteIPInterceptor.getRemoteIP()); //订单生成的机器IP，指用户浏览器端IP $this->orderData->clientIP;
        packageReqHandler.setParameter("fee_type", "1"); //币种，1人民币   66
        packageReqHandler.setParameter("input_charset", "UTF-8"); //字符编码
        String expireTime = TimeUtil.getExpireTime(orderData.getCreateTime());
        packageReqHandler.setParameter("time_expire", expireTime);

        // 2.2 生成package包
        String packageValue = packageReqHandler.getRequestURL();

        logger.info("[{}] package value {}", orderCode, packageValue);

        // 第三步: 获取prepayId
        String noncestr = WXUtil.getNonceStr();
        String timestamp = WXUtil.getTimeStamp();
        String traceid = "YOHOBuy_" + orderData.getOrderCode();
        // 3.1 设置获取prepayid支付参数
        prepayReqHandler.setParameter("appid", ConstantUtil.APP_ID);
        prepayReqHandler.setParameter("appkey", ConstantUtil.APP_KEY);
        prepayReqHandler.setParameter("noncestr", noncestr);
        prepayReqHandler.setParameter("package", packageValue);
        prepayReqHandler.setParameter("timestamp", timestamp);
        prepayReqHandler.setParameter("traceid", traceid);

        // 3.2 生成获取预支付签名
        String sign = prepayReqHandler.createSHA1Sign();
        // 3.3 增加非参与签名的额外参数
        prepayReqHandler.setParameter("app_signature", sign);
        prepayReqHandler.setParameter("sign_method", "sha1");
        // 3.4 入口地址+token
        String gateUrl = ConstantUtil.GATEURL + token;
        prepayReqHandler.setGateUrl(gateUrl);

        // 3.5 获取prepayId
        String prepayid = prepayReqHandler.sendPrepay();

        // 获取prepayId失败
        if (Strings.isNullOrEmpty(prepayid)) {
            loggerErr.error("[{}] Failed to get PrepayId", orderCode);
            return new ApiResponse.ApiResponseBuilder().code(500).message("获取PrepayId失败.").build();
        }

        logger.debug("[{}] got prepayId: {}", orderCode, prepayid);
        // 获取prepayId成功,则生成支付参数
        // 第四步: 生成客户端支付参数
        clientHandler.setParameter("appid", ConstantUtil.APP_ID);
        clientHandler.setParameter("appkey", ConstantUtil.APP_KEY);
        clientHandler.setParameter("noncestr", noncestr);
        clientHandler.setParameter("package", "Sign=WXpay");
        clientHandler.setParameter("partnerid", ConstantUtil.PARTNER);
        clientHandler.setParameter("prepayid", prepayid);
        clientHandler.setParameter("timestamp", timestamp);
        // 生成签名
        //sign = clientHandler.createSHA1Sign();
        sign = createSHA1Sign(clientHandler.getAllParameters());
        clientHandler.setParameter("sign", sign);

        // 重新组织数据格式
        Map<String, Object> prePayData = new LinkedHashMap<>();
        prePayData.put("partnerId", ConstantUtil.PARTNER);
        prePayData.put("prepayId", prepayid);
        prePayData.put("package", "Sign=WXpay");
        prePayData.put("nonceStr", noncestr);
        prePayData.put("timeStamp", timestamp);
        prePayData.put("sign", sign);

        JSONObject sendData = new JSONObject();
        sendData.put("prePayUrl", ConstantUtil.GATEURL + token);
        sendData.put("token", token);
        sendData.put("prePayData", prePayData);

        paymentStr = JSON.toJSONString(sendData);

        // 将支付数据写入数据库
        paymentData = new PaymentData();
        paymentData.setOrderCode(orderCode);
        paymentData.setPaymentData(paymentStr);
        // 此处为支付数据的产生时间,而非订单产生时间
        int curr = (int) (System.currentTimeMillis() / 1000);
        paymentData.setCreateTime(curr);
        int expire = TimeUtil.getExpireTimeInSec(orderData.getCreateTime());
        paymentData.setExpireTime(expire);

        logger.info("[{}] paymentStr: {}, createT:{}, expireT:{}", orderCode, paymentStr, curr, expire);

        try {
            serviceCaller.call("order.savePaymentData", paymentData, Void.class);
        } catch (Exception e) {
            loggerErr.error("[{}] save paymentData failed, {}", orderCode, e);
        }
        return new ApiResponse.ApiResponseBuilder().code(200).message("pay info").data(sendData).build();
    }


    /**
     * 微信支付的回调入口
     */
    @RequestMapping(value = "/payment/wechat_notify", method = RequestMethod.POST)
    public void notifyWechatPayment(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("\n\n\n************************* Notify");

        // 初始化微信回调处理类
        ResponseHandler responseHandler = new ResponseHandler(request, response);
        responseHandler.setKey(ConstantUtil.PARTNER_KEY);

        logRequestParams(request, logger);



        String out_no_raw = responseHandler.getParameter("out_trade_no");
        String out_trade_no;
        // 把 YOHOBuy_XXXX 形式的订单号转换为 XXXX
        try {
            String[] sp = out_no_raw.split("_");
            out_trade_no = sp[1];
        } catch (Exception e) {
            loggerErr.error("[{}] wrong out_trade_no format! YOHOBuy_XXX", out_no_raw);
            responseHandler.sendToCFT("fail");
            return;
        }


        //publish event
        PaymentEvent event =  new PaymentEvent("wechat", out_trade_no,
                responseHandler.getParameter("transaction_id"), responseHandler.getParameter("total_fee"),
                responseHandler.getParameter("trade_status"));
        this.publisher.publishEvent(event);


        if (!responseHandler.isTenpaySign()) {  // 验证回调数据的签名
            loggerErr.error("[{}] Wechat notification check failed!", out_trade_no);
            event.setStatus("SIGN_CHECK_FAIL");
            this.publisher.publishEvent(event);
            responseHandler.sendToCFT("fail");  // 返回结果给微信
            return;
        }

        PayResult payResult = new PayResult();

        payResult.setOrderCode(out_trade_no);
        payResult.setPaymentID(Constants.WECHAT_CODE);  // 微信支付的代码
        payResult.setPaymentResult(200);
        payResult.setPaymentTime(TimeUtil.formatTime(responseHandler.getParameter("time_end")));
        payResult.setCallbackTime(TimeUtil.getCurrentTime());

        double total_fee = Double.parseDouble(responseHandler.getParameter("total_fee"));
        payResult.setTotalFeeInCent(total_fee);
        payResult.setBankCode(responseHandler.getParameter("bank_type"));  // "bank_type"
        payResult.setBankName(responseHandler.getParameter("bank_type"));  // "bank_type"
        payResult.setTradeNo(responseHandler.getParameter("transaction_id"));

        String orderCode = payResult.getOrderCode();
        // 支付成功处理流程
        try {
            notify(orderCode, payResult, logger);
        } catch (ServiceException e) {
            loggerErr.error("[{}] ServiceEx error: {}", orderCode, e.getMessage());
            // 更新状态失败,让微信重新发送通知
            responseHandler.sendToCFT("fail");
            event.setStatus("PROCESS_FAILED");
            this.publisher.publishEvent(event);
            return;
        }

        event.setStatus("SUCCESS");
        this.publisher.publishEvent(event);
        responseHandler.sendToCFT("success");  // 返回结果给微信
    }


    private static String createSHA1Sign(SortedMap map) {
        StringBuffer sb = new StringBuffer();
        Set es = map.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            sb.append(k + "=" + v + "&");
        }
        String params = sb.substring(0, sb.lastIndexOf("&"));
        String appsign = Sha1Util.getSha1(params);

        return appsign;
    }

    private String getAccessToken() {
        String accessToken = null;
        String key = "weixin-token-" + ConstantUtil.APP_ID;
        try {
            boolean haskey = redis.hasKey(key);
            if (haskey) {
                accessToken = valueOperations.get(key);
                logger.info("get access token from redis success,key:{}, accesstoken:{}", key, accessToken);
            }
            boolean newTokenFlag = false;
            if (StringUtils.isEmpty(accessToken)) {
                logger.info("accesstoken is null,key:{}", key);
                newTokenFlag = true;
            } else {
                //是否过期
                if (AccessTokenRequestHandler.tokenIsExpire(accessToken)) {
                    logger.info("accesstoken:{} expired,key:{}", accessToken, key);
                    newTokenFlag = true;
                }
            }

            if (newTokenFlag) {
                accessToken = createAccessTokenAndCache(key);
            }
            logger.info("return accesstoken:{} of key:{}", accessToken, key);

        } catch (Exception e) {
            logger.warn("get cache value faid,key {}", key, e);
        }
        return accessToken;
    }

    private String createAccessTokenAndCache(String key) {
        String accessToken = AccessTokenRequestHandler.getAccessToken();
        //设置值
        valueOperations.set(key, accessToken);
        //缓存2h
        redis.expire(key, 2, TimeUnit.HOURS);
        logger.info("set access token to redis success,key:{}, accesstoken:{},expired after 2h", key, accessToken);
        return accessToken;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
