package com.yoho.gateway.model.bigdata.vo;

import lombok.Data;

//总数 金额汇总统计
@Data
public class NumAmountVO {

    private Long allNumber;

    private Double allAmount;

    //上期数据汇总
    private Long lastAllNumber;

    private Double lastAllAmount;

    //环比
    private Double allNumberRate;
    //是否有环比判断 true 有 false 无
    private boolean allNumberRateFlag=false ;

    private Double allAmountRate;

    //是否有环比判断 true 有 false 无
    private boolean allAmountRateFlag=false ;

    //退货率 = 退货金额 / 交易金额 * 100%
    private Double returnAmountRate;

    //是否有环比判断 true 有 false 无
    private boolean returnAmountRateFlag=false ;

    private Double lastReturnAmountRate ;

    //退货率环比
    private Double returnRingRate ;
    //    //是否有环比判断 true 有 false 无
    private boolean returnRingRateFlag=false ;


}