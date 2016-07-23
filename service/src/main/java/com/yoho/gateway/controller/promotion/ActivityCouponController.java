package com.yoho.gateway.controller.promotion;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.exception.GatewayException;
import com.yoho.service.model.promotion.ActivityBo;
import com.yoho.service.model.promotion.ActivityCouponBo;
import com.yoho.service.model.promotion.request.ActivityReq;
import com.yoho.service.model.response.CommonRspBO;

/**
 * ActivityCoupon gateway
 *
 * @author maelk-liu
 */
@Controller
public class ActivityCouponController {

    private static Logger logger = LoggerFactory.getLogger(ActivityCouponController.class);

    private final static int ACTIVITY_PARAM_ID_ERROR = 404;

    private final static String ACTIVITY_PARAM_ID_CODE_ERROR = "activity_id is error";

    private final static int RESULT_SUCCESS = 200;

    private final static String RESULT_SUCCESS_CODE = "call getActivityById service success";
    
    private final static String RESULT_FAIL_CODE ="call promotion.getActivityById fail";
    
    private final static int CALL_SAVEINBOX_SERVICE_FAIL=500;
    
    private final static String RESULT_SUCCESS_GETACTCOUPONBYIDS_CODE = "call promotion.getActCouponByIds service success";
    
    @Autowired
    private ServiceCaller serviceCaller;
    

    /**
     * 根据活动Id获得活动相关详情
     *
     * @param activity_id
     * @return ApiResponse
     * @throws GatewayException 
     */
//    @RequestMapping("/event/api/*/activity/get")
    @RequestMapping(params = "method=web.activity.get")
    @ResponseBody
    public ApiResponse getActivityById(@RequestParam(value = "activity_id") Integer activity_id) {
        logger.info(" gateway begin getActivityById request is {}", activity_id);
        if (activity_id == null || activity_id < 1) {
            logger.debug("RequestBody is wrong,activity_id is{} ", activity_id);
            return new ApiResponse.ApiResponseBuilder().code(ACTIVITY_PARAM_ID_ERROR).message(ACTIVITY_PARAM_ID_CODE_ERROR).build();
        }
        // 组装请求参数
        ActivityReq req = new ActivityReq();
        req.setId(activity_id);
        ActivityBo ao = serviceCaller.call("promotion.getActivityByActId", req, ActivityBo.class);
        if(null==ao){
            logger.debug("call promotion.getActivityById fail, activity_id is {}",activity_id);
            return new ApiResponse.ApiResponseBuilder().code(CALL_SAVEINBOX_SERVICE_FAIL).message(RESULT_FAIL_CODE).data(null).build();
        }
        JSONObject jo= toJson(ao);
        return new ApiResponse.ApiResponseBuilder().code(RESULT_SUCCESS).message(RESULT_SUCCESS_CODE).data(jo).build();
    }
    
    private JSONObject toJson(ActivityBo ao){
        JSONObject jo = new JSONObject();
        jo.put("activity_type", ao.getActivityType());
        jo.put("act_name", ao.getActName());
        jo.put("act_url", ao.getActUrl());
        jo.put("create_time", ao.getCreateTime());
        jo.put("end_time", ao.getEndTime());
        jo.put("id", ao.getId());
        jo.put("is_verify", ao.getIsVerify());
        jo.put("p_id", ao.getpId());
        jo.put("result", ao.getResult());
        jo.put("share_describe", ao.getShareDescribe());
        jo.put("share_pic", ao.getSharePic());
        jo.put("share_title", ao.getShareTitle());
        jo.put("share_url", ao.getShareUrl());
        jo.put("start_time", ao.getStartTime());
        jo.put("status", ao.getStatus());
        if(ao.getConf()==null){
            jo.put("conf", null);
            return jo;
        }
        JSONObject conf = new JSONObject();
        conf.put("activity_id", ao.getConf().getActivityId());
        conf.put("bg_image", ao.getConf().getBgImage());
        conf.put("bg_image_url", ao.getConf().getBgImageUrl());
        conf.put("coupon_id", ao.getConf().getCouponId());
        conf.put("id", ao.getConf().getId());
        conf.put("instructions", ao.getConf().getInstructions());
        conf.put("is_newuser", ao.getConf().getIsNewuser());
        JSONArray array = new JSONArray();
        if(ao.getConf().getCoupon_count()!=null){
            for(Map.Entry<String, Integer> entry:ao.getConf().getCoupon_count().entrySet()){
                JSONObject coupon_count_obj  = new JSONObject(); 
                coupon_count_obj.put("id",entry.getKey());
                coupon_count_obj.put("coupon_num", entry.getValue());
                array.add(coupon_count_obj);
            }
        }
        conf.put("coupon_count",array);
        jo.put("conf", conf);
        return jo;
    }
    

//    @RequestMapping("/event/api/*/activity/getCoupon")
    @RequestMapping(params = "method=web.activity.getCoupon")
    @ResponseBody
    public ApiResponse getActivityCouponById(@RequestParam(value = "activity_id") Integer activity_id,
            @RequestParam(value = "uid") Integer uid, @RequestParam(value = "coupon_id",required = false) Integer coupon_id) {
        logger.info(" getActivityById request is {}, {}, {}", activity_id, uid, coupon_id);
        if (activity_id < 1) {
            logger.warn("RequestBody is wrong,activity_id is{} ", activity_id);
            throw new ServiceException(404, "activity_id is error");
        }
        if (uid < 1) {
            logger.warn("RequestBody is wrong,uid<1 is{} ", activity_id);
            throw new ServiceException(404, "uid<1 is error");
        }
        ActivityCouponBo req = new ActivityCouponBo();
        req.setActivityId(activity_id);
        req.setUid(uid);
        ActivityBo ao;
        if (coupon_id == null) {
            req.setCouponId("");
            ao = serviceCaller.call("promotion.getActivityCoupon", req, ActivityBo.class);
        } else {
            req.setCouponId(String.valueOf(coupon_id));
            ao = serviceCaller.call("promotion.getActCouponByIds", req, ActivityBo.class);
        }
        if(ao.getResult()==null||"".equals(ao.getResult())){
            return new ApiResponse.ApiResponseBuilder().code(500).message("call service getCoupon fail").data(null).build();
        }
        Integer code=Integer.parseInt(ao.getResult());
        return new ApiResponse.ApiResponseBuilder().code(code).message(ao.getMessage()).data(null).build();
    }

    /**
     * 新客领优惠券
     */
    @RequestMapping(params = "method=app.activity.getCoupon")
    @ResponseBody
    public ApiResponse getCouponById(@RequestParam(value = "activity_id") Integer activity_id,
            @RequestParam(value = "uid") Integer uid, @RequestParam(value = "coupon_id",required = false) String coupon_id) {	
    	logger.info("getCouponById request is {}, {}, {}", activity_id, uid, coupon_id);
    	 
    	 if (activity_id < 1) {
             logger.warn("RequestBody is wrong,activity_id is {} ", activity_id);
             throw new ServiceException(404, "activity_id is error");
         }
         if (uid < 1) {
             logger.warn("RequestBody is wrong,uid<1 is {} ", activity_id);
             throw new ServiceException(404, "uid<1 is error");
         }

         ActivityCouponBo request = new ActivityCouponBo();
         request.setActivityId(activity_id);
         request.setCouponId(coupon_id);
         request.setUid(uid);
         CommonRspBO response = serviceCaller.call("promotion.getActivityCoupon", request, CommonRspBO.class);
    		
 		 return new ApiResponse.ApiResponseBuilder().code(response.getCode()).message(response.getMessage()).build();	
    }
}