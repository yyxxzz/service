package com.yoho.gateway.service.order;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.NameFilter;
import com.yoho.gateway.model.order.OrdersGoodsVO;
import com.yoho.gateway.model.order.RefundGoodsVO;
import com.yoho.gateway.utils.CalendarUtils;
import com.yoho.service.model.order.model.RefundGoodsBO;
import com.yoho.service.model.order.model.refund.Goods;
import com.yoho.service.model.order.model.refund.Payment;
import com.yoho.service.model.order.model.refund.UnderscoreGoods;
import com.yoho.service.model.order.model.refund.UnderscorePayment;
import com.yoho.service.model.order.response.OrdersGoods;
import com.yoho.service.model.order.response.PageResponse;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * qianjun
 * 2016/1/6.
 */
@Service
public class RefundService {

    private static final Logger logger = LoggerFactory.getLogger(RefundService.class);

    /**
     * 将Bo 转换为 Vo
     *
     * @param pageResponse
     */
    public PageResponse<RefundGoodsVO> convertRefundGoodsBO2VO(PageResponse pageResponse) {
        List<RefundGoodsVO> refundGoodsVOs = new ArrayList<>();
        List<RefundGoodsBO> refundGoodsBOList = JSON.parseArray(pageResponse.getList().toString(), RefundGoodsBO.class);
        if (CollectionUtils.isEmpty(refundGoodsBOList)) {
            return pageResponse;
        }

        for (RefundGoodsBO refundGoodsBO : refundGoodsBOList) {
            RefundGoodsVO refundGoodsVO = new RefundGoodsVO();
            BeanUtils.copyProperties(refundGoodsBO, refundGoodsVO);

            refundGoodsVO.setStatus(getStringValue(refundGoodsBO.getStatus()));
            refundGoodsVO.setOrderCode(getStringValue(refundGoodsBO.getOrderCode()));
            refundGoodsVO.setCreateTime(parseformatSeconds(refundGoodsBO.getCreateTime(), CalendarUtils.SHORT_FORMAT_LINE));
            refundGoodsVO.setOrderCreateTime(parseformatSeconds(refundGoodsBO.getOrderCreateTime(), CalendarUtils.LONG_FORMAT_LINE));
            refundGoodsVO.setId(getStringValue(refundGoodsBO.getId()));
            refundGoodsVO.setCanCancel(refundGoodsBO.getCanCancel());

            List<OrdersGoodsVO> orderGoodsVos = new ArrayList<>();

            if (CollectionUtils.isEmpty(refundGoodsBO.getGoods())) {
                refundGoodsVO.setGoods(orderGoodsVos);
                refundGoodsVOs.add(refundGoodsVO);
            }

            List<OrdersGoods> goodsList = refundGoodsBO.getGoods();
            for (OrdersGoods goodsBO : goodsList) {
                OrdersGoodsVO ordersGoodsVO = new OrdersGoodsVO();
                BeanUtils.copyProperties(goodsBO, ordersGoodsVO);

                ordersGoodsVO.setProductSku(getStringValue(goodsBO.getProductSku()));
                ordersGoodsVO.setProductSkn(getStringValue(goodsBO.getProductSkn()));
                ordersGoodsVO.setProductName(getStringValue(goodsBO.getProductName()));
                ordersGoodsVO.setGoodsImage(getStringValue(goodsBO.getGoodsImage()));
                ordersGoodsVO.setSizeName(getStringValue(goodsBO.getSizeName()));
                ordersGoodsVO.setColorName(getStringValue(goodsBO.getColorName()));
                ordersGoodsVO.setSalesPrice(getStringValue(goodsBO.getSalesPrice()));
                ordersGoodsVO.setGoodsType(getStringValue(goodsBO.getGoodsTypeDESC()));
                ordersGoodsVO.setGoodsId(getStringValue(goodsBO.getGoodsId()));
                ordersGoodsVO.setProductId(getStringValue(goodsBO.getProductId()));
                orderGoodsVos.add(ordersGoodsVO);
            }
            refundGoodsVO.setGoods(orderGoodsVos);
            refundGoodsVOs.add(refundGoodsVO);

        }

        pageResponse.setList(refundGoodsVOs);
        return pageResponse;
    }

    private String getStringValue(Object value) {
        return Objects.isNull(value) ? "" : value + "";
    }

    /**
     * 将日期时间戳格式化日期
     *
     * @param value
     * @param format
     * @return
     */
    private String parseformatSeconds(Integer value, String format) {
        if (Objects.isNull(value)) {
            return "";
        }

        return CalendarUtils.parseformatSeconds(value, format);
    }

    /**
     * 退货商品下划线格式属性名转换为驼峰格式属性名
     *
     * @return
     */
    public List<Goods> underscoreGoodsToGoods(List<UnderscoreGoods> underscoreGoodses) {
        if (CollectionUtils.isEmpty(underscoreGoodses)) {
            return Collections.emptyList();
        }
        List<Goods> goodses = new ArrayList<>();
        for (UnderscoreGoods underscoreGoods : underscoreGoodses) {
            Goods goods = new Goods();
            goods.setProductSkn(underscoreGoods.getProduct_skn());
            goods.setProductSkc(underscoreGoods.getProduct_skc());
            goods.setProductSku(underscoreGoods.getProduct_sku());
            goods.setGoodsType(underscoreGoods.getGoods_type());
            goods.setLastPrice(underscoreGoods.getLast_price());
            goods.setReturnedReason(underscoreGoods.getReturned_reason());
            goods.setRemark(underscoreGoods.getRemark());
            if (CollectionUtils.isEmpty(underscoreGoods.getEvidence_images())) {
                goods.setEvidenceImages(null);
            } else {
                Iterator<String> it = underscoreGoods.getEvidence_images().iterator();
                while (it.hasNext()) {
                    String evidenceImage = it.next();
                    if (StringUtils.isNotBlank(evidenceImage)) {
                        if ("null".equalsIgnoreCase(evidenceImage)) {
                            logger.warn("evidenceImage exists null character string");
                            it.remove();
                        }
                    } else {
                        logger.warn("evidenceImage exists empty character string");
                        it.remove();
                    }
                }
                goods.setEvidenceImages(underscoreGoods.getEvidence_images());
            }
            goodses.add(goods);
        }
        return goodses;
    }

    /**
     * 退款 下划线格式属性名转换为驼峰格式属性名
     *
     * @return
     */
    public Payment underscorePaymentToPayment(UnderscorePayment underscorePayment) {
        Payment payment = new Payment();
        payment.setProvince(underscorePayment.getProvince());
        payment.setCity(underscorePayment.getCity());
        payment.setCounty(underscorePayment.getCounty());
        payment.setAreaCode(underscorePayment.getArea_code());
        payment.setReturnAmountMode(underscorePayment.getReturn_amount_mode());
        payment.setPayeeName(underscorePayment.getPayee_name());
        payment.setRemark(underscorePayment.getRemark());
        payment.setBankName(underscorePayment.getBank_name());
        payment.setBankCard(underscorePayment.getBank_card());
        payment.setAlipayName(underscorePayment.getAlipay_name());
        payment.setAlipayAccount(underscorePayment.getAlipay_account());
        return payment;
    }
}
