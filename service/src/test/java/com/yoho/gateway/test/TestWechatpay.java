package com.yoho.gateway.test;

import com.yoho.gateway.controller.order.payment.tenpay.util.MD5Util;
import org.junit.Test;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.data.redis.hash.HashMapper;

import java.util.*;

/**
 * Created by ming on 16/1/21.
 */
public class TestWechatpay {

    @Test
    public void testSign() {

        Map<String, String> params = new TreeMap<>();
        params.put("bank_type", "0");
        params.put("discount", "0");
        params.put("fee_type", "1");
        params.put("input_charset", "UTF-8");
        params.put("notify_id", "Ft0l94v3jZAePFeG1IJ79VY5Ia_Y5HfXaSTAjakUgqYafWREYzxz4RNyNheQBiTS3jNAqyugRTOzeTj3d9BvCgiSmYhwGQ2C");
        params.put("out_trade_no", "20160121180533");
        params.put("partner", "1218934901");
        params.put("product_fee", "1");
        params.put("sign_type", "MD5");
        params.put("time_end", "20160121180546");
        params.put("total_fee", "1");
        params.put("trade_mode", "1");
        params.put("trade_state", "0");
        params.put("transaction_id", "1218934901381601211541782252");
        params.put("transport_fee", "0");
      //  params.put("key", "b22de5cfd0ded341e0516505f72649a9");

        StringBuffer sb = new StringBuffer();
        Set es = params.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            String v = (String)entry.getValue();
            if(!"sign".equals(k) && null != v && !"".equals(v)) {
                sb.append(k + "=" + v + "&");
            }
        }

        sb.append("key=" + "b22de5cfd0ded341e0516505f72649a9");

        System.out.println("sb: " + sb.toString());
        String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8").toLowerCase();
        System.out.println("sign: " + sign);

        String preSign = "bank_type=0&discount=0&fee_type=1&input_charset=UTF8&notify_id=Ft0l94v3jZAePFeG1IJ79VY5Ia_Y5HfXaSTAjakUgqYafWREYzxz4RNyNheQBiTS3jNAqyugRTOzeTj3d9BvCgiSmYhwGQ2C&out_trade_no=20160121180533&partner=1218934901&product_fee=1&sign_type=MD5&time_end=20160121180546&total_fee=1&trade_mode=1&trade_state=0&transaction_id=1218934901381601211541782252&transport_fee=0&key=b22de5cfd0ded341e0516505f72649a9";
        System.out.println("sp: " + preSign);
        String sign2 = MD5Util.MD5Encode(preSign, "UTF-8").toLowerCase();
        System.out.println("sign2: " + sign2);
    }

    @Test
    public void testParse() {


    }
}
