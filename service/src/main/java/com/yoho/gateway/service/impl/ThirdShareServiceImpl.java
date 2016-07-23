package com.yoho.gateway.service.impl;

import com.yoho.gateway.model.request.ShareReqVO;
import com.yoho.gateway.model.response.ThirdUserRspVO;
import com.yoho.gateway.service.ThirdShareService;
import com.yoho.service.model.profile.UserShareBO;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import weibo4j.Timeline;
import weibo4j.model.Status;
import weibo4j.model.WeiboException;

import java.util.Date;

/**
 * 类的描述
 *
 * @author lijian
 * @Time 2015/11/10
 */
@Service
public class ThirdShareServiceImpl implements ThirdShareService {


    @Override
    public Status submitMsg(ShareReqVO shareReqVO,ThirdUserRspVO thirdUserRspVO) {
        Status status = null;
        //文字分享 功能暂时不用 后面再说
        if (shareReqVO != null && shareReqVO.getAuthType() != null && shareReqVO.getAuthType().intValue() == 2
                && shareReqVO.getContentType() != null && shareReqVO.getContentType().intValue() == 1) {
            try {
                Timeline time = new Timeline(thirdUserRspVO.getAccess_token());
                status = time.updateStatus(shareReqVO.getContent());
            } catch (WeiboException e) {
                e.printStackTrace();
            }
            //图文分享
        } else if (shareReqVO != null && shareReqVO.getAuthType() != null && shareReqVO.getAuthType().intValue() == 2
                && shareReqVO.getContentType() != null && shareReqVO.getContentType().intValue() == 2) {
        /*    AccessToken accessToken = null;
            try {
                Timeline time = new Timeline(accessToken.getAccessToken());
                byte[] url = ImgTools.image2byte(shareReqVO.getFileUrl()
                );
                ImageItem pic = new ImageItem("pic", url);
                status = time.uploadStatus(shareReqVO.getContent(), pic);
            } catch (WeiboException e) {
                e.printStackTrace();
            }*/
        }
        return status;


    }


    //判断token是否有效 1 是 0否
    public  int isValidToken(ThirdUserRspVO thirdUserRspVO,UserShareBO userShareBO) {
        Date dt = new Date();
        // 判断是否过期
        Long timeCnt = dt.getTime() / 1000 - userShareBO.getUpdateTime();
        Long tokenExpire = Long.parseLong(thirdUserRspVO.getExpires_in());

        if (timeCnt > tokenExpire) {
            return 0;
        } else {
            return 1;
        }

    }

    public  UserShareBO setUserInfo(ShareReqVO shareReqVO){

        //封装用户信息

        JSONObject shareInfo = JSONObject.fromObject(shareReqVO.getAuthInfo());
        ThirdUserRspVO thirdUserInput = (ThirdUserRspVO) JSONObject.toBean(shareInfo, ThirdUserRspVO.class);
        UserShareBO shareUserVO = new UserShareBO();
        shareUserVO.setUid(Integer.valueOf(shareReqVO.getUid()));
        Long accountId = Long.parseLong(thirdUserInput.getUid());
        shareUserVO.setAccountId(accountId.longValue());
        shareUserVO.setChannel(shareReqVO.getAuthType());
        shareUserVO.setShareReqObj(shareReqVO.getAuthInfo());
        Long time = new Date().getTime() / 1000;
        shareUserVO.setCreateTime(time.intValue());
        shareUserVO.setUpdateTime(time.intValue());
        return  shareUserVO;

    }


}
