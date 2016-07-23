package com.yoho.gateway.model.bigdata;


import com.yoho.gateway.model.PageRequestBase;

import lombok.Data;

@Data
public class OrdersGoodsVo extends PageRequestBase {

    // 1 日 2 周 3 月
    private String type;

    //日 月请求日期 20160501
    private String reqTime;

    //周报起始时间 20160521
    private String beginTime;
    //周报起始时间 20160527
    private String endTime;

    private String productSkn;

    private String productSkc;

    private String productSku;

    private String factoryCode;

    // 可能是 1级 2级 三级分类
    private String sortId;

    private String shopId;

    private String brandId;

}