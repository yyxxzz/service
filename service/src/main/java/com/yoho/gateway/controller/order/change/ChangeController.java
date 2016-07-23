package com.yoho.gateway.controller.order.change;

import com.alibaba.fastjson.JSON;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.order.cache.UserOrderCache;
import com.yoho.gateway.utils.BeanTool;
import com.yoho.gateway.utils.PhoneUtil;
import com.yoho.service.model.order.model.ChangeGoodsBO;
import com.yoho.service.model.order.model.ChangeGoodsListBO;
import com.yoho.service.model.order.request.*;
import com.yoho.service.model.order.response.orderChange.OrderChangeDeliveryRsp;
import com.yoho.service.model.order.response.orderChange.OrderChangeGoodsApplyRsp;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * lijian 订单换货
 * 2015/1/13
 */
@Controller
public class ChangeController {
    @Autowired
    private ServiceCaller serviceCaller;

    private static final String URL_ORDER_SUBMIT = "order.submitChangeGoods";

    private static final String URL_ORDER_CHANGE_GET_DELIVERY = "order.queryDeliveryList";

    private static final String URL_ORDER_CHANGE_GET_CHANGE_LIST = "order.queryChangeGoodsList";

    private static final String URL_ORDER_CHANGE_SETEXPRESS = "order.setChangeExpress";

    private static final String URL_ORDER_CHANGE_DETAIL = "order.queryChangeGoodsDetail";

    private Logger logger = LoggerFactory.getLogger(ChangeController.class);
    
    @Autowired
    private UserOrderCache userOrderCache;
    /**
     * 提交换货申请
     */
    @RequestMapping(params = "method=app.change.submit")
    @ResponseBody
    public ApiResponse submit(@RequestParam("order_code") Long orderCode, @RequestParam("uid") int uid,
                              @RequestParam(value = "area_code", required = false, defaultValue = "") Integer areaCode,
                              @RequestParam("goods") String goodsList,
                              @RequestParam("consignee_name") String consignee_name,
                              @RequestParam("address") String address,
                              @RequestParam("mobile") String mobile,
                              @RequestParam(value = "zip_code", required = false, defaultValue = "0") String zip_code,
                              @RequestParam(value = "delivery_tpye", required = false, defaultValue = "0") byte delivery_tpye,
                              @RequestParam(value = "receipt_time", required = false, defaultValue = "2") String receipt_time,
                              @RequestParam(value = "address_id", required = false) String address_id
    ) {
        logger.info("ChangeController--change submit orderChangeGoodsApplyReq req  goodsList is{}", goodsList);

        OrderChangeGoodsApplyReq orderChangeGoodsApplyReq = new OrderChangeGoodsApplyReq();
        orderChangeGoodsApplyReq.setOrder_code(Long.valueOf(orderCode));
        orderChangeGoodsApplyReq.setUid(uid);
        orderChangeGoodsApplyReq.setConsignee_name(consignee_name);
        orderChangeGoodsApplyReq.setArea_code(String.valueOf(areaCode));
        orderChangeGoodsApplyReq.setAddress(address);
        orderChangeGoodsApplyReq.setMobile(mobile);
        if(address_id==null||"".equals(address_id)){
            orderChangeGoodsApplyReq.setAddress_id(null);
        }else{
            orderChangeGoodsApplyReq.setAddress_id(Integer.parseInt(address_id));
        }
        if (zip_code != null && !"null".equals(zip_code)) {
            orderChangeGoodsApplyReq.setZip_code((zip_code));
        } else {
            orderChangeGoodsApplyReq.setZip_code(("0"));
        }
        orderChangeGoodsApplyReq.setDelivery_tpye(delivery_tpye);
        orderChangeGoodsApplyReq.setReceipt_time(receipt_time);
        List<OrderChangeGoodsDetailApplyGateReq> goodList = JSON.parseArray(goodsList, OrderChangeGoodsDetailApplyGateReq.class);
        List<OrderChangeGoodsDetailApplyReq> convertGoodList = new ArrayList<OrderChangeGoodsDetailApplyReq>();
        if (goodList != null && goodList.size() > 0) {
            for (OrderChangeGoodsDetailApplyGateReq orderChangeGoodsDetailApplyGateReq : goodList) {
                OrderChangeGoodsDetailApplyReq goodsDetailApplyReq = new OrderChangeGoodsDetailApplyReq();
                BeanTool.copyPropertysWithoutNull(goodsDetailApplyReq, orderChangeGoodsDetailApplyGateReq, "evidence_images");
                Object imgs = orderChangeGoodsDetailApplyGateReq.getEvidence_images();
                if (StringUtils.isNotBlank(String.valueOf(imgs))) {
                    goodsDetailApplyReq.setEvidence_images((List<String>) imgs);
                }
                convertGoodList.add(goodsDetailApplyReq);
            }
        }
        orderChangeGoodsApplyReq.setGoods(convertGoodList);
        logger.info("ChangeController--change submit orderChangeGoodsApplyReq req is {}", JSON.toJSONString(orderChangeGoodsApplyReq));
        OrderChangeGoodsApplyRsp applyId = serviceCaller.call(URL_ORDER_SUBMIT, orderChangeGoodsApplyReq, OrderChangeGoodsApplyRsp.class);
        logger.info("OrderChangeGoodsApplyRsp return  is {}", JSON.toJSONString(applyId));
        
        //换货后，清除订单各类总数信息缓存
        userOrderCache.clearRefundOrderCountCache(uid);
        
        return new ApiResponse.ApiResponseBuilder().code(200).message("换货申请成功").data(JSON.toJSON(applyId)).build();
    }


