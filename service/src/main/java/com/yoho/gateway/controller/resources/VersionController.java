package com.yoho.gateway.controller.resources;

import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.resources.AppVersionWarningVO;
import com.yoho.gateway.service.resources.VersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * Created by sunjiexiang on 2016/2/16.
 */
@RestController
public class VersionController {

    @Autowired
    private VersionService versionService;

    /**
     * 获取APP版本升级提醒
     */
    @RequestMapping("/operations/api/{v}/version/getVersion")
    @Cachable(expire = ResourcesCacheExpireTime.APP_VERSION)
    public ApiResponse getVersion(@RequestParam(value = "client_type", required = false, defaultValue = "h5") String clientType) {
        AppVersionWarningVO data = versionService.getVersion(clientType);
        if (Objects.nonNull(data)) {
            return new ApiResponse.ApiResponseBuilder().code(200).message("APP版本信息").data(data).build();
        } else {
            return new ApiResponse.ApiResponseBuilder().code(200).message("APP版本信息").data(false).build();
        }
    }
}
