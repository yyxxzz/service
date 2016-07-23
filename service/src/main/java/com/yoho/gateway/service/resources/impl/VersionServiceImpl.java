package com.yoho.gateway.service.resources.impl;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.model.resources.AppVersionWarningVO;
import com.yoho.gateway.service.resources.VersionService;
import com.yoho.gateway.utils.StringUtils;
import com.yoho.service.model.resource.AppVersionWarningBO;
import com.yoho.service.model.resource.ResourcesServices;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Created by sunjiexiang on 2016/2/16.
 */
@Service
public class VersionServiceImpl implements VersionService {

    @Autowired
    private ServiceCaller serviceCaller;

    @Override
    public AppVersionWarningVO getVersion(String clientType) {
        if (StringUtils.isEmpty(clientType)) {
            return null;
        }
        AppVersionWarningBO appVersionWarningBO = serviceCaller.call(ResourcesServices.getVersion, clientType, AppVersionWarningBO.class);

        if (Objects.nonNull(appVersionWarningBO)) {
            AppVersionWarningVO appVersionWarningVO = new AppVersionWarningVO();
            BeanUtils.copyProperties(appVersionWarningBO, appVersionWarningVO);
            return appVersionWarningVO;
        }
        return null;
    }
}
