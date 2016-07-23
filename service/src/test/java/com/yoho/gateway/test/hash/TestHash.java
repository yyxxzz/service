package com.yoho.gateway.test.hash;

import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

/**
 * Created by chunhua.zhang@yoho.cn on 2016/2/27.
 */
public class TestHash {

    @Test
    public void test() throws UnsupportedEncodingException {

        BigInteger bigInteger = new FNV().fnv1a_64("yh:user:browse_10216497".getBytes("UTF-8"));

        System.out.println(bigInteger.intValue() % 32);
    }
}
