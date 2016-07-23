package com.yoho.gateway.controller.order.express;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yoho.service.model.order.request.NewExpressRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.order.ExpressCompanyVO;
import com.yoho.gateway.model.order.WaybillInfoVO;
import com.yoho.gateway.service.order.ExpressService;
import com.yoho.service.model.order.model.ExpressCompanyBO;
import com.yoho.service.model.order.model.WaybillInfoBO;
import com.yoho.service.model.order.request.ExpressCompanyRequest;
import com.yoho.service.model.order.request.WaybillInfoRequest;

/**
 * qianjun
 * 2015/12/21.
 */
@Controller
public class ExpressController {
    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
    private ExpressService expressService;

    /**
     * 获取物流公司列表
     * @param status 物流公司启用状态 1开启，0关闭 默认开启
     * @return
     */
    @RequestMapping(params = "method=app.express.getExpressCompany")
    @ResponseBody
    public ApiResponse getExpressCompany(@RequestParam(value = "status", required = false, defaultValue = "1") int status) {
        ExpressCompanyRequest expressCompanyRequest = new ExpressCompanyRequest();
        expressCompanyRequest.setStatus(new Integer(status).byteValue());
        ExpressCompanyBO[] expressCompanyBOArray = serviceCaller.call("order.getExpressCompany", expressCompanyRequest, ExpressCompanyBO[].class);
        Map<String, List<ExpressCompanyVO>> expressCompanyVOListMap = expressService.getExpressCompanyVO(Arrays.asList(expressCompanyBOArray));
        return new ApiResponse.ApiResponseBuilder().code(200).message("物流公司").data(expressCompanyVOListMap).build();
    }

    /**
     * 获取最新退换货物流信息
     *
     * @param id   退换货申请ID
     * @param uid  用户ID
     * @param type refund:退货,change:换货
     * @return
     */
    @RequestMapping(params = "method=app.express.getNewExpress")
    @ResponseBody
    public ApiResponse getNewExpress(@RequestParam("id") int id, @RequestParam("uid") int uid, @RequestParam("type") String type) {
        NewExpressRequest newExpressRequest = new NewExpressRequest();
        newExpressRequest.setId(id);
        newExpressRequest.setUid(uid);
        newExpressRequest.setType(type);
        Map<String, Object> expressData = serviceCaller.call("order.getNewExpress", newExpressRequest, Map.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("成功获取物流信息").data(expressData).build();
    }

    /**
     * 获取退换货物流信息
     *
     * @param expressId  快递公司ID
     * @param expressNumber  快递单号
     * @return
     */
    @RequestMapping(params = "method=app.express.getRefundExpress")
    @ResponseBody
    public ApiResponse getRefundExpress(@RequestParam("express_id") int expressId, @RequestParam("express_number") String expressNumber) {
        WaybillInfoRequest waybillInfoRequest = new WaybillInfoRequest();
        waybillInfoRequest.setLogisticsType(new Integer(expressId).byteValue());
        waybillInfoRequest.setWaybillCode(expressNumber);
        WaybillInfoBO[] waybillInfoBOArray = serviceCaller.call("order.getRefundExpress", waybillInfoRequest, WaybillInfoBO[].class);
        List<WaybillInfoVO> waybillInfoVOList = expressService.getRefundExpressVO(Arrays.asList(waybillInfoBOArray));
        return new ApiResponse.ApiResponseBuilder().code(200).message("物流信息").data(waybillInfoVOList).build();
    }
}
