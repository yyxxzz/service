package com.yoho.gateway.controller.order;

import com.yoho.error.event.PaymentEvent;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.order.payment.alipay.AlipayNotify;
import com.yoho.gateway.controller.order.payment.common.Constants;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.controller.order.payment.wechat.XMLUtil;
import org.jdom.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ming on 16/1/23.
 */
@Controller
public class AlipayWapController extends AbstractController implements ApplicationEventPublisherAware {

    private static final Logger logger = LoggerFactory.getLogger("alipayWapLogger");
    private static final Logger loggerErr = LoggerFactory.getLogger("alipayWapLoggerErr");
    private ApplicationEventPublisher publisher;

    /**
     * 支付宝通知回调
     */
    @RequestMapping(value = "/payment/alipaywap_notify", method = RequestMethod.POST)
    public void notifyAliWapPayment(HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("\n\n\n************************* Notify");

        logRequestParams(request, logger);

        String notify_data = request.getParameter("notify_data");
        String sec_id = request.getParameter("sec_id");
        String service = request.getParameter("service");
        String v = request.getParameter("v");
        String sign = request.getParameter("sign");

        Map<String, String> params = new HashMap<>();
        params.put("service", service);
        params.put("v", v);
        params.put("sec_id", sec_id);
        params.put("notify_data", notify_data);
        params.put("sign", sign);

        logger.debug("REQ params: {}", params);

        if (!AlipayNotify.getSignVerifyWap(params, sign)) {
            // 签名验证失败
            loggerErr.error("Sign verification failed: {}", params);

            response.getWriter().print("failed");
            return;
        }

        Map<String, String> res = null;
        try {
            res = XMLUtil.doXMLParse(notify_data);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == res) {
            loggerErr.error("Empty parsed params");
            response.getWriter().print("failed");
            return;
        }

        logger.debug("XML PARSED: {}", res);


        //publish  event
        PaymentEvent event =  new PaymentEvent("alipay", res.get("out_trade_no"),
                res.get("trade_no"), res.get("total_fee"),res.get("trade_status"));
        this.publisher.publishEvent(event);

        String trade_status = res.get("trade_status");

        // 判断交易结果, TRADE_FINISH, WAIT_BUYER_PAY, TRADE_CLOSED
        if (!trade_status.equals("TRADE_SUCCESS")) {
            loggerErr.warn("not TRADE_SUCCESS, {}", res);
            return;
        }

        //支付宝交易号
        String trade_no = res.get("trade_no");
        // YOHO订单号
        String out_trade_no = res.get("out_trade_no");

        PayResult payResult = new PayResult();
        payResult.setOrderCode(out_trade_no);
        payResult.setPaymentID(Constants.ALIPAYWAP_CODE);  // 支付宝的代码

        logger.info("[{}] TOTAL_FEE: {}", out_trade_no, res.get("total_fee"));

        double total_fee = Double.parseDouble(res.get("total_fee"));
        payResult.setTotalFeeInYuan(total_fee);
        payResult.setBankCode("");
        payResult.setBankName("");
        payResult.setPaymentResult(200);
        payResult.setPaymentTime(null == res.get("gmt_payment") ? "" : res.get("gmt_payment"));
        payResult.setCallbackTime(null == params.get("notify_time") ? "" : params.get("notify_time"));
        payResult.setResultMsg(res.get("notify_type"));
        payResult.setPayOrderCode(out_trade_no);
        payResult.setTradeNo(trade_no);
        payResult.setBankBillNo("");

        try {
            notify(out_trade_no, payResult, logger);
        } catch (ServiceException e) {
            logger.info("[{}] send fail to alipay, {}", out_trade_no, res);
            response.getWriter().print("failed");
            event.setStatus("PROCESS_FAIL");
            this.publisher.publishEvent(event);
            return;
        }

        logger.info("[{}] send success to alipay, {}", out_trade_no, res);
        event.setStatus("SUCCESS");
        this.publisher.publishEvent(event);
        response.getWriter().print("success");
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}

