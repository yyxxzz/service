package com.yoho.gateway.model.bigdata.vo;


import lombok.Data;

import java.io.Serializable;
//近七天统计数据
@Data
public class OrdersGoodsLast7DaysVO implements Serializable {

    private static final long serialVersionUID = 2313485800853429669L;

    //统计日期
    private String dateId;

    private String allNums;

    private String allAmounts;

}