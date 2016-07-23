package com.yoho.gateway.model.request;

import lombok.Data;

@Data
public class YohoCoinVO {

	private int uid;
    //yoho币数量
    private int num;
    private int type;
    // 和mars points关联的marsId
    private String marsId;
    
    // mars points中的订单号
    private long orderCode;

    //pid是erp操作人员的uid，logid是记录的这个yoho最原始的id
    private int pid;
}
