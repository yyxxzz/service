package com.yoho.gateway.test;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Created by ming on 16/1/15.
 */
public class TestPayment {

    @Test
    public void testBigDecimal() {

        String txnAmt = "9910";
        double total_fee = Double.parseDouble(txnAmt);  // 单位为分
        BigDecimal bdec = new BigDecimal(total_fee * 0.01);

        System.out.println(bdec);
        bdec = bdec.setScale(2, BigDecimal.ROUND_HALF_UP);

        System.out.println(bdec);

        DecimalFormat df = new DecimalFormat("0.00");
        System.out.println(df.format(bdec));

        Assert.assertEquals(bdec.toString(), "99.10");

    }
}
