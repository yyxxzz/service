package com.yoho.gateway.controller.order;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.common.restbean.ResponseBean;
import com.yoho.core.message.YhProducerTemplate;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.cache.UserOrderCache;
import com.yoho.gateway.controller.order.payment.common.PayResult;
import com.yoho.gateway.model.order.HistoryOrderVO;
import com.yoho.gateway.service.order.OrderService;
import com.yoho.gateway.utils.PhoneUtil;
import com.yoho.product.model.GoodsImagesBo;
import com.yoho.service.model.order.OrderServices;
import com.yoho.service.model.order.OrderStatusDesc;
import com.yoho.service.model.order.model.PaymentBO;
import com.yoho.service.model.order.model.simple.SimpleIntBO;
import com.yoho.service.model.order.payment.OrdersPayRefundRequest;
import com.yoho.service.model.order.request.*;
import com.yoho.service.model.order.response.*;
import com.yoho.service.model.order.utils.ClientTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by yoho on 2015/11/20.
 */
@Controller
public class YohoOrderController {

    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
    private OrderService orderService;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    protected YhProducerTemplate producerTemplate;

    @Value("${erp.order.status.url}")
    private String erpOrderStatusUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserOrderCache userOrderCache;

    private static final String PAYMENT_FINISH_MQ_TOPTIC = "order.payment";

    /**
     * 根据订单号,更新订单支付方式
     *
     * @param orderCode
     * @return
     */
    @RequestMapping(params = "method=app.SpaceOrders.updateOrdersPaymentByCode")
    @ResponseBody
    public ApiResponse updateOrdersPaymentByCode(@RequestParam("order_code") Long orderCode, @RequestParam("payment") Byte payment) {
        logger.info("UpdateOrdersPaymentByCode start, order_code is {} and payment is {}.", orderCode, payment);
        OrderRequest request = new OrderRequest();
        request.setOrderCode(orderCode);
        request.setPayment(payment);
        serviceCaller.call(OrderServices.updateOrdersPaymentByCode, request, Void.class);
        logger.info("UpdateOrdersPaymentByCode success, order_code is {} and payment is {}.", orderCode, payment);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").build();
    }


