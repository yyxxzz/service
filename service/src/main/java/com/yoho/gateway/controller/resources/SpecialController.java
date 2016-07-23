package com.yoho.gateway.controller.resources;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.resource.SpecialBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by yoho on 2016/3/17.
 */
@Controller
public class SpecialController {

    @Autowired
    private ServiceCaller serviceCaller;

    @RequestMapping(params = "method=app.resources.getOneSpecial")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.SPECIAL)
    public ApiResponse getOneSpecial(@RequestParam("special_id") Integer specialId) {
        SpecialBO request = new SpecialBO();
        request.setId(specialId);
        SpecialBO data = serviceCaller.call("resources.findSpecialById", request, SpecialBO.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("专区信息").data(data).build();
    }
}