package com.yoho.gateway.test;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.controller.order.payment.common.TimeUtil;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ming on 16/1/20.
 */
public class TestAbstractController {

    @Test
    public void testGetCurrentTime() {
        String res = TimeUtil.getCurrentTime();

        System.out.println(res);
    }

    @Test
    public void testGetExpireTime() {
        int time = 1453286665;
        String times = "20160120204425";

        String res = TimeUtil.getExpireTime(time);

        Assert.assertEquals(res, times);
        System.out.println(res);
    }

    @Test
    public void testErpResp() {
        String json = "{\"code\":\"200\",\"message\":\"\\u4fee\\u6539\\u6210\\u529f.\",\"data\":{\"row_count\":0,\"order_code\":\"1610909588\",\"product_storage\":{\"580683\":21},\"curYohoCoin\":\"4000\"},\"md5\":\"cfd07dd4450fa3904458a6482f7c9c31\"}";

        JSONObject jsonObject = JSONObject.parseObject(json);
        int code = jsonObject.getIntValue("code");

        System.out.println(code);

    }
}
