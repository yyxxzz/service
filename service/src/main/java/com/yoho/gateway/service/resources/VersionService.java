package com.yoho.gateway.service.resources;

import com.yoho.gateway.model.resources.AppVersionWarningVO;

/**
 * Created by sunjiexiang on 2016/2/16.
 */
public interface VersionService {

    /**
     * 获取APP版本升级提醒
     * @param clientType
     * @return
     */
    AppVersionWarningVO getVersion(String clientType);
}
