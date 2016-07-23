package com.yoho.gateway.model.order;

import com.alibaba.fastjson.annotation.JSONField;
import com.yoho.gateway.model.sns.OrderGoodsVo;

import java.util.List;

/**
 * sunjiexiang
 * 2015/12/9
 */
public class RefundGoodsVO {
    /**
     * 退货ID
     */
    private String id;

    /**
     * 订单号
     */
    @JSONField(name="order_code")
    private String orderCode;

    /**
     * 商品集合
     */
    private List<OrdersGoodsVO> goods;

    /**
     * 状态
     * 0   提交
     * 10 审核通过
     * 20 商品寄回
     * 30 已入库
     * 40 付款结束
     * 91 客服拒退
     */
    private String status;

    /**
     * 状态名
     */
    @JSONField(name="status_name")
    private String statusName;

    /**
     * 退货类型
     */
    @JSONField(name="refund_type")
    private Byte refundType;

    /**
     * 创建日期
     */
    @JSONField(name="create_time")
    private String createTime;

    /**
     * 原始订单创建时间
     */
    @JSONField(name = "order_create_time")
    private String orderCreateTime;
    
    /**
     * 是否支持取消
     */
    private String canCancel;

    public String getCanCancel() {
        return canCancel;
    }

    public void setCanCancel(String canCancel) {
        this.canCancel = canCancel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }

    public List<OrdersGoodsVO> getGoods() {
        return goods;
    }

    public void setGoods(List<OrdersGoodsVO> goods) {
        this.goods = goods;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Byte getRefundType() {
        return refundType;
    }

    public void setRefundType(Byte refundType) {
        this.refundType = refundType;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOrderCreateTime() {
        return orderCreateTime;
    }

    public void setOrderCreateTime(String orderCreateTime) {
        this.orderCreateTime = orderCreateTime;
    }
}
