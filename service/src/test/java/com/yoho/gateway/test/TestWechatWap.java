package com.yoho.gateway.test;

import com.yoho.gateway.controller.order.payment.wechat.PayCommonUtil;
import com.yoho.gateway.controller.order.payment.wechat.XMLUtil;
import org.jdom.JDOMException;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by ming on 16/1/25.
 */
public class TestWechatWap {

    @Test
    public void testWapSign () {

        String xml = "<xml><appid><![CDATA[wx75e5a7c0c88e45c2]]></appid>\n" +
                "<bank_type><![CDATA[CFT]]></bank_type>\n" +
                "<cash_fee><![CDATA[12]]></cash_fee>\n" +
                "<fee_type><![CDATA[CNY]]></fee_type>\n" +
                "<is_subscribe><![CDATA[Y]]></is_subscribe>\n" +
                "<mch_id><![CDATA[1227694201]]></mch_id>\n" +
                "<nonce_str><![CDATA[1e9dtv8ut47tttooaz1re0anode5wnyw]]></nonce_str>\n" +
                "<openid><![CDATA[oemqmjjLf52DtIg7fbiKzt4N_cIg]]></openid>\n" +
                "<out_trade_no><![CDATA[YOHOBuy_1610510159]]></out_trade_no>\n" +
                "<result_code><![CDATA[SUCCESS]]></result_code>\n" +
                "<return_code><![CDATA[SUCCESS]]></return_code>\n" +
                "<sign><![CDATA[626E989C72128D8C24556D4076F5097D]]></sign>\n" +
                "<time_end><![CDATA[20160123172914]]></time_end>\n" +
                "<total_fee>12</total_fee>\n" +
                "<trade_type><![CDATA[JSAPI]]></trade_type>\n" +
                "<transaction_id><![CDATA[1005600555201601232860109919]]></transaction_id>\n" +
                "</xml>";

        SortedMap res=null;
        try {
            res = XMLUtil.doXMLParse(xml);
        } catch (JDOMException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (res != null) {
            String signs = PayCommonUtil.createSign("UTF-8", res);
            System.out.println(signs);
            System.out.println("map: " + res);
        }


    }
}