    /**
     * 根据订单号取消订单
     *
     * @return
     */
    @RequestMapping(params = "method=app.SpaceOrders.close")
    @ResponseBody
    public ApiResponse closeOrderByCode(@RequestParam(value = "order_code", required = true) long orderCode,
                                        @RequestParam(value = "uid", required = false, defaultValue = "0") int uid,
                                        @RequestParam(value = "client_type", required = false, defaultValue = ClientTypeUtils.H5) String clientType,
                                        @RequestParam(value = "reason_id", required = false) String reasonId,
                                        @RequestParam(value = "reason", required = false) String reason) {
        logger.info("CloseOrderByCode start, order_code is {} uid is {} client_type is {} reason_id is {} reason is {}.", orderCode, uid, clientType, reasonId, reason);
        if (uid == 0) {
            logger.info("{} please give me uid for app.SpaceOrders.close api.", clientType);
            OrderRequest request = new OrderRequest();
            request.setOrderCode(orderCode);
            uid = serviceCaller.call(OrderServices.FIND_UID_BY_ORDER_CODE, request, SimpleIntBO.class).getValue();
        }
        OrderCancelRequest orderCancelRequest = new OrderCancelRequest();
        orderCancelRequest.setOrderCode(orderCode);
        orderCancelRequest.setUid(uid);
        orderCancelRequest.setClientType(clientType);
        orderCancelRequest.setReasonId(reasonId);
        orderCancelRequest.setReason(reason);
        serviceCaller.call(OrderServices.closeOrderByCode, orderCancelRequest, Void.class);
        userOrderCache.clearOrderCountCache(uid);
        logger.info("CloseOrderByCode success, order_code is {}.", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("订单取消成功").build();
    }


    /**
     * 订单取消原因列表
     *
     * @return
     */
    @RequestMapping(params = "method=app.SpaceOrders.closeReasons")
    @ResponseBody
    public ApiResponse closeOrderReasons() {
        logger.info("closeOrderReasons start");
        JSONArray x = closeOrderReasonsLocal();
        logger.info("closeOrderReasons success");
        return new ApiResponse.ApiResponseBuilder().code(200).data(x).message("订单取消原因列表").build();
    }

    private JSONArray closeOrderReasonsLocal() {
        JSONArray array = new JSONArray();
        for (int i = 1; i <= 10; i++) {
            JSONObject obj = new JSONObject();
            obj.put("id", i + "");
            obj.put("reason", reasonConvert(i));
            array.add(obj);
        }
        return array;
    }

    private String reasonConvert(int id) {
        if (id == 1) {
            return "支付不成功";
        } else if (id == 2) {
            return "现在不想购买";
        } else if (id == 3) {
            return "订单价格有问题";
        } else if (id == 4) {
            return "修改订单信息";
        } else if (id == 5) {
            return "错误或重复下单";
        } else if (id == 6) {
            return "忘记使用优惠劵或YOHO币";
        } else if (id == 7) {
            return "等待发货时间过长";
        } else if (id == 8) {
            return "商品价格较贵";
        } else if (id == 9) {
            return "运费过高";
        } else {
            return "其他";
        }
    }

    /**
     * 根据订单号确认订单
     *
     * @return
     */
    @RequestMapping(params = "method=app.SpaceOrders.confirm")
    @ResponseBody
    public ApiResponse confirmOrderByCode(@RequestParam(value = "order_code", required = true) long orderCode,
                                          @RequestParam(value = "uid", required = false, defaultValue = "0") int uid,
                                          @RequestParam(value = "client_type", required = false, defaultValue = ClientTypeUtils.H5) String clientType) {
        logger.info("ConfirmOrderByCode start, order_code is {}.", orderCode);
        if (uid == 0) {
            logger.info("{} please give me uid for app.SpaceOrders.confirm api.", clientType);
            OrderRequest request = new OrderRequest();
            request.setOrderCode(orderCode);
            uid = serviceCaller.call(OrderServices.FIND_UID_BY_ORDER_CODE, request, SimpleIntBO.class).getValue();
        }
        Orders orders = new Orders();
        orders.setOrderCode(orderCode);
        serviceCaller.call(OrderServices.confirmOrderByCode, orders, Void.class);
        userOrderCache.clearOrderCountCache(uid);
        logger.info("ConfirmOrderByCode success, order_code is {}.", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("订单确认成功").build();
    }

    @RequestMapping(params = "method=app.SpaceOrders.success")
    @ResponseBody
    public ApiResponse success(@RequestParam("uid") Integer uid,
                               @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit) {
        OrderSuccessRequest request = new OrderSuccessRequest();
        request.setLimit(limit);
        request.setUid(uid);
        OrderSuccessResponse response = serviceCaller.call(OrderServices.success, request, OrderSuccessResponse.class);
        return new ApiResponse.ApiResponseBuilder().code(200).data(response).message("SpaceOrders success list").build();
    }

    /**
     * 根据订单号删除订单
     *
     * @return
     */
    @RequestMapping(params = "method=app.SpaceOrders.delOrderByCode")
    @ResponseBody
    public ApiResponse deleteOrderByCode(@RequestParam(value = "order_code", required = true) long orderCode,
                                         @RequestParam(value = "uid", required = false, defaultValue = "0") int uid,
                                         @RequestParam(value = "client_type", required = false, defaultValue = ClientTypeUtils.H5) String clientType) {
        logger.info("DeleteOrderByCode start, order_code is {}.", orderCode);
        if (uid == 0) {
            logger.info("{} please give me uid for app.SpaceOrders.delOrderByCode api.", clientType);
            OrderRequest request = new OrderRequest();
            request.setOrderCode(orderCode);
            uid = serviceCaller.call(OrderServices.FIND_UID_BY_ORDER_CODE, request, SimpleIntBO.class).getValue();
        }
        Orders orders = new Orders();
        orders.setOrderCode(orderCode);
        orders.setUid(uid);
        serviceCaller.call(OrderServices.deleteOrderByCode, orders, Void.class);
        logger.info("DeleteOrderByCode success, order_code is {}.", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("删除成功").build();
    }


    /**
     * 根据订单号获取电子票列表
     *
     * @return
     */
    @RequestMapping(params = "method=app.SpaceOrders.getQrByOrderCode")
    @ResponseBody
    public ApiResponse getQrByOrderCode(@RequestParam(value = "order_code", required = true) long orderCode,
                                        @RequestParam(value = "uid", required = false, defaultValue = "0") int uid,
                                        @RequestParam(value = "client_type", required = false, defaultValue = ClientTypeUtils.H5) String clientType) {
        logger.info("GetQrByOrderCode start, order_code is {} uid is {} client_type is {}.", orderCode, uid, clientType);
        if (uid == 0) {
            logger.info("{} please give me uid for app.SpaceOrders.getQrByOrderCode api.", clientType);
            OrderRequest request = new OrderRequest();
            request.setOrderCode(orderCode);
            uid = serviceCaller.call(OrderServices.FIND_UID_BY_ORDER_CODE, request, SimpleIntBO.class).getValue();
        }
        Orders orders = new Orders();
        orders.setOrderCode(orderCode);
        orders.setUid(uid);
        TicketsQr qr = serviceCaller.call(OrderServices.getQrByOrderCode, orders, TicketsQr.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(qr).build();
    }


    /**
     * 待晒单列表
     * 1：全部订单
     * 2：待付款
     * 3：待发货
     * 4：待收货
     * 5：待评论  成功订单
     * 7：失败 取消 订单
     */
    @RequestMapping(params = "method=app.SpaceOrders.getShareOrder")
    @ResponseBody
    public ApiResponse getMyShareOrders(@RequestParam("type") int type,
                                        @RequestParam("uid") Integer uid,
                                        @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                        @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        return getMyOrders(type, uid, page, limit);
    }

    /**
     * 我的订单
     * 1：全部订单
     * 2：待付款
     * 3：待发货
     * 4：待收货
     * 5：待评论  成功订单
     * 7：失败 取消 订单
     */
    @RequestMapping(params = "method=app.SpaceOrders.get")
    @ResponseBody
    public ApiResponse getMyOrders(@RequestParam("type") int type,
                                   @RequestParam("uid") Integer uid,
                                   @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                   @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        OrderListRequest request = new OrderListRequest();
        request.setType(type);
        request.setLimit(limit);
        request.setPage(page);
        request.setUid(uid.toString());
        CountBO countBO = serviceCaller.call(OrderServices.getOrderCount, request, CountBO.class);
        Orders[] ordeses = serviceCaller.call(OrderServices.getOrderList, request, Orders[].class);
        JSONObject data = createMyOrdersVO(page, limit, countBO.getCount(), ordeses);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(data).build();
    }

    private String goodsTypeConvert(int orderType) {
        if (orderType == 1) {
            return "ordinary";
        } else if (orderType == 2) {
            return "gift";
        } else if (orderType == 3) {
            return "price_gift";
        } else if (orderType == 4) {
            return "outlet";
        } else if (orderType == 5) {
            return "free";
        } else if (orderType == 6) {
            return "advance";
        } else if (orderType == 7) {
            return "ticket";
        } else {
            return "ordinary";
        }
    }

    private String formatDate(Integer time, String pattern) {
        if (time == null || pattern == null) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date((long) time * 1000));
    }

    private JSONObject createMyOrdersVO(int page, int limit, int count, Orders[] ordeses) {
        JSONArray order_list = new JSONArray();
        for (Orders orders : ordeses) {

            JSONObject order = new JSONObject();
            order.put("id", toString(orders.getId()));
            order.put("order_code", toString(orders.getOrderCode()));
            order.put("order_type", orders.getOrderType());
            order.put("refund_status", orders.getRefundStatus());
            order.put("uid", toString(orders.getUid()));
            order.put("parent_order_code", toString(orders.getParentOrderCode()));
            order.put("create_time", toString(orders.getCreateTime()));
            order.put("update_time", toString(orders.getUpdateTime()));
            order.put("pay_expire", formatDate(orders.getCreateTime() + 7200, "yyyy-MM-dd HH:mm:ss"));
            order.put("payment", toString(orders.getPayment()));
            order.put("amount", toString(orders.getAmount()));
            order.put("payment_status", orders.getPaymentStatus());
            order.put("user_confirm_paid", orders.getUser_confirm_paid());
            order.put("use_limit_code", orders.getUse_limit_code());
            order.put("payment_type", toString(orders.getPaymentType()));
            order.put("payment_type", toString(orders.getPaymentType()));
            order.put("payment_type", toString(orders.getPaymentType()));
            order.put("counter_flag", "Y");
            //倒计时时间
            int leftTime = orders.getCreateTime() + 7200 - (int) (System.currentTimeMillis() / 1000);
            if (Orders.PAYMENT_TYPE_ONLINE.equals(orders.getPaymentType()) && leftTime > 0) {
                order.put("pay_lefttime", "" + leftTime);
            } else {
                order.put("pay_lefttime", "0");
            }
            order.put("status", orders.getStatus());
            if ("Y".equals(orders.getPaymentStatus()) && orders.getStatus() == 0) {
                order.put("status", 1);
            }
            OrderStatusDesc orderStatusDesc = OrderStatusDesc.valueOf(orders.getPaymentType(), order.getIntValue("status"));
            order.put("status_str", "Y".equals(orders.getIsCancel()) ? "已取消" : orderStatusDesc.getStatusDesc());
            order.put("payment_type_str", orderStatusDesc.getPaymentTypeDesc());
            order.put("shipping_cost", toString(orders.getShippingCost()));
            order.put("is_cancel", orders.getIsCancel());
            order.put("is_comment", orders.getIsComment());
            order.put("attribute", toString(orders.getAttribute()));
            List<OrdersGoods> ordersGoodses = orders.getOrdersGoodsList();
            int buy_total = 0;
            JSONArray order_goods = new JSONArray();
            if (ordersGoodses != null && !ordersGoodses.isEmpty()) {
                for (OrdersGoods ordersGoods : ordersGoodses) {
                    JSONObject goods = new JSONObject();
                    goods.put("size_name", ordersGoods.getSizeName());
                    goods.put("color_name", ordersGoods.getColorName());
                    goods.put("buy_number", ordersGoods.getNum());
                    buy_total += ordersGoods.getNum();
                    goods.put("product_id", ordersGoods.getProductId());
                    goods.put("product_skn", ordersGoods.getProductSkn());
                    goods.put("product_name", ordersGoods.getProductName());
                    goods.put("cn_alphabet", ordersGoods.getCnAlphabet());

                    goods.put("goods_id", ordersGoods.getGoodsId());
                    goods.put("goods_price", toString(ordersGoods.getGoodsPrice()));
                    goods.put("goods_amount", toString(ordersGoods.getGoodsAmount()));
                    goods.put("goods_image", "");
                    if (ordersGoods.getGoodsImg() != null && !ordersGoods.getGoodsImg().isEmpty()) {
                        GoodsImagesBo retImg = null;
                        for (GoodsImagesBo goodsImagesBo : ordersGoods.getGoodsImg()) {
                            if ("Y".equals(goodsImagesBo.getIsDefault()) && goodsImagesBo.getProductId().equals(ordersGoods.getProductId())) {
                                retImg = goodsImagesBo;
                            } else if (goodsImagesBo.getProductId() != null && goodsImagesBo.getProductId().equals(ordersGoods.getProductId())) {
                                goods.put("goods_image", goodsImagesBo.getImageUrl());
                            }
                        }
                        if (retImg != null) {
                            goods.put("goods_image", retImg.getImageUrl());
                        }
                    }
                    goods.put("goods_type", goodsTypeConvert(ordersGoods.getGoodsType()));
                    goods.put("expect_arrival_time", ordersGoods.getExpectArrivalTime() == null ? "" : ordersGoods.getExpectArrivalTime());
                    goods.put("refund_num", ordersGoods.getRefundNum());
                    order_goods.add(goods);
                }
            }
            order.put("virtual_type", orders.getVirtualType());
            order.put("buy_total", buy_total);
            order.put("order_goods", order_goods);
            order_list.add(order);
        }
        JSONObject data = new JSONObject();
        data.put("order_list", order_list);
        data.put("page_total", getPageTotal(count, limit));
        data.put("page", page);
        data.put("total", count);
        return data;
    }

    private int getPageTotal(int total, int pageSize) {
        return (int) Math.ceil(total * 1.0 / pageSize);
    }

    private String toString(Object obj) {
        if (obj == null) {
            return null;
        } else {
            return obj.toString();
        }
    }


    /**
     * 获取物流详情信息
     */
    @RequestMapping(params = "method=app.express.get")
    @ResponseBody
    public ApiResponse get(@RequestParam(value = "order_code", required = true) long orderCode,
                           @RequestParam(value = "uid", required = false, defaultValue = "0") int uid,
                           @RequestParam(value = "client_type", required = false, defaultValue = ClientTypeUtils.H5) String clientType) {
        logger.info("Get express for order {}.", orderCode);
        return li(orderCode, uid, clientType);
    }

    /**
     * 获取物流详情信息
     */
    @RequestMapping(params = "method=app.express.li")
    @ResponseBody
    public ApiResponse li(@RequestParam(value = "order_code", required = true) long orderCode,
                          @RequestParam(value = "uid", required = false, defaultValue = "0") int uid,
                          @RequestParam(value = "client_type", required = false, defaultValue = ClientTypeUtils.H5) String clientType) {
        logger.info("Get express li for order {}.", orderCode);
        if (uid == 0) {
            logger.info("{} please give me uid for app.express.li api.", clientType);
            OrderRequest request = new OrderRequest();
            request.setOrderCode(orderCode);
            uid = serviceCaller.call(OrderServices.FIND_UID_BY_ORDER_CODE, request, SimpleIntBO.class).getValue();
        }
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderCode(orderCode);
        orderRequest.setUid(uid);
        ResponseBean responseBean = serviceCaller.call(OrderServices.FIND_LOGISTICS_DETAIL, orderRequest, ResponseBean.class);
        logger.info("Get express li for order {} success.", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(Integer.parseInt(responseBean.getCode())).data(responseBean.getData()).message(responseBean.getMessage()).build();
    }

    @RequestMapping(params = "method=app.SpaceOrders.info")
    @ResponseBody
    public ApiResponse info(@RequestParam("order_code") String orderCode, @RequestParam("uid") String uid) {
        logger.info("Get order info for order {}.", orderCode);
        OrderInfoResponse orderInfoResponse = getOrderDetail(orderCode, uid);
        orderInfoResponse.setPromotionFormulas(null);
        logger.info("Get order info for order {} success.", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("OK").data(orderInfoResponse).build();
    }


    @RequestMapping(params = "method=app.SpaceOrders.detail")
    @ResponseBody
    public ApiResponse detail(@RequestParam(value = "order_code", required = true) long orderCode,
                              @RequestParam(value = "uid", required = false, defaultValue = "0") int uid,
                              @RequestParam(value = "client_type", required = false, defaultValue = ClientTypeUtils.H5) String clientType) {
        logger.info("Get order detail for order {}.", orderCode);
        if (uid == 0) {
            logger.info("{} please give me uid for app.SpaceOrders.detail api.", clientType);
            OrderRequest request = new OrderRequest();
            request.setOrderCode(orderCode);
            uid = serviceCaller.call(OrderServices.FIND_UID_BY_ORDER_CODE, request, SimpleIntBO.class).getValue();
        }
        OrderInfoResponse orderInfoResponse = getOrderDetail(String.valueOf(orderCode), String.valueOf(uid));
        orderInfoResponse.setGoods_total_amount(null);
        orderInfoResponse.setPromotion_amount(null);
        orderInfoResponse.setShipping_cost(null);
        orderInfoResponse.setCoupons_amount(null);
        orderInfoResponse.setYoho_coin_num(null);
        logger.info("Get order detail for order {} success.", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("OK").data(orderInfoResponse).build();
    }

    private OrderInfoResponse getOrderDetail(String orderCode, String uid) {
        OrderDetailRequest orderDetailRequest = new OrderDetailRequest();
        orderDetailRequest.setOrderCode(orderCode);
        orderDetailRequest.setUid(uid);
        OrderInfoResponse orderInfoResponse = serviceCaller.call(OrderServices.orderDetail, orderDetailRequest, OrderInfoResponse.class);
        if (orderInfoResponse.getMobile() != null) {
            orderInfoResponse.setMobile(PhoneUtil.coverMobile(orderInfoResponse.getMobile()));
        }
        orderInfoResponse.setPay_expire(formatDate(Integer.parseInt(orderInfoResponse.getCreate_time()) + 7200, "yyyy-MM-dd HH:mm:ss"));
        orderInfoResponse.setCounter_flag("Y");
        //倒计时时间
        int leftTime = Integer.parseInt(orderInfoResponse.getCreate_time()) + 7200 - (int) (System.currentTimeMillis() / 1000);
        if ("1".equals(orderInfoResponse.getPayment_type()) && leftTime > 0) {
            orderInfoResponse.setPay_lefttime("" + leftTime);
        } else {
            orderInfoResponse.setPay_lefttime("" + 0);
        }
        return orderInfoResponse;
    }

    // end

    /**
     * 根据uid,获取订单状态统计信息
     *
     * @return
     */
    @RequestMapping(params = "method=app.SpaceOrders.getOrdersStatusStatisticsByUid")
    @ResponseBody
    public ApiResponse getOrdersStatusStatisticsByUid(@RequestParam("uid") Integer uid) {
        OrdersStatusStatisticsRequest request = new OrdersStatusStatisticsRequest();
        request.setUid(uid);
        OrdersStatusStatistics data = serviceCaller.call(OrderServices.getOrdersStatusStatisticsByUid, request, OrdersStatusStatistics.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(data).build();
    }

    /**
     * 根据uid,status,获取订单数量
     *
     * @param uid
     * @return
     */
    @RequestMapping(params = "method=app.SpaceOrders.getOrdersCountByUidAndStatus")
    @ResponseBody
    public ApiResponse getOrdersCountByUidAndStatus(@RequestParam("uid") Integer uid,
                                                    @RequestParam(value = "status", required = false, defaultValue = "") String status) {
        OrdersStatusStatisticsRequest request = new OrdersStatusStatisticsRequest();
        request.setUid(uid);
        if (StringUtils.hasText(status)) {
            String[] statusArray = status.split(",");
            List<Integer> statusList = new ArrayList<>();
            for (String statusStr : statusArray) {
                try {
                    statusList.add(Integer.valueOf(statusStr));
                } catch (NumberFormatException e) {
                    return new ApiResponse.ApiResponseBuilder().code(400).message("Format string[" + statusStr + "] to number error.").build();
                }
            }
            request.setStatus(statusList);
        }
        CountBO data = serviceCaller.call(OrderServices.getOrdersCountByUidAndStatus, request, CountBO.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(data).build();
    }

    /**
     * 根据订单号,获取订单
     *
     * @param orderCode
     * @return
     */
    @RequestMapping(params = "method=app.SpaceOrders.getOrdersByCode")
    @ResponseBody
    public ApiResponse getOrdersByCode(@RequestParam("order_code") Long orderCode) {
        OrderRequest request = new OrderRequest();
        request.setOrderCode(orderCode);
        Orders data = serviceCaller.call(OrderServices.getOrdersByCode, request, Orders.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(data).build();
    }

    /**
     * 支付支付确认
     */
    @RequestMapping(params = "method=app.SpaceOrders.payConfirm")
    @ResponseBody
    public ApiResponse payConfirm(@RequestParam(value = "order_code", required = true) String order_code,
                                  @RequestParam(value = "payment_id", required = true) int payment_id,
                                  @RequestParam(value = "uid", required = true) int uid) {
        logger.info("PayConfirm start, order_code is {} payment_id is {} uid is {}.", order_code, payment_id, uid);
        PaymentConfirmReq request = new PaymentConfirmReq();
        request.setOrder_code(order_code);
        request.setPayment_id(payment_id);
        request.setUid(uid);
        PaymentConfirmResponse response = serviceCaller.call(OrderServices.payConfirm, request, PaymentConfirmResponse.class);
        logger.info("PayConfirm success, order_code is {} payment_id is {} uid is {}.", order_code, payment_id, uid);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(response).build();
    }


    /**
     * 获得历史订单
     */
    @RequestMapping(params = "method=app.SpaceOrders.history")
    @ResponseBody
    public ApiResponse getHistoryOrder(@RequestParam("uid") Integer uid,
                                       @RequestParam(value = "page", required = false, defaultValue = "1") int page,
                                       @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {
        OrderListRequest request = new OrderListRequest();
        request.setLimit(limit);
        request.setPage(page);
        request.setUid(uid.toString());
        PageResponse<HistoryOrderVO> historyOrder = orderService.getHistoryOrderList(request);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(historyOrder).build();
    }

    /**
     * 获取用户的订单总数
     */
    @RequestMapping(params = "method=web.SpaceOrders.getOrderCountByUid")
    @ResponseBody
    public ApiResponse getOrderCountByUid(@RequestParam("uid") Integer uid) {
        logger.info("Get order count by uid {}", uid);

        OrdersStatusStatisticsRequest request = new OrdersStatusStatisticsRequest();
        request.setUid(uid);

        CountBO data = serviceCaller.call(OrderServices.getOrderCountByUid, request, CountBO.class);

        logger.info("Get order count by uid result: uid {}, count {}", request.getUid(), data.getCount());
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(data).build();

    }


    /**
     * 通过ID获取支付途径
     */
    @RequestMapping(params = "method=web.SpaceOrders.getPaymentById")
    @ResponseBody
    public ApiResponse getOneById(@RequestParam("id") Integer id) {
        logger.info("Get payment by id, id: {}", id);
        if (id < 1) {
            logger.warn("PaymentId incorrect: {}", id);
            return new ApiResponse.ApiResponseBuilder().code(500).message("id<1").data(null).build();
        }
        PaymentBO data = serviceCaller.call(OrderServices.getPaymentById, id, PaymentBO.class);

        logger.info("End get payment by id, id: {}", id);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(data).build();
    }

    /**
     * 获取所有支付途径
     */
    @RequestMapping(params = "method=web.SpaceOrders.getPaymentList")
    @ResponseBody
    public ApiResponse getPaymentList() {
        logger.info("Begin get payment list");
        List<PaymentBO> data = serviceCaller.call(OrderServices.getPaymentList, null, List.class);
        logger.info("End get payment list");
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(data).build();
    }


    /**
     * ERP提交订单状态
     */
    @RequestMapping(params = "method=web.SpaceOrders.submitOrderStatus")
    @ResponseBody
    public ApiResponse submitOrderStatus(@RequestParam("order_code") String orderCode,
                                         @RequestParam("payment") Byte payment,
                                         @RequestParam("bank_name") String bankName,
                                         @RequestParam("bank_code") String bankCode,
                                         @RequestParam("amount") double amount,
                                         @RequestParam("payOrderCode") String payOrderCode,
                                         @RequestParam("trade_no") String tradeNo,
                                         @RequestParam("bank_bill_no") String bankBillNo) {
        logger.info("[{}] Begin submit order status to ERP MQ", orderCode);

        PayResult payResult = new PayResult();
        payResult.setOrderCode(orderCode);
        payResult.setPaymentID(payment);
        payResult.setBankName(bankName);
        payResult.setBankCode(bankCode);
        payResult.setTotalFeeInYuan(amount);
        payResult.setPayOrderCode(payOrderCode);
        payResult.setTradeNo(tradeNo);
        payResult.setBankBillNo(bankBillNo);

//        // 向 ERP 发送消息
//        if (isCreateERPOrderByMQ()) {
//            // 发送 MQ 消息
//            notifyERPMQ(payResult, logger);
//            logger.info("[{}] ERP MQ sent succeeded", orderCode);
//            return new ApiResponse.ApiResponseBuilder().code(200).message("ok").build();
//        }
//
//        //通知erp
//        try {
//            notifyERP(payResult, logger);
//        } catch (Exception e) {
//            // 如果 ERP 订单状态更新失败, 则不再更新后台订单数据,抛出异常
//            logger.error("[{}] ERP order call failed", orderCode);
//            // throw new ServiceException(ServiceError.ORDER_SUBMIT_CALL_ERP);
//            return new ApiResponse.ApiResponseBuilder().code(500).message("erp call failed").build();
//        }

        //去掉${erp.message.sync.type}开关判断，直接发送MQ
        notifyERPMQ(payResult, logger);
        logger.info("[{}] ERP MQ sent succeeded", orderCode);

        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").build();
    }


    protected void notifyERPMQ(PayResult payResult, Logger logger) throws ServiceException {
        JSONObject statusData = new JSONObject();
        statusData.put("paymentCode", payResult.getPaymentID());
        statusData.put("bankCode", payResult.getBankCode());
        statusData.put("bankName", payResult.getBankName());
        statusData.put("amount", payResult.getTotalFee());
        statusData.put("payOrderCode", payResult.getPayOrderCode());
        statusData.put("tradeNo", payResult.getTradeNo());
        statusData.put("bankBillNo", payResult.getBankBillNo());

        JSONObject data = new JSONObject();
        data.put("orderCode", payResult.getOrderCode());
        data.put("status", 200);
        data.put("statusData", statusData);

        logger.info("[{}] send MQ message is : {}", payResult.getOrderCode(), data);

        try {
            Map<String, Object> map = new HashMap<>();
            map.put("order_code", payResult.getOrderCode());
            map.put("uid", payResult.getUid());

            producerTemplate.send(PAYMENT_FINISH_MQ_TOPTIC, data, map);
            logger.info("[{}] send MQ message success", payResult.getOrderCode());
        } catch (Exception ex) {
            logger.error("[{}] send MQ fail, json:{}", payResult.getOrderCode(), data, ex);
            throw new ServiceException(ServiceError.ORDER_SUBMIT_CALL_ERP);
        }
    }


    protected void notifyERP(PayResult payResult, Logger logger) throws Exception {

        JSONObject statusData = new JSONObject();
        statusData.put("paymentCode", payResult.getPaymentID());
        statusData.put("bankCode", payResult.getBankCode());
        statusData.put("bankName", payResult.getBankName());
        statusData.put("amount", payResult.getTotalFee());
        statusData.put("payment", payResult.getPaymentID());
        statusData.put("payOrderCode", payResult.getPayOrderCode());
        statusData.put("tradeNo", payResult.getTradeNo());
        statusData.put("bankBillNo", payResult.getBankBillNo());
        JSONObject data = new JSONObject();
        data.put("orderCode", payResult.getOrderCode());
        data.put("status", 200);
        data.put("statusData", statusData);
        LinkedMultiValueMap<String, Object> req = new LinkedMultiValueMap<String, Object>();
        req.add("data", data.toJSONString());

        logger.info("[{}] ERP req: {}", payResult.getOrderCode(), req.get("data"));

        String json = null;
        JSONObject jsonObject;

        try {
            json = restTemplate.postForObject(erpOrderStatusUrl, req, String.class);
            jsonObject = JSONObject.parseObject(json);
            logger.info("[{}] ERP resp: {}", payResult.getOrderCode(), json);
        } catch (Exception e) {
            logger.error("[{}] Erp order status call fail:{}, json:{}", payResult.getOrderCode(), e, json);
            throw new ServiceException(ServiceError.ORDER_SUBMIT_CALL_ERP);
        }
        int code = jsonObject.getIntValue("code");
        // logger.debug("ERP return code: [{}]", code);
        if (code != 200) {
            logger.warn("[{}] ERP call return invalid code", payResult.getOrderCode());
            throw new ServiceException(ServiceError.ORDER_SUBMIT_CALL_ERP);
        }
    }

//    /**
//     * 开关
//     * @return
//     */
//    private boolean isCreateERPOrderByMQ() {
//        if ("mq".equalsIgnoreCase(erpMessageSyncType)) {
//            return true;
//        }
//        return false;
//    }

    /**
     * 获取订单的支付银行
     */
    @RequestMapping(params = "method=web.SpaceOrders.getOrderPayBank")
    @ResponseBody
    public ApiResponse getOrderPayBank(@RequestParam("orderCode") Long orderCode) {
        logger.info("[{}] getOrderPayBank", orderCode);
        OrderPayBankBO response = serviceCaller.call(OrderServices.getBankByOrder, orderCode, OrderPayBankBO.class);

        logger.info("[{}] End getOrderPayBank", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(response).build();
    }

    /**
     * 添加订单支付银行记录
     */
    @RequestMapping(params = "method=web.SpaceOrders.addOrderPayBank")
    @ResponseBody
    public ApiResponse addOrderPayBank(@RequestParam("orderCode") Long orderCode, @RequestParam("payment") Byte payment, @RequestParam("bankCode") String bankCode) {
        logger.info("[{}] addOrderPayBank: payment {}, bankCode {}", orderCode, payment, bankCode);
        OrderPayBankBO request = new OrderPayBankBO();
        request.setOrderCode(orderCode);
        request.setPayment(payment);
        request.setBankCode(bankCode);
        serviceCaller.call(OrderServices.addOrderPayBank, request, Void.class);

        logger.info("[{}]End addOrderPayBank", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").build();
    }

    /**
     * 更改订单支付银行记录
     */
    @RequestMapping(params = "method=web.SpaceOrders.modifyOrderPayBank")
    @ResponseBody
    public ApiResponse modifyOrderPayBank(@RequestParam("orderCode") Long orderCode, @RequestParam("payment") Byte payment, @RequestParam("bankCode") String bankCode) {
        logger.info("[{}] modifyOrderPayBank: payment {}, bankCode {}", orderCode, payment, bankCode);
        OrderPayBankBO request = new OrderPayBankBO();
        request.setOrderCode(orderCode);
        request.setPayment(payment);
        request.setBankCode(bankCode);
        serviceCaller.call(OrderServices.modifyOrderPayBank, request, Void.class);

        logger.info("[{}]End modifyOrderPayBank", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").build();
    }

    /**
     * 保存预支付信息
     */
    @RequestMapping(params = "method=app.order.savePrePayInfo")
    @ResponseBody
    public ApiResponse savePrePayInfo(@RequestParam(value = "orderCode", required = true) Long orderCode,
                                      @RequestParam(value = "payment", required = true) int payment,
                                      @RequestParam(value = "uid", required = true) int uid) {
        logger.info("SavePrePayInfo for order {}: payment {}, uid {}", orderCode, payment, uid);
        PrePaymentRequest request = new PrePaymentRequest();
        request.setOrderCode(orderCode);
        request.setPayment(payment);
        request.setUid(uid);
        serviceCaller.call(OrderServices.savePrePayInfo, request, Void.class);
        logger.info("SavePrePayInfo for order {} success.", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").build();
    }


    /**
     * 订单支付方式主动查询接口（内部接口）
     *
     * @param orderCode
     * @return
     */
    @RequestMapping(params = "method=web.SpaceOrders.paymentQuery")
    @ResponseBody
    public ApiResponse paymentOrderQuery(@RequestParam("orderCode") String orderCode) {
        logger.info("payment orderquery for order: {}", orderCode);
        PaymentOrderQueryBO bo = serviceCaller.call(OrderServices.paymentOrderQuery, orderCode, PaymentOrderQueryBO.class);
        logger.info("payment orderquery end, orderCode: {}, resultCode: {}", orderCode, bo.getResultCode());
        return new ApiResponse.ApiResponseBuilder().code(200).data(bo).build();
    }

    /**
     * 订单退款（内部接口，不提供外部使用）
     *
     * @param orderCode
     * @return
     */
    //@RequestMapping(params = "method=web.SpaceOrders.paymentRefund")
    @RequestMapping("/erp/order/paymentRefund")
    @ResponseBody
    public ApiResponse paymentOrderRefund(@RequestParam(value = "orderCode", required = true) long orderCode,
                                          @RequestParam(value = "payment", required = true) int payment,
                                          @RequestParam(value = "amount", required = true) String amount) {
        //amount=0.0100，末尾00可能被springmvc忽略，此时计算的client_secret是错误的，将amount类型改为String
        logger.info("payment refund for order: {}, payment: {}, amount: {}", orderCode, payment, amount);
        OrdersPayRefundRequest request = new OrdersPayRefundRequest();
        request.setOrderCode(orderCode);
        request.setPayment(payment);
        request.setAmount(Double.valueOf(amount));
        PaymentOrderQueryBO bo = serviceCaller.call("order.paymentRefund", request, PaymentOrderQueryBO.class);
        logger.info("payment refund end, orderCode: {}, resultCode: {}", orderCode, bo.getResultCode());
        return new ApiResponse.ApiResponseBuilder().code(200).data(bo).build();
    }

    /**
     * 订单退款（内部接口，不提供外部使用）
     *
     * @param orderCode
     * @return
     */
    @RequestMapping(params = "method=web.SpaceOrders.paymentRefundQuery")
    @ResponseBody
    public ApiResponse paymentRefundQuery(@RequestParam(value = "orderCode", required = true) long orderCode,
                                          @RequestParam(value = "payment", required = true) int payment) {
        logger.info("payment refund query for order: {}, payment: {}", orderCode, payment);
        OrdersPayRefundRequest request = new OrdersPayRefundRequest();
        request.setOrderCode(orderCode);
        request.setPayment(payment);
        PaymentOrderQueryBO bo = serviceCaller.call("order.paymentRefundQuery", request, PaymentOrderQueryBO.class);
        logger.info("payment refund query end, orderCode: {}, resultCode: {}", orderCode, bo.getResultCode());
        return new ApiResponse.ApiResponseBuilder().code(200).data(bo).build();
    }

    /**
     * 获取订单花呗分期详情
     *
     * @param orderCode
     */
    @RequestMapping(params = "method=app.SpaceOrders.getAntHbfqDetail")
    @ResponseBody
    public ApiResponse getAntHbfqDetail(@RequestParam("orderCode") Long orderCode) {
        logger.info("[{}] getAntHbfqDetail", orderCode);
        List<AntHbfqBO> hbfqBOList = serviceCaller.call(OrderServices.getAntHbfqDetail, orderCode, List.class);
        logger.info("[{}] End getAntHbfqDetail", orderCode);
        return new ApiResponse.ApiResponseBuilder().code(200).message("ok").data(hbfqBOList).build();
    }
}
