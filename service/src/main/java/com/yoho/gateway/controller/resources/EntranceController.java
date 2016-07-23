package com.yoho.gateway.controller.resources;

import com.alibaba.fastjson.JSON;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.resource.ResourcesServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 入口信息
 * Created by sunjiexiang on 2016/2/16.
 */
@RestController
public class EntranceController {

    private static final Logger logger = LoggerFactory.getLogger(EntranceController.class);

    @Autowired
    private ServiceCaller serviceCaller;

    /**
     * 获取性别选择页入口状态
     */
    @RequestMapping("/operations/api/{v}/entrance/getEntrance")
    @Cachable(expire = ResourcesCacheExpireTime.ENTRANCE)
    public ApiResponse getEntrance(@RequestParam(value = "client_type", required = false, defaultValue = "h5") String clientType,
                                   @RequestParam(value = "app_version", required = false, defaultValue = "4.0.0") String app_version) {
        //判断版本号是否低于4.0.0，如果是返回给定json
        if (app_version.compareTo("4.0.0") < 0) {
            String retJsonData = "{\"list\":[{\"channel_id\":\"6\",\"click_after_img\":\"boy_p@3x.png\",\"click_before_img\":\"boy_n@3x.png\",\"gender\":\"1,3\",\"id\":\"6\",\"url\":\"http://m.yohobuy.com/boys.html?openby:yohobuy={\\\"action\\\":\\\"go.gender\\\",\\\"channel\\\":1,\\\"params\\\":{\\\"gender\\\":1}}\",\"yh_channel\":\"6\"},{\"channel_id\":\"6\",\"click_after_img\":\"girl_p@3x.png\",\"click_before_img\":\"girl_n@3x.png\",\"gender\":\"2,3\",\"id\":\"6\",\"url\":\"http://m.yohobuy.com/girls.html?openby:yohobuy={\\\"action\\\":\\\"go.gender\\\",\\\"channel\\\":2,\\\"params\\\":{\\\"gender\\\":2}}\",\"yh_channel\":\"2\"},{\"channel_id\":\"6\",\"click_after_img\":\"kids_p@3x.png\",\"click_before_img\":\"kids_n@3x.png\",\"gender\":\"2,3\",\"id\":\"6\",\"url\":\"http://m.yohobuy.com/kids.html?openby:yohobuy={\\\"action\\\":\\\"go.gender\\\",\\\"channel\\\":3,\\\"params\\\":{\\\"gender\\\":3}}\",\"yh_channel\":\"3\"},{\"channel_id\":\"6\",\"click_after_img\":\"life_p@3x.png\",\"click_before_img\":\"life_n@3x.png\",\"gender\":\"1,2,3\",\"id\":\"6\",\"url\":\"http://m.yohobuy.com/life.html?openby:yohobuy={\\\"action\\\":\\\"go.gender\\\",\\\"channel\\\":4,\\\"params\\\":{\\\"gender\\\":4}}\",\"yh_channel\":\"4\"}],\"zip_url\":\"http://img12.static.yhbimg.com/yhb-img01/2015/08/25/02/0251554e494fb78730bf19e817099fced1.zip\"}";
            logger.info("entrance/getEntrance, app_version is under 4.0.0,app_version={}", app_version);
            return new ApiResponse.ApiResponseBuilder().code(200).message("Entrance List").data(JSON.parse(retJsonData)).build();
        }
        Map<String, Object> data = serviceCaller.call(ResourcesServices.getEntrance, clientType, Map.class);
        return new ApiResponse.ApiResponseBuilder().code(200).message("Entrance List").data(data).build();
    }
}
