package com.yoho.gateway.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import com.yoho.gateway.controller.order.UnionpayController;
import com.yoho.gateway.controller.order.payment.common.TimeUtil;
import com.yoho.gateway.controller.order.payment.unionpay.SDKConfig;
import com.yoho.gateway.controller.order.payment.unionpay.SDKUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ming on 16/1/20.
 */
public class TestUnionpay {

    @Test
    public void testFormatTime() {
        String time = "20160120165432";
        String time2 = "2016-01-20 16:54:32";

        String conv = TimeUtil.formatTime(time);

        Assert.assertEquals(time2, conv);
        System.out.println(conv);
    }

    @Test
    public void testSingleTradeQuery() {

        SDKConfig.getConfig().loadPropertiesFromPath("/Users/ming/source/tmp/yoho-pay/web/src/main/resources/");
        String dirpath = "/Users/ming/source/tmp/yoho-pay/web/src/main/resources/certs/";
        SDKConfig.getConfig().setValidateCertDir(dirpath);
        //SDKConfig.getConfig().setSignCertPath(dirpath + "pc_online_banking.pfx");
        SDKConfig.getConfig().setSignCertPath(dirpath + "PM_700000000000001_acp.pfx");

        System.out.println("ABS PATH: " + dirpath);


        String orderCode="1607926273"; // "1618759582";
        String txnTime = "20160121135752"; // "20160115155054";
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
        // 交易类型 00
        data.put("txnType", "00");
        // 交易子类型 默认00
        data.put("txnSubType", "00");
        // 业务类型
        data.put("bizType", "000000");
        // 接入类型
        data.put("accessType", "0");
        // 商户代码
        data.put("merId",  "700000000000001"); // "898111453110466");
        // 订单发送时间
        data.put("txnTime", txnTime);
        // 商户订单号
        data.put("orderId", orderCode);
        // 交易查询流水号
        // "queryId"
        // 保留域
        // "reserved"


        Map<String, String> submitFromData = SDKUtil.signData(data, SDKUtil.encoding_UTF8);
        String singleQueryUrl = SDKConfig.getConfig().getSingleQueryUrl();
        //String singleQueryUrl = SDKConfig.getConfig().getAppRequestUrl();

        Map<String, String> resmap = SDKUtil.submitUrl(submitFromData, singleQueryUrl, SDKUtil.encoding_UTF8);

        System.out.println(resmap);
    }

    @Test
    public void testUnixTimeStamp () {
        int time = 1309259070;
        String timeS = "20110628190430";
        String res = UnionpayController.unixTimeStamp(time);

        Assert.assertEquals(res, timeS);
        System.out.println(res);
    }
}
