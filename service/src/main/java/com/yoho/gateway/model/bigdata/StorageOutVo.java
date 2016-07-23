package com.yoho.gateway.model.bigdata;


import com.yoho.gateway.model.PageRequestBase;
import lombok.Data;

@Data
public class StorageOutVo extends PageRequestBase {

    //起始时间
    private String beginTime;

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