package com.yoho.gateway.service.sns;

import com.yoho.error.exception.ServiceException;
import com.yoho.service.model.sns.response.CurrencyParamRspBO;

import java.util.Map;

/**
 * Created by zhouxiang on 2016/3/26.
 */
public interface IShowService {

    /**
     * 获取用户返币晒物总数
     * @param ssoUids
     * @return
     * @throws ServiceException
     */
    Map<String,CurrencyParamRspBO> returnCurrency(String[] ssoUids) throws ServiceException;
}
