package com.yoho.gateway.controller.promotion;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.ServiceError;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.service.promotion.impl.CouponService;
import com.yoho.service.model.promotion.UserCouponLi;
import com.yoho.service.model.promotion.UserCouponListBO;
import com.yoho.service.model.promotion.request.UserCouponListReq;

/**
 * promotion gateway
 *
 * @author lijian
 * @Time 2015/12/11
 */
@Controller
public class CouponController {

    static Logger logger = LoggerFactory.getLogger(CouponController.class);

    private final String QUERY_USER_COUPON_SERVICE_NAME = "promotion.queryUserCoupons";

    private final String COUPONS_COUNT_URL = "promotion.queryUserCouponsCnt";


    private final String SEND_COUPON_SUCCESS = "优惠券列表";

    //获取优惠券数量成功
    private final int COUPONS_TOTAL_SUCCESS_CODE = 200;
    private final String COUPONS_TOTAL_SUCCESS_MSG = "coupons total";


    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
    private CouponService couponService;


	/**
	 * 查询用户优惠券的列表 app调用 支持分页 获取用户指定状态的优惠券总数，use：已使用，notuse：未使用，overtime：过期
	 * @param uid
	 * @param type
	 * @param limit
	 * @param page
	 * @return
	 */
    @RequestMapping(params = "method=app.coupons.lists")
    @ResponseBody
    public ApiResponse queryUserCoupons(@RequestParam(value = "uid") Integer uid,
                                        @RequestParam(value = "type") String type,
                                        @RequestParam(value = "limit") Integer limit,
                                        @RequestParam(value = "page") Integer page) {
        UserCouponListReq userCouponListReq = new UserCouponListReq();
        userCouponListReq.setLimit(limit);
        userCouponListReq.setPage(page);
        userCouponListReq.setType(type);
        userCouponListReq.setUid(uid);
        logger.info(" UserCouponListReq request is {}", userCouponListReq.toString());
        if (userCouponListReq == null || (userCouponListReq.getUid() == null)) {
            logger.warn("RequestBody is wrong ", userCouponListReq);
            throw new ServiceException(ServiceError.PROMOTION_REQUEST_PAREMENT_ERROR);
        }

        UserCouponListBO data = serviceCaller.call(QUERY_USER_COUPON_SERVICE_NAME, userCouponListReq, UserCouponListBO.class);
        logger.info("result data is {}", JSON.toJSONString(data));
        return new ApiResponse.ApiResponseBuilder().code(200).message(SEND_COUPON_SUCCESS).data(data).build();
    }

    /**
    * 查询用户优惠券的列表 app调用 支持分页 为了兼容遗留老系统功能和上面的接口一样 status=0 未使用 1 已使用
    * @param uid
    * @param status
    * @param limit
    * @param page
    * @return
    */
    @RequestMapping(params = "method=app.coupons.li")
    @ResponseBody
    public ApiResponse queryUserCouponLi(@RequestParam(value = "uid") Integer uid,
                                         @RequestParam(value = "status") Integer status,
                                         @RequestParam(value = "limit") Integer limit,
                                         @RequestParam(value = "page") Integer page) {
        String type = status == 0 ? "notuse" : "use";
        UserCouponListReq userCouponListReq = new UserCouponListReq();
        userCouponListReq.setLimit(limit);
        userCouponListReq.setPage(page);
        userCouponListReq.setType(type);
        userCouponListReq.setUid(uid);
        logger.info(" UserCouponListReq request is {}", userCouponListReq.toString());
        if (userCouponListReq == null || (userCouponListReq.getUid() == null)) {
            logger.warn("RequestBody is wrong ", userCouponListReq);
            throw new ServiceException(ServiceError.PROMOTION_REQUEST_PAREMENT_ERROR);
        }

        UserCouponListBO data = serviceCaller.call(QUERY_USER_COUPON_SERVICE_NAME, userCouponListReq, UserCouponListBO.class);
        logger.info("result data is {}", JSON.toJSONString(data));
        UserCouponLi userCouponLi = couponService.convertUserCouponList(data);
        return new ApiResponse.ApiResponseBuilder().code(200).data(userCouponLi).build();
    }


    /**
     * 获取用户优惠券数量
     *
     * @param type 优惠券类型 notuse:未使用，use：已使用
     * @param uid  用户id
     * @return ApiResponse
     */
    @RequestMapping(params = "method=app.coupons.total")
    @ResponseBody
    public ApiResponse getCouponsCount(@RequestParam("type") String type, @RequestParam("uid") int uid) {
        logger.debug("Begin call getCouponsList gateway. Param type is {}, uid is {}", type, uid);
        ApiResponse responseBean = null;
        //(1)组装请求参数，并提交新增请求
        UserCouponListReq userCouponListReq = new UserCouponListReq();
        userCouponListReq.setUid(uid);
        userCouponListReq.setType(type);
        Integer count = serviceCaller.call(COUPONS_COUNT_URL, userCouponListReq, Integer.class);

        //(2)组装返回
        Map<String, Integer> dataMap = new HashMap<String, Integer>();
        dataMap.put("total", count);
        responseBean = new ApiResponse.ApiResponseBuilder().code(COUPONS_TOTAL_SUCCESS_CODE).message(COUPONS_TOTAL_SUCCESS_MSG).data(dataMap).build();
        return responseBean;
    }
}