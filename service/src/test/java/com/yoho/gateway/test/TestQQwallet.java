package com.yoho.gateway.test;

import com.yoho.gateway.controller.order.QQwalletController;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ming on 16/1/22.
 */
public class TestQQwallet {

    @Test
    public void testSign() {
        String params = "appId=100703379&bargainorId=1900000109&nonce=1453440672087&pubAcc=&tokenId=1V626ce6dda14fbf54a1f4634d877744";
        String appKey = "4578e54fb3a1bd18e0681bc1c734514e";

        String signGood = "V6bkxyZUBCmczHS4n6txVqzI5kI=";

        String sign = QQwalletController.sign(params, appKey);
        Assert.assertEquals(sign, signGood);
        System.out.println(sign);
    }
}
