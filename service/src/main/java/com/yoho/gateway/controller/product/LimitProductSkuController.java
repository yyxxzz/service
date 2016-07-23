package com.yoho.gateway.controller.product;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.controller.product.convert.ProductConvert;
import com.yoho.gateway.model.product.LimitProductSkuVo;
import com.yoho.gateway.model.product.ProductVo;
import com.yoho.product.model.ProductBo;
import com.yoho.product.request.LimitProductQueryRequest;
import com.yoho.product.request.LimitProductSkuReqBo;
import com.yoho.service.model.promotion.request.ActivityStatusReqBO;
import com.yoho.service.model.promotion.request.LimitCodeReq;
import org.apache.commons.lang3.StringUtils;
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
 * Created by zhouxiang on 2016/4/22.
 */
@Controller
public class LimitProductSkuController {

    private Logger logger = LoggerFactory.getLogger(LimitProductSkuController.class);

    @Autowired
    protected ServiceCaller serviceCaller;

    @Autowired
    private ProductConvert productConvert;

    /**
     * 用户选择/修改sku信息
     *
     * @param limitProductSkuVo
     * @return
     */
    @RequestMapping(params = "method=app.limitProduct.userChoiceSku")
    @ResponseBody
    public ApiResponse userChoiceSku(LimitProductSkuVo limitProductSkuVo) {
        logger.info("Enter userChoiceSku. param activity_id is {},uid is {},product_skn is {},product_sku is {},product_skc is {},color_name is {},size_name is {}",limitProductSkuVo.getActivity_id(),
                limitProductSkuVo.getUid(),limitProductSkuVo.getProduct_skn(),limitProductSkuVo.getProduct_sku(),limitProductSkuVo.getProduct_skc(),limitProductSkuVo.getColor_name(),limitProductSkuVo.getSize_name());
        //判断活动id是否为空
        if(StringUtils.isBlank(limitProductSkuVo.getActivity_id())){
            return new ApiResponse.ApiResponseBuilder().code(500).message("活动id为空").data(null).build();
        }
        //组装请求参数
        ActivityStatusReqBO activityStatusBO = new ActivityStatusReqBO();
        activityStatusBO.setActivityId(limitProductSkuVo.getActivity_id());
        activityStatusBO.setUid("0");
        int activityStatus = serviceCaller.call("promotion.getActivityStatus", activityStatusBO, Integer.class);
        //活动已结束
        if(3 == activityStatus){
            return new ApiResponse.ApiResponseBuilder().code(500).message("活动已结束，不可以修改颜色尺码").data(null).build();
        }

        int code = 200;
        String messsage = "选择颜色尺码成功";
        //验证必填项
        if (limitProductSkuVo == null || StringUtils.isBlank(limitProductSkuVo.getUid()) || StringUtils.isBlank(limitProductSkuVo.getProduct_skn())) {
            return new ApiResponse.ApiResponseBuilder().code(500).message("uid或productSkn不能为空").data(null).build();
        }
        //vo转bo
        LimitProductSkuReqBo reqBo = getLimitProductBoByVo(limitProductSkuVo);
        logger.info("Enter userChoiceSku. param uid is {},product_skn is {}", limitProductSkuVo.getUid(),limitProductSkuVo.getProduct_skn());
        //插入选择的sku信息
        int flag = serviceCaller.call("product.userChoiceSkuRest", reqBo, Integer.class);
        logger.info("end userChoiceSku. param flag is {}", flag);

        if (flag != 1) {
            code = 500;
            messsage = "选择颜色尺码失败";
        }

        return new ApiResponse.ApiResponseBuilder().code(code).message(messsage).data(null).build();
    }

    /**
     * 根据skn查询选择的sku信息 颜色、尺码、商品名称、价格
     *
     * @return
     */
    @RequestMapping(params = "method=app.limitProduct.getLimitProductDataBySkn")
    @ResponseBody
    public ApiResponse getLimitProductDataBySkn(@RequestParam(value = "product_skn", required = false) String productSkn) {
        logger.info("Enter getLimitProductDataBySkus. param productSkn is {}", productSkn);
        //校验参数
        if (StringUtils.isBlank(productSkn)) {
            return new ApiResponse.ApiResponseBuilder().code(500).message("product_skn不能为空").data(null).build();
        }
        LimitCodeReq limitCodeReq = new LimitCodeReq();
        limitCodeReq.setSkn(productSkn);
        List<Integer> skuIds = serviceCaller.call("promotion.getLimitCodeSkuOfSkn", limitCodeReq, ArrayList.class);
        if (null == skuIds || skuIds.size() == 0) {
            return new ApiResponse.ApiResponseBuilder().code(500).message("找不到商品对应的颜色尺码信息").data(null).build();
        }

        LimitProductQueryRequest request = new LimitProductQueryRequest();
        request.setProductSkn(Integer.valueOf(productSkn));
        request.setSkuIds(skuIds);
        logger.info("queryProductLimited  productskn is:{}", productSkn);
        ProductBo productBo = serviceCaller.call("product.queryProductLimited", request, ProductBo.class);
        //当商品信息或价格信息或颜色信息或尺码信息不完整时  抛出异常
        if (null == productBo || null == productBo.getProductPriceBo() || null == productBo.getGoodsList()) {
            logger.warn("queryProductLimited productBo is null productskn is:{}", productSkn);
            return new ApiResponse.ApiResponseBuilder().data(productBo).code(500).message("找不到商品对应的颜色尺码信息").build();
        }

        // 转换成VO
        ProductVo productVo = productConvert.convert(productBo);
        return new ApiResponse.ApiResponseBuilder().data(productVo).code(200).message("成功").build();
    }

    /**
     * 获取bo对象
     *
     * @param limitProductSkuVo
     * @return
     */
    private LimitProductSkuReqBo getLimitProductBoByVo(LimitProductSkuVo limitProductSkuVo) {
        LimitProductSkuReqBo reqBo = new LimitProductSkuReqBo();
        if (StringUtils.isNotBlank(limitProductSkuVo.getUid()))
            reqBo.setUid(Integer.valueOf(limitProductSkuVo.getUid()));
        if (StringUtils.isNotBlank(limitProductSkuVo.getProduct_skn()))
            reqBo.setProductSkn(Integer.valueOf(limitProductSkuVo.getProduct_skn()));
        if (StringUtils.isNotBlank(limitProductSkuVo.getProduct_skc()))
            reqBo.setProductSkc(Integer.valueOf(limitProductSkuVo.getProduct_skc()));
        if (StringUtils.isNotBlank(limitProductSkuVo.getProduct_sku()))
            reqBo.setProductSku(Integer.valueOf(limitProductSkuVo.getProduct_sku()));
        if (StringUtils.isNotBlank(limitProductSkuVo.getColor_name()))
            reqBo.setColorName(limitProductSkuVo.getColor_name());
        if (StringUtils.isNotBlank(limitProductSkuVo.getSize_name()))
            reqBo.setSizeName(limitProductSkuVo.getSize_name());
        return reqBo;
    }
}
