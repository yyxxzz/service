package com.yoho.gateway.test;

import com.alibaba.fastjson.JSON;
import com.yoho.gateway.controller.ApiResponse;
import org.junit.Assert;
import org.junit.Test;

/**
 * API Response
 * Created by chunhua.zhang@yoho.cn on 2015/11/16.
 */
public class TestApiResponseBuilder {


    /**
     * <pre>
     *
     *     {
     * "code": 200,
     * "message": "coupons total",
     * "data": {
     * "total": "0"
     * },
     * "md5": "65e4eae39e995ad7cf235f5d036ae701"
     * }
     *
     * </pre>
     */
    @Test
    public void testBuild() {

        ApiResponse.ApiResponseBuilder builder = new ApiResponse.ApiResponseBuilder();

        ApiResponse apiResponse = builder.code(200).message("coupons total").data(new Total("0")).build();


    }


    @Test
    public void testBuildNoData() {

        ApiResponse.ApiResponseBuilder builder = new ApiResponse.ApiResponseBuilder();

        ApiResponse apiResponse = builder.code(500).message("coupons total").build();

        System.out.println(JSON.toJSONString(apiResponse));


    }

    class Total {
        private String total;

        public Total(String total) {
            this.total = total;
        }

        public String getTotal() {
            return total;
        }

    }
}
