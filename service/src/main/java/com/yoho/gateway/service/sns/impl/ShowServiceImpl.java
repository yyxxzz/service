package com.yoho.gateway.service.sns.impl;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.service.sns.IShowService;
import com.yoho.service.model.sns.response.CurrencyParamRspBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouxiang on 2016/3/26.
 */
@Service
public class ShowServiceImpl implements IShowService {

    private static Logger logger = LoggerFactory.getLogger(ShowServiceImpl.class);

    @Autowired
    ServiceCaller serviceCaller;

    /**
     * 获取用户返币晒物总数
     * @param ssoUids
     * @return
     */
    @Override
    public Map<String, CurrencyParamRspBO> returnCurrency(String[] ssoUids) throws ServiceException {
        logger.info("returnCurrency request param sso_uid is {}",ssoUids);
        List ssoUidList = Arrays.asList(ssoUids);
        Map<String, CurrencyParamRspBO> map = serviceCaller.call("sns.returnCurrency", ssoUidList, Map.class);;
        return map;
    }
}