    /**
     * 换货详情 http://api.open.yohobuy.com/?method=app.change.detail&v=4
     * id 换货申请id
     */
    @RequestMapping(params = "method=app.change.detail")
    @ResponseBody
    public ApiResponse detail(@RequestParam("id") int id, @RequestParam("uid") int uid) {
        OrderChangeGoodsReq orderChangeGoodsReq = new OrderChangeGoodsReq();
        orderChangeGoodsReq.setId(id);
        orderChangeGoodsReq.setUid(uid);
        ChangeGoodsBO changeGoodsBO = null;
        logger.info("orderChangeGoodsReq req is {}", orderChangeGoodsReq);
        changeGoodsBO = serviceCaller.call(URL_ORDER_CHANGE_DETAIL, orderChangeGoodsReq, ChangeGoodsBO.class);
        if(changeGoodsBO.getMobile()!=null&&!"".equals(changeGoodsBO.getMobile())){
            changeGoodsBO.setMobile(PhoneUtil.coverMobile(changeGoodsBO.getMobile()));
        }
        //增加Id,uid主要是为了前端     取消换货申请时   用
        changeGoodsBO.setId(id);
        changeGoodsBO.setUid(uid);
        logger.info("orderChangeGoodsReq return  is {}", JSON.toJSONString(changeGoodsBO));
        return new ApiResponse.ApiResponseBuilder().code(200).message("换货详情").data(changeGoodsBO).build();

    }


    /**
     * 保存快递信息 http://api.open.yohobuy.com/?method=app.change.setexpress
     * &id=1&express_company=%E5%BF%AB%E9%80%92%E5%85%AC%E5%8F%B8%E5%90%8D&express_id=1&express_number=1234&uid=123
     */
    @RequestMapping(params = "method=app.change.setexpress")
    @ResponseBody
    public ApiResponse setExpress(@RequestParam("id") int id,
                                  @RequestParam("uid") int uid,
                                  @RequestParam("express_company") String expressCompany,
                                  @RequestParam("express_number") String expressNumber,
                                  @RequestParam("express_id") Byte expressId) {
        OrderChangeExpressReq orderChangeExpressReq = new OrderChangeExpressReq();
        orderChangeExpressReq.setId(id);
        orderChangeExpressReq.setUid(uid);
        orderChangeExpressReq.setExpressCompany(expressCompany);
        orderChangeExpressReq.setExpressNumber(expressNumber);
        orderChangeExpressReq.setExpressId(expressId);
        logger.info("setExpress orderChangeGoodsReq req is {}", JSON.toJSONString(orderChangeExpressReq));

        serviceCaller.call(URL_ORDER_CHANGE_SETEXPRESS, orderChangeExpressReq, String.class);

        return new ApiResponse.ApiResponseBuilder().code(200).message("快递单号设置成功").build();

    }

    /**
     * 换货商品返回方式 http://api.open.yohobuy.com/?method=app.change.getDelivery&uid=123&area_code=1234
     */
    @RequestMapping(params = "method=app.change.getDelivery")
    @ResponseBody
    public ApiResponse getDelivery(@RequestParam("area_code") String area_code, @RequestParam("uid") int uid) {
        OrderChangeDeliveryReq orderChangeDeliveryReq = new OrderChangeDeliveryReq();
        orderChangeDeliveryReq.setAreaCode(area_code);
        orderChangeDeliveryReq.setUid(uid);
        logger.info("getDelivery orderChangeDeliveryReq req is {}", orderChangeDeliveryReq);
        OrderChangeDeliveryRsp[] orderChangeDeliveryRsps = serviceCaller.call(URL_ORDER_CHANGE_GET_DELIVERY, orderChangeDeliveryReq, OrderChangeDeliveryRsp[].class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("change type").data(orderChangeDeliveryRsps).build();
    }

    /**
     * 获取换货订单商品列表
     */
    @RequestMapping(params = "method=app.change.goodsList")
    @ResponseBody
    public ApiResponse goodsList(@RequestParam("uid") int uid, @RequestParam("order_code") long orderCode) {
        OrderChangeGoodsReq changeGoodsReq = new OrderChangeGoodsReq();
        changeGoodsReq.setUid(uid);
        changeGoodsReq.setOrderCode(orderCode);
        logger.info("app.change.goodsList  change_good_list req is {}", JSON.toJSONString(changeGoodsReq));
        ChangeGoodsListBO changeGoodsListBO = serviceCaller.call(URL_ORDER_CHANGE_GET_CHANGE_LIST, changeGoodsReq, ChangeGoodsListBO.class);
        if(changeGoodsListBO.getAddress()!=null&&changeGoodsListBO.getAddress().getMobile()!=null){
            String mobile=changeGoodsListBO.getAddress().getMobile();
            changeGoodsListBO.getAddress().setMobile(PhoneUtil.coverMobile(mobile)); 
        }
        logger.info("app.change.goodsList  change_good_list return  is {}", JSON.toJSONString(changeGoodsListBO));
        return new ApiResponse.ApiResponseBuilder().code(200).message("change goods list").data(changeGoodsListBO).build();
    }
    /**
     * 取消换货
     *
     * @param id  换货表id
     * @param uid
     */
    @RequestMapping(params = "method=app.change.cancel")
    @ResponseBody
    public ApiResponse changeCancel(@RequestParam("id") int id, @RequestParam("uid") int uid) {
        OrderChangeGoodsReq orderChangeGoodsReq = new OrderChangeGoodsReq();
        orderChangeGoodsReq.setId(id);
        orderChangeGoodsReq.setUid(uid);
        logger.info("changeCancel req is changeId:{},uid:{}", id,uid);
        serviceCaller.call("order.changeCancel", orderChangeGoodsReq, ChangeGoodsBO.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("取消换货成功").build();

    }

}