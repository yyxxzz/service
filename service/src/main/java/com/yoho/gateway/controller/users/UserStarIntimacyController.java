package com.yoho.gateway.controller.users;

import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.service.model.request.UserStarIntimacyReqBO;
import com.yoho.service.model.response.UserStarInfoBO;
import com.yoho.service.model.response.UserStarInfoTopBO;
import com.yoho.service.model.response.UserStarIntimacyInfoBO;
import com.yoho.service.model.response.UserStarIntimacySignBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 新潮教室对外提供接口
 * @author LiQZ on 2016/4/8.
 */
@RestController
public class UserStarIntimacyController {

    @Autowired
    private ServiceCaller serviceCaller;

    public static final int STAR_ID = 1;

    /**
     * 获取当前用户亲密度，排名
     */
    @RequestMapping(params = "method=app.starClass.rankInfo")
    public ApiResponse getMyUserStarIntimacyInfo(Integer uid) {
        UserStarIntimacyReqBO userStarIntimacyReqBO = new UserStarIntimacyReqBO();
        userStarIntimacyReqBO.setUid(uid);
        userStarIntimacyReqBO.setStarId(STAR_ID);
        UserStarInfoBO userStarInfoBO = serviceCaller.call("users.getMyUserStarIntimacyInfo", userStarIntimacyReqBO, UserStarInfoBO.class);
        return new ApiResponse.ApiResponseBuilder().message("获取用户亲密度成功").data(userStarInfoBO).build();
    }

    /**
     * 签到
     */
    @RequestMapping(params = "method=app.starClass.sign")
    public ApiResponse userStarIntimacySign(Integer uid) {
        UserStarIntimacyReqBO userStarIntimacyReqBO = new UserStarIntimacyReqBO();
        userStarIntimacyReqBO.setUid(uid);
        userStarIntimacyReqBO.setStarId(STAR_ID);
        UserStarIntimacySignBO userStarIntimacySignBO = serviceCaller.call("users.userStarIntimacySign", userStarIntimacyReqBO, UserStarIntimacySignBO.class);
        return new ApiResponse.ApiResponseBuilder().message("获取用户签到信息成功").data(userStarIntimacySignBO).build();
    }

    /**
     * 转发
     */
    @RequestMapping(params = "method=app.starClass.forward")
    public ApiResponse userStarIntimacyForward(Integer uid) {
        UserStarIntimacyReqBO userStarIntimacyReqBO = new UserStarIntimacyReqBO();
        userStarIntimacyReqBO.setUid(uid);
        userStarIntimacyReqBO.setStarId(STAR_ID);
        int addNum = serviceCaller.call("users.userStarIntimacyForward", userStarIntimacyReqBO, Integer.class);
        return new ApiResponse.ApiResponseBuilder().message("用户转发").data(addNum).build();
    }

    /**
     * 前 100 名
     */
    @RequestMapping(params = "method=app.starClass.top100")
    public ApiResponse userStarIntimacyTop100(@RequestParam(required=false) Integer uid) {
        UserStarIntimacyReqBO userStarIntimacyReqBO = new UserStarIntimacyReqBO();
        userStarIntimacyReqBO.setStarId(STAR_ID);
        userStarIntimacyReqBO.setUid(uid == null? 0 : uid);
        UserStarInfoTopBO userStarInfoTopBO = serviceCaller.call("users.userStarIntimacyTop100", userStarIntimacyReqBO, UserStarInfoTopBO.class);
        return new ApiResponse.ApiResponseBuilder().message("top100").data(userStarInfoTopBO).build();
    }

}
