package com.yoho.gateway.test;

import com.yoho.gateway.controller.order.AbstractController;
import com.yoho.gateway.controller.order.AlipayController;
import com.yoho.gateway.controller.order.payment.alipay.AlipayConfig;
import com.yoho.gateway.controller.order.payment.alipay.AlipayCore;
import com.yoho.gateway.controller.order.payment.alipay.AlipayNotify;
import com.yoho.gateway.controller.order.payment.alipay.RSA;
import com.yoho.gateway.controller.order.payment.wechat.XMLUtil;
import org.jdom.JDOMException;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * Created by ming on 16/1/23.
 */
public class TestAlipay {

    @Test
    public void testVerify() {

        Map<String, String> params = new HashMap<String, String>();

        params.put("body", "yoho");
        params.put("buyer_email", "newalipaytest@sina.com");
        params.put("buyer_id", "2088802749169369");
        params.put("discount", "0.00");
        params.put("gmt_create", "2016-01-22 20:25:29");
        params.put("gmt_payment", "2016-01-22 20:25:29");
        params.put("is_total_fee_adjust", "N");
        params.put("notify_id", "44117f49771f29aff20102e1cd232c2is0");
        params.put("notify_time", "2016-01-23 05:49:15");
        params.put("notify_type", "trade_status_sync");
        params.put("out_trade_no", "1615264227");
        params.put("payment_type", "1");
        params.put("price", "11.00");
        params.put("quantity", "1");
        params.put("seller_email", "zfb@yoho.cn");
        params.put("seller_id", "2088701661478015");
        params.put("sign", "V8B78z8JY/KaWn+hrEktnfK8VnTc9wI2A0KiIhiR6T5oIj+aEEd8IlZ2rQhldkwqISZQ1FABpgZCaWum7x0qV002EDxmF+VtQ5Qb7pg5U2vIZsUDkediA37192ZtUTZ+CMotTYorUCvvg3dMHAae9ZlF1tgQb0Q565bj3qafJR8=");
        params.put("sign_type", "RSA");
        params.put("subject", "1615264227");
        params.put("total_fee", "11.00");
        params.put("trade_no", "2016012221001004360089669117");
        params.put("trade_status", "TRADE_SUCCESS");
        params.put("use_coupon", "N");

        Map<String, String> sParaNew = AlipayCore.paraFilter(params);
        //获取待签名字符串
        String preSignStr = AlipayCore.createLinkString(sParaNew);

        System.out.println("preSignStr: " + preSignStr);

        String sign = params.get("sign");
        //获得签名验证结果
        boolean isSign = false;
        if(AlipayConfig.sign_type.equals("RSA")){
            isSign = RSA.verify(preSignStr, sign, AlipayConfig.ali_public_key, AlipayConfig.input_charset);
        }

        System.out.println(isSign);
    }

    @Test
    public void testParseXML() {
        String data = "<notify><payment_type>1</payment_type><subject>yoho order:1605798472</subject><trade_no>2016012321001004360097711805</trade_no><buyer_email>newalipaytest@sina.com</buyer_email><gmt_create>2016-01-23 16:00:03</gmt_create><notify_type>trade_status_sync</notify_type><quantity>1</quantity><out_trade_no>1605798472</out_trade_no><notify_time>2016-01-23 17:24:09</notify_time><seller_id>2088701661478015</seller_id><trade_status>TRADE_SUCCESS</trade_status><is_total_fee_adjust>N</is_total_fee_adjust><total_fee>0.23</total_fee><gmt_payment>2016-01-23 16:00:03</gmt_payment><seller_email>zfb@yoho.cn</seller_email><price>0.23</price><buyer_id>2088802749169369</buyer_id><notify_id>e78828dace8d603a98b1e8f8a5b1b87is0</notify_id><use_coupon>N</use_coupon></notify>";
        Map<String, String> res = null;
        try {
            res = XMLUtil.doXMLParse(data);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String sign = "47887825882a01f50eac6f78813f459c";
        res.put("sign", sign);
        System.out.println(res);
        //Map<String, String> kk = parseParams(res);
        //System.out.println(kk);
        System.out.println(AlipayNotify.verify(res));
    }

    @Test
    public void test2() {
        String service="alipay.wap.trade.create.direct";
        String v="1.0";
        String sec_id="MD5";
        String notify_data="<notify><payment_type>1</payment_type><subject>yoho order:1605798472</subject><trade_no>2016012321001004360097711805</trade_no><buyer_email>newalipaytest@sina.com</buyer_email><gmt_create>2016-01-23 16:00:03</gmt_create><notify_type>trade_status_sync</notify_type><quantity>1</quantity><out_trade_no>1605798472</out_trade_no><notify_time>2016-01-23 17:24:09</notify_time><seller_id>2088701661478015</seller_id><trade_status>TRADE_SUCCESS</trade_status><is_total_fee_adjust>N</is_total_fee_adjust><total_fee>0.23</total_fee><gmt_payment>2016-01-23 16:00:03</gmt_payment><seller_email>zfb@yoho.cn</seller_email><price>0.23</price><buyer_id>2088802749169369</buyer_id><notify_id>e78828dace8d603a98b1e8f8a5b1b87is0</notify_id><use_coupon>N</use_coupon></notify>";
        String sign = "47887825882a01f50eac6f78813f459c";

        Map<String, String> params = new HashMap<>();
        params.put("service", service);
        params.put("v", v);
        params.put("sec_id", sec_id);
        params.put("notify_data", notify_data);
        params.put("sign", sign);

        System.out.println(AlipayNotify.verify(params));

    }

    public static Map<String, String> parseParams(Map<String, String> requestParams) {

        Map<String, String> params = new HashMap<String, String>();

        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            String name = entry.getKey();
            String values = entry.getValue();
            // String valueStr = String.join(",", values);
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, values);
        }
        return params;
    }
}
