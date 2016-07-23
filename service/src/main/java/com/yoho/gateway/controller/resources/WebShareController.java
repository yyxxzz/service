package com.yoho.gateway.controller.resources;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.resource.ResourcesServices;
import com.yoho.service.model.resource.WebShareBO;
import com.yoho.service.model.resource.request.FindWebShareRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by LUOXC on 2016/2/20.
 */
@RestController
public class WebShareController {

    @Autowired
    private ServiceCaller serviceCaller;


    /**
     * 获取分享信息的接口
     *
     * @return
     */
    @RequestMapping("/operations/api/{v}/webshare/getShare")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.WEB_SHARE)
    public ApiResponse getShare(@RequestParam(value = "share_id") Integer shareId) {
        FindWebShareRequest request = new FindWebShareRequest();
        request.setId(shareId);
        WebShareBO data = serviceCaller.call(ResourcesServices.findWebShare, request, WebShareBO.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("获取分享详情").data(data).build();
    }

}
