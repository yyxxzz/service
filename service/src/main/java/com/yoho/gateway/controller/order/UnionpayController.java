package com.yoho.gateway.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.yoho.error.event.PaymentEvent;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.payment.common.Constants;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.controller.order.payment.common.TimeUtil;
import com.yoho.gateway.controller.order.payment.unionpay.SDKConfig;
import com.yoho.gateway.controller.order.payment.unionpay.SDKConstants;
import com.yoho.gateway.controller.order.payment.unionpay.SDKUtil;
import com.yoho.service.model.order.request.OrderRequest;
import com.yoho.service.model.order.response.Orders;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ming on 16/1/19.
 */
@Controller
public class UnionpayController extends AbstractController implements ApplicationEventPublisherAware {

// TODO: 加数据库表,存银联订单号,及对应关系,捕获所有异常,打日志

    private static final Logger logger = LoggerFactory.getLogger("unionpayLogger");
    //private static final Logger loggerErr = LoggerFactory.getLogger("unionpayLoggerErr");

    @Value("${unionpay.env}")
    private String unionpayEnv;

    @Value("${unionpay.merid}")
    private String unionpayMerId;

    @Value("${unionpay.sign.cert}")
    private String unionpaySignCert;

    /**
     * 银联的回调地址，默认https
     */
    @Value("${unionpay.notifyurl:https://service.yoho.cn/payment/unionpay_newnotify}")
    private String unionpayNotifyURL;

    private ApplicationEventPublisher publisher;

