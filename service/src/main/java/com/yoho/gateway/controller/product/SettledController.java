package com.yoho.gateway.controller.product;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.gateway.model.request.SellerApplyLogReqVO;
import com.yoho.product.request.SellerApplyLogReqBo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by zhouxiang on 2016/4/7.
 */
@Controller
public class SettledController {

    private Logger logger = LoggerFactory.getLogger(SettledController.class);

    @Autowired
    protected ServiceCaller serviceCaller;

    /**
     * 添加商家入驻申请记录
     * @param sellerApplyLogReqVO
     * @return
     */
    @RequestMapping(params = "method=app.shops.insertApply")
    @ResponseBody
    public ApiResponse insertApply(SellerApplyLogReqVO sellerApplyLogReqVO) throws GatewayException {
        logger.debug("Enter SettledController.insertApply. sellerApplyLogReqVO is {}", sellerApplyLogReqVO);
        SellerApplyLogReqBo sellerApplyLogReqBo = new SellerApplyLogReqBo();
        BeanUtils.copyProperties(sellerApplyLogReqVO, sellerApplyLogReqBo);
        int flag = serviceCaller.call("product.insertApply", sellerApplyLogReqBo,Integer.class);
        int code = 200;
        String message = "添加商家入驻申请记录成功。";
        if(flag != 1){
            code = 500;
            message = "添加商家入驻申请记录失败。";
        }

        //组织返回
        return new ApiResponse.ApiResponseBuilder().code(code).message(message).data(null).build();
    }

}
