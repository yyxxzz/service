package com.yoho.gateway.controller.order;

/**
 * Created by ming on 16/1/22.
 */

import com.yoho.error.event.PaymentEvent;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.order.payment.common.Constants;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.controller.order.payment.common.TimeUtil;
import com.yoho.gateway.controller.order.payment.tenpay.handlers.*;
import com.yoho.gateway.controller.order.payment.tenpay.util.Sha1Util;
import com.yoho.gateway.controller.order.payment.wechat.CommonUtil;
import com.yoho.gateway.controller.order.payment.wechat.ConfigUtil;
import com.yoho.gateway.controller.order.payment.wechat.PayCommonUtil;
import com.yoho.gateway.controller.order.payment.wechat.XMLUtil;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.*;


@Controller
public class WechatWapController extends AbstractController implements ApplicationEventPublisherAware {

    private static final Logger logger = LoggerFactory.getLogger("wechatWapLogger");
    private static final Logger loggerErr = LoggerFactory.getLogger("wechatWapLoggerErr");
    private ApplicationEventPublisher publisher;

    // H5 支付参数
    @Value("${wechat.wap.mchid}")
    private String mchidWap;

    @Value("${wechat.wap.key}")
    private String keyWap;

    @Value("${wechat.wap.appid}")
    private String appIdWap;

    @Value("${wechat.wap.appsecret}")
    private String appSecretWap;

    @Value("${wechat.wap.notifyurl}")
    private String notifyURLWap;


    /**
     * 微信WAP支付的回调入口
     */
    @RequestMapping(value = "/payment/wechatwap_notify", method = RequestMethod.POST, produces={MediaType.APPLICATION_XML_VALUE})
    @ResponseBody
    public void notifyWechatWapPayment(HttpServletRequest request, HttpServletResponse response) throws Exception {

        logger.info("\n\n\n************************* Notify");

        String xml = IOUtils.toString(request.getInputStream());

        logger.info("XML received: {}", xml);

        // 解析 XML 参数
        SortedMap<String, String> res = XMLUtil.doXMLParse(xml);
        logger.info("Params parsed: {}", res);

        String sign_calc = PayCommonUtil.createSign("UTF-8", res);
        String sign = res.get("sign");

        logger.info("sign returned: {}, sign calculated: {}", sign, sign_calc);

        // 验证签名合法性
        if (!sign_calc.equalsIgnoreCase(sign)) {
            logger.error("Signature verification failed!");
            // 返回给财付通
            sendResp("FAIL", "Sig verif failed!", response, "");
            return;
        }




        String out_no = res.get("out_trade_no");  // "YOHOBuy_XXXXXX"
        logger.info("[{}] success!", out_no);

        // 判断支付是否成功
        if (!"SUCCESS".equalsIgnoreCase(res.get("result_code"))) {
            logger.error("[{}] payment failed! ret_code: {}, ret_msg: {}", out_no, res.get("result_code"), res.get("return_msg"));
            // 返回给财付通
            sendResp("FAIL", "not success!", response, out_no);
            return;
        }

        PayResult payResult = new PayResult();

        // H5 的微信订单号以 "YOHOBUY_"开头,需处理
        String out_trade_no = "";
        try {
            String[] fs = out_no.split("_");
            out_trade_no = fs[1];
        } catch (Exception e) {
            loggerErr.error("Wrong out_trade_no, should begin with YOHOBUY_, [{}]", out_no);
            sendResp("FAIL", "wrong out_trade_no", response, out_trade_no);
            return;
        }


        //publish  event
        PaymentEvent event =  new PaymentEvent("wechatwap", out_trade_no,
                res.get("transaction_id"), res.get("total_fee"),
                "success");
        this.publisher.publishEvent(event);

        payResult.setOrderCode(out_trade_no);
        payResult.setPaymentID(Constants.WECHATWAP_CODE);  // 微信WAP支付的代码
        payResult.setPaymentResult(0);
        payResult.setPaymentTime(TimeUtil.formatTime(res.get("time_end")));
        payResult.setCallbackTime(TimeUtil.getCurrentTime());

        double total_fee = Double.parseDouble(res.get("total_fee"));
        payResult.setTotalFeeInCent(total_fee);
        payResult.setBankCode(res.get("bank_type"));  // "bank_type"
        payResult.setBankName(res.get("bank_type"));  // "bank_type"
        payResult.setTradeNo(res.get("transaction_id"));

        String orderCode = payResult.getOrderCode();
        // 支付成功处理流程
        try {
            notify(orderCode, payResult, logger);
        } catch (ServiceException e) {
            loggerErr.error("[{}] ServiceEx error: {}", out_trade_no, e.getMessage());
            // 更新状态失败,让微信重新发送通知
            sendResp("FAIL", "Resend", response, out_trade_no);
            event.setStatus("PROCESS_FAIL");
            this.publisher.publishEvent(event);
            return;
        } catch (Exception e) {
            loggerErr.error("[{}] Exception: {}", out_trade_no, e.getMessage());
            // 更新状态失败,让微信重新发送通知
            sendResp("FAIL", "Resend", response, out_trade_no);
            event.setStatus("PROCESS_FAIL");
            this.publisher.publishEvent(event);
            return;
        }

        // 返回给微信
        sendResp("SUCCESS", "OK", response, out_trade_no);
        event.setStatus("SUCCESS");
        this.publisher.publishEvent(event);
    }

    /**
     * 返回处理结果给微信
     * @param code, "SUCCESS", "FAIL"
     * @param msg
     */
    private void sendResp (String code, String msg, HttpServletResponse response, String orderCode) {

        String retStr;
        if (code.equals("SUCCESS")) {
            retStr = PayCommonUtil.setXML(code);
        } else {
            retStr = PayCommonUtil.setXML(code, msg);
        }
        
        logger.info("[{}] Send result to Wechat: {}", orderCode, retStr);

        try {
            PrintWriter pr = response.getWriter();
            response.setContentType("application/xml;charset=UTF-8");
            pr.write(retStr);
        } catch (IOException e) {
            logger.error("[{}] Send result to wechat exception: {}", orderCode, e);
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
