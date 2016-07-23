package com.yoho.gateway.service;

import com.yoho.gateway.model.request.ShareReqVO;
import com.yoho.gateway.model.response.ThirdUserRspVO;
import com.yoho.service.model.profile.UserShareBO;
import org.springframework.stereotype.Service;
import weibo4j.model.Status;

/**
 *  用户微信微博分享功能
 * Created by lijian 2015-11-16 17:13:44
 */
@Service
public interface ThirdShareService {

    public Status submitMsg(ShareReqVO shareReqVO,ThirdUserRspVO thirdUserRspVO);

    //判断token是否有效 1 是 0否
    public  int isValidToken(ThirdUserRspVO thirdUserRspVO,UserShareBO userShareBO);

    public  UserShareBO setUserInfo(ShareReqVO shareReqVO);


}