    @PostConstruct
    public void init() {
        SDKConfig.getConfig().loadPropertiesFromSrc();// 从classpath加载acp_sdk.properties文件
        org.springframework.core.io.Resource resource = new ClassPathResource("/certs");
        String dirpathRaw = null;
        try {
            dirpathRaw = resource.getURI().toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String dirpath = dirpathRaw.replace("file:", "");
        SDKConfig.getConfig().setValidateCertDir(dirpath);
        SDKConfig.getConfig().setSignCertPath(dirpath);

        logger.info("Absolute Cert PATH: {}", dirpath);
        logger.info("SignCertPath: {}", SDKConfig.getConfig().getSignCertPath());
        logger.info("ValidateCertDir: {}", SDKConfig.getConfig().getValidateCertDir());
        logger.info("NotifyURL: {}", unionpayNotifyURL);
    }


    /**
     * 银联支付接口
     * 根据调用者传入的orderCode,生成预支付Id和支付调用参数
     */
    @RequestMapping(value = "/payment/unionpay_data", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponse getUnionData(@RequestParam("order_code") Long orderCode,
                                    @RequestParam("payment_code") byte paymentCode,
                                    HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("\n\n\n************************* Prepay Data");

        logger.debug("[{}] App request: payCode {}", orderCode, paymentCode);
        // 根据传入的orderCode,构造请求,查询订单数据
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderCode(orderCode);
        orderRequest.setPayment(paymentCode);  // 银联代码为14

        Orders orderData = serviceCaller.call("order.getOrdersByCode", orderRequest, Orders.class);

        if (null == orderData) {
            logger.error("[{}] no such order", orderCode);
            return new ApiResponse.ApiResponseBuilder().code(500).message("没有要支付的订单").build();
        }
        
        if(!isOrderPayable(orderData, logger)){
        	logger.error("[{}] Order status is not payable", orderCode);
        	return new ApiResponse.ApiResponseBuilder().code(500).message("订单状态不可支付.").build();
        }

        // 更新订单支付方式
        serviceCaller.call("order.updateOrdersPaymentByCode", orderRequest, Void.class);

        /**
         * 组装请求报文
         */
        Map<String, String> data = new HashMap<String, String>();
        // 版本号
        data.put("version", "5.0.0");
        // 字符集编码 默认"UTF-8"
        data.put("encoding", "UTF-8");
        // 签名方法 01 RSA
        data.put("signMethod", "01");
        // 交易类型 01-消费
        data.put("txnType", "01");
        // 交易子类型 01:自助消费 02:订购 03:分期付款
        data.put("txnSubType", "01");
        // 业务类型
        data.put("bizType", "000201");
        // 渠道类型，07-PC，08-手机
        data.put("channelType", "08");
        // 前台通知地址 ，控件接入方式无作用
        data.put("frontUrl", "");
        // 后台通知地址
        data.put("backUrl", unionpayNotifyURL);
        // 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
        data.put("accessType", "0");
        // 商户号码
        data.put("merId", unionpayMerId);
        // 商户订单号，8-40位数字字母
        data.put("orderId", orderData.getOrderCode().toString());
        // 订单发送时间，取系统时间
        // data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        data.put("txnTime", unixTimeStamp(orderData.getCreateTime()));
        // 交易金额，单位分
        data.put("txnAmt", String.valueOf(orderData.getAmount().multiply(new BigDecimal("100")).intValue()));
        // 交易币种, RMB=156
        data.put("currencyCode", "156");
        // 订单支付超时时间, 订单生成时间加超时间隔（7200秒)
        data.put("payTimeout", TimeUtil.getExpireTime(orderData.getCreateTime()));
        // 请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
        data.put("reqReserved", "透传信息");
        // 订单描述，可不上送，上送时控件中会显示该信息
        // data.put("orderDesc", "订单描述");

        /** 对请求参数进行签名并发送http post请求，接收同步应答报文 **/
        //SDKConfig.getConfig().setSignCertPath(dirpath + unionpaySignCert);
        //由于Applepay加入后改为多证书模式，故这里改为多证书模式的签名方法。
        Map<String, String> submitFromData = SDKUtil.signData(data, 
        			SDKConfig.getConfig().getSignCertPath() + unionpaySignCert,
        			"yohobuy",
        			SDKUtil.encoding_UTF8);

        //交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
        String requestAppUrl = SDKConfig.getConfig().getAppRequestUrl();

        //发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）
        Map<String, String> resmap = SDKUtil.submitUrl(submitFromData, requestAppUrl, SDKUtil.encoding_UTF8);

        // 判断resmap为空
        if (null == resmap || resmap.isEmpty()) {
            logger.error("[{}] empty response", orderCode);
            return new ApiResponse.ApiResponseBuilder().code(500).message("failed to fetch TN").build();
        }

        //应答码规范参考《平台接入接口规范-第5部分-附录》
        String respCode = resmap.get("respCode");

        String tn = "";
        if (!("00").equals(respCode)) {
            logger.error("[{}] failed to fetch Unionpay TN, Req: {}, Res: {}", orderCode, data.toString(), resmap.toString());
            return new ApiResponse.ApiResponseBuilder().code(500).message("failed to fetch TN").build();
        }

        //成功,获取tn号
        tn = resmap.get("tn");
        JSONObject sendData = new JSONObject();
        sendData.put("tn", tn);
        sendData.put("env", unionpayEnv);   // 00为生产环境, 01为测试环境
        logger.info("[{}] Unionpay TN, Req: {}, Res: {}", orderCode, data.toString(), resmap.toString());

        return new ApiResponse.ApiResponseBuilder().code(200).message("pay info").data(sendData).build();
    }


    /**
     * 银联通知回调
     */
    @RequestMapping(value = "/payment/unionpay_newnotify", method = RequestMethod.POST)
    public void notifyUnionPayment(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("\n\n\n************************* Notify");

        PaymentEvent event =  new PaymentEvent("unionpay", request.getParameter("orderId"),
                request.getParameter("queryId"), request.getParameter("txnAmt"),request.getParameter("respMsg"));
        this.publisher.publishEvent(event);


        String encoding = request.getParameter(SDKConstants.param_encoding);
        // 获取请求参数中所有的信息
        Map<String, String> reqParam = parseParams(request.getParameterMap()); //getAllRequestParam(request);

        if (null == reqParam || reqParam.isEmpty()) {
            logger.error("Empty resp from UnionPay");
            // 返回银联500码,银联重新发送通知
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        logger.debug("notify req:{}", reqParam);
        
        // 验证签名
        if (!SDKUtil.validate(reqParam, encoding)) {
            // 验证失败
            logger.error("signature verification failed. {}", reqParam);
            // 返回银联500码,银联重新发送通知
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            event.setStatus("VER_FAILED");
            this.publisher.publishEvent(event);

            return;
        }
        // 验证通过
        String orderCode = reqParam.get("orderId");
        logger.debug("[{}] orderCode", orderCode);

        PayResult payResult = new PayResult();
        payResult.setOrderCode(orderCode);
        payResult.setPaymentID(Constants.UNIONPAY_CODE);  // 银联的代码  --PC银联支付也走这个回调，故不能写死

        double total_fee = Double.parseDouble(request.getParameter("txnAmt"));  // 单位为分
        payResult.setTotalFeeInCent(total_fee);  // 由 setTotalFeeInCent 转换为元
        payResult.setBankCode("");
        payResult.setBankName("");
        payResult.setPaymentResult(200);
        payResult.setPaymentTime(TimeUtil.formatTime(reqParam.get("txnTime")));
        payResult.setCallbackTime(TimeUtil.getCurrentTime());
        payResult.setResultMsg(reqParam.get("respMsg"));
        payResult.setPayOrderCode(reqParam.get("orderId"));
        payResult.setTradeNo(reqParam.get("queryId"));
        payResult.setBankBillNo("");

        try {
        	notifyProcess(payResult, logger);
        } catch (Exception e) {
            logger.error("[{}] Send 500 to unionpay. {}", orderCode, e);
            // 更新状态失败,让银联重新发送通知
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            event.setStatus("PROCESS_FAILED");
            this.publisher.publishEvent(event);

            return;
        }

        logger.info("[{}] Send OK to unionpay", orderCode);


        event.setStatus("SUCCESS");
        this.publisher.publishEvent(event);

        response.getWriter().print("ok"); // 返回状态码200即可
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


    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher  = applicationEventPublisher;
    }
}
