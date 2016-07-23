package com.yoho.gateway.controller.resources;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.resource.ClientBO;
import com.yoho.service.model.resource.ClientInitConfigBO;
import com.yoho.service.model.resource.ResourcesServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ConfigController {

    @Autowired
    private ServiceCaller serviceCaller;


    /**
     * 软件初始化配置接口
     *
     * @return
     */
    @RequestMapping(params = "method=app.resources.config.clientInitConfig")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.CONFIG_CLIENT_INIT_CONFIG)
    public ApiResponse clientInitConfig(@RequestParam("client_type") String clientType) {
        ClientBO client = new ClientBO();
        client.setClientType(clientType);
        ClientInitConfigBO data = serviceCaller.call(ResourcesServices.clientInitConfig, client, ClientInitConfigBO.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("config success").data(data).build();
    }

    /**
     * 获取图标配置接口
     *
     * @return
     */
    @RequestMapping("/operations/api/{v}/icon/getIcon")
    @ResponseBody
    @Cachable(expire = ResourcesCacheExpireTime.CONFIG_ICON_CONFIG)
    public ApiResponse getIconConfig(@RequestParam(value = "client_type", required = false, defaultValue = "H5") String clientType) {
        ClientBO client = new ClientBO();
        client.setClientType(clientType);
        Map data = serviceCaller.call(ResourcesServices.getIconConfig, client, Map.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("List").data(data).build();
    }


}
