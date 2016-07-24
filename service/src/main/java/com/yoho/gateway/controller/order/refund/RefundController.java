package com.yoho.gateway.controller.order.refund;

import com.alibaba.fastjson.JSON;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.cache.UserOrderCache;
import com.yoho.gateway.model.order.RefundGoodsVO;
import com.yoho.gateway.service.order.RefundService;
import com.yoho.service.model.order.model.refund.Goods;
import com.yoho.service.model.order.model.refund.Payment;
import com.yoho.service.model.order.model.refund.UnderscoreGoods;
import com.yoho.service.model.order.model.refund.UnderscorePayment;
import com.yoho.service.model.order.request.RefundGoodsListRequest;
import com.yoho.service.model.order.request.RefundGoodsRequest;
import com.yoho.service.model.order.request.RefundRequest;
import com.yoho.service.model.order.request.RefundSubmitRequest;
import com.yoho.service.model.order.response.PageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * qianjun
 * 2015/12/3
 */
@Controller
public class RefundController {
    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
    private RefundService refundService;
    @Autowired
    private UserOrderCache userOrderCache;
    
    private Logger logger = LoggerFactory.getLogger(RefundController.class);
    
    /**
     * 获取退换货订单列表
     * @param is_blk
     */
    @RequestMapping(params = "method=app.refund.getList")
    @ResponseBody
    public ApiResponse goodsList(@RequestParam("uid") int uid,
                                 @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                 @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        RefundRequest refundRequest = new RefundRequest();
        refundRequest.setUid(uid);
        refundRequest.setPage(page);
        refundRequest.setLimit(limit);

        PageResponse refundOrders = serviceCaller.call("order.getList", refundRequest, PageResponse.class);
        PageResponse<RefundGoodsVO> response = refundService.convertRefundGoodsBO2VO(refundOrders);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(response).build();
    }

    /**
     * 获取退货订单商品列表
     * @param uid  用户ID
     * @param orderCode 订单号
     * @return
     */
    @RequestMapping(params = "method=app.refund.goodsList")
    @ResponseBody
    public ApiResponse goodsList(@RequestParam("uid") int uid, @RequestParam("order_code") long orderCode) {
        RefundGoodsListRequest refundGoodsListRequest = new RefundGoodsListRequest();
        refundGoodsListRequest.setUid(uid);
        refundGoodsListRequest.setOrderCode(orderCode);
        Map<String, Object> refundGoods = serviceCaller.call("order.goodsList", refundGoodsListRequest, Map.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("Refund goods list").data(refundGoods).build();
    }

    /**
     * 提交退货申请
     *
     * @param orderCode 订单号
     * @param uid 用户ID
     * @param areaCode 地区编号
     * @param goodsList 退货商品
     * @param payment 退款
     * @return
     */
    @RequestMapping(params = "method=app.refund.submit")
    @ResponseBody
    public ApiResponse submit(@RequestParam("order_code") long orderCode, @RequestParam("uid") int uid,
                              @RequestParam(value = "area_code", required = false, defaultValue = "") String areaCode,
                              @RequestParam("goods") String goodsList,
                              @RequestParam("payment") String payment,
                              @RequestParam(value = "app_type", required = false, defaultValue = "0") int app_type) {
        RefundSubmitRequest refundSubmitRequest = new RefundSubmitRequest();
        refundSubmitRequest.setUid(uid);
        refundSubmitRequest.setOrderCode(orderCode);
        refundSubmitRequest.setAreaCode(areaCode);
        List<UnderscoreGoods> underscoreGoodses=JSON.parseArray(goodsList,UnderscoreGoods.class);
        List<Goods> goodsRequest = refundService.underscoreGoodsToGoods(underscoreGoodses);
        UnderscorePayment underscorePayment=JSON.parseObject(payment,UnderscorePayment.class);
        Payment paymentRequest = refundService.underscorePaymentToPayment(underscorePayment);
        refundSubmitRequest.setGoodsList(goodsRequest);
        refundSubmitRequest.setPayment(paymentRequest);
        Map<String, Object> applyId = serviceCaller.call("order.submit", refundSubmitRequest, Map.class);
        //退货后，清除订单各类总数统计信息缓存
        userOrderCache.clearRefundOrderCountCache(uid);
        return new ApiResponse.ApiResponseBuilder().code(200).message("退货申请成功").data(applyId).build();
    }

    /**
     * 退货详情
     * @param id 退货申请ID
     * @param uid 用户ID
     * @return
     */
    @RequestMapping(params = "method=app.refund.detail")
    @ResponseBody
    public ApiResponse detail(@RequestParam("id") int id, @RequestParam("uid") int uid) {
        RefundGoodsRequest refundGoodsRequest = new RefundGoodsRequest();
        refundGoodsRequest.setId(id);
        refundGoodsRequest.setUid(uid);
        Map<String, Object> refundGoodsList = serviceCaller.call("order.detail", refundGoodsRequest, Map.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("退货详情").data(refundGoodsList).build();
    }

    /**
     * 保存快递信息
     * @param id 退货申请ID
     * @param uid 用户ID
     * @param expressCompany 快递公司名称
     * @param expressNumber 快递单号
     * @param expressId 快递公司ID
     * @return
     */
    @RequestMapping(params = "method=app.refund.setexpress")
    @ResponseBody
    public ApiResponse setExpress(@RequestParam("id") int id,
                                  @RequestParam("uid") int uid,
                                  @RequestParam("express_company") String expressCompany,
                                  @RequestParam("express_number") String expressNumber,
                                  @RequestParam("express_id") int expressId) {
        RefundGoodsRequest refundGoodsRequest = new RefundGoodsRequest();
        refundGoodsRequest.setId(id);
        refundGoodsRequest.setUid(uid);
        refundGoodsRequest.setExpressCompany(expressCompany);
        refundGoodsRequest.setExpressNumber(expressNumber);
        refundGoodsRequest.setExpressId(expressId);
        serviceCaller.call("order.setExpress", refundGoodsRequest, void.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("快递单号设置成功").build();
    }
    
    /**
     * 取消退货
     *
     * @param id  退货表id
     * @param uid 
     */
    @RequestMapping(params = "method=app.refund.cancel")
    @ResponseBody
    public ApiResponse refundCancel(@RequestParam("id") int id, @RequestParam("uid") int uid) {
        RefundGoodsRequest refundGoodsRequest = new RefundGoodsRequest();
        refundGoodsRequest.setId(id);
        refundGoodsRequest.setUid(uid);
        logger.info("refundCancel req is refundId:{},uid:{}", id,uid);
        serviceCaller.call("order.refundCancel", refundGoodsRequest, void.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("取消退货成功").build();
    }
}





































