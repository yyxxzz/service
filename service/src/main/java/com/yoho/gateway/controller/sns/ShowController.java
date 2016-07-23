package com.yoho.gateway.controller.sns;

import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.service.sns.IShowService;
import com.yoho.service.model.sns.response.CurrencyParamRspBO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

/**
 * Created by zhouxiang on 2016/3/26.
 */
@Controller
public class ShowController {
    private static Logger logger = LoggerFactory.getLogger(ShowController.class);

    @Autowired
    private IShowService showService;

    /**
     * 获取用户返币晒物总数
     * @param sso_uid
     * @return
     */
    @RequestMapping(params = "method=cn.show.returnCurrency")
    @ResponseBody
    public ApiResponse returnCurrency(@RequestParam(defaultValue="") String sso_uid) {
        logger.info("enter returnCurrency. param  sso_uid is {}", sso_uid);
        //SSO UID为空状态 返回错误提示信息"SSO UID 不能为空."
        if (StringUtils.isEmpty(sso_uid)) {
            logger.warn("returnCurrency request param sso_uid is null");
            return new ApiResponse.ApiResponseBuilder().code(500).message("SSO UID 不能为空.").data(null).build();
        }

        //SSO UID不为空 按‘,’分割sso_uid
        String[] ssoUids = sso_uid.split(",");
        //sso_uid数量大于50，返回对应错误提示信息 "最多50个SSOUID查询"
        if (ssoUids.length > 50) {
            logger.warn("returnCurrency request param sso_uid number > 50");
            return new ApiResponse.ApiResponseBuilder().code(500).message("最多50个SSOUID查询.").data(null).build();
        }

        Map<String,CurrencyParamRspBO> data = showService.returnCurrency(ssoUids);
        //message ： 用户返币晒物总数
        return new ApiResponse.ApiResponseBuilder().code(200).message("用户返币晒物总数.").data(data).build();
    }
}
