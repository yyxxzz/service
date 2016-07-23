package com.yoho.gateway.controller.promotion;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.yoho.core.cache.CacheClient;
import com.yoho.core.common.restbean.ResponseBean;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.promotion.LimitCodeVo;
import com.yoho.gateway.service.promotion.LimitCodeService;
import com.yoho.gateway.utils.DateUtil;
import com.yoho.service.model.promotion.LimitCodeProductBo;
import com.yoho.service.model.promotion.LimitCodeUserBo;
import com.yoho.service.model.promotion.LimitProductPromotionBo;
import com.yoho.service.model.promotion.request.LimitCodeReq;
import com.yoho.service.model.promotion.request.ProductLimitCodeReq;

/**
 * 限购码功能
 *
 * @author wangshusheng
 * @Time 2016/2/1
 */
@Controller
public class LimitCodeController {

    static Logger logger = LoggerFactory.getLogger(LimitCodeController.class);

    // 查询该限定商品是否有可使用限购码
    private final String CHECK_AVAILABLE_LIMITCODE_SERVICE_NAME = "promotion.checkAvailableLimitCode";
    //用户分享领取限购码，新增领取记录
    private final String ADD_LIMITCODE_SERVICE_NAME = "promotion.addLimitCodeReceiveRecord";
    //查询我的限购码领取记录
    private final String QUERY_MYLIMITCODE_SERVICE_NAME = "promotion.queryLimitCodeReceiveRecord";
    // 判断我的限购码tab处是否展示小红点
    private final String CHECK_NEW_LIMITPRODUCT_SERVICE_NAME = "promotion.checkNewLimitProduct";
    // 删除失效的限购码记录
    private final String DELETE_INVALID_LIMITCODE_SERVICE_NAME = "promotion.deleteInvalidLimitCodeRecord";
    
    private final String CHECK_AVAILABLE_LIMITCODE = "是否可使用限购码";

  	//判断限购码小红点用的到访问限售频道的时间缓存的key
  	private final static String LIMITCODE_REDPOINT_KEYPRE="yh:gw:promotion:limitcode:redpoint:";
  	
  	// 上次我的限购码列表访问时间缓存90天，访问后更新
    private final static int LIMITCODE_LIST_CACHE = 24 *60 * 60;
  	
    @Autowired
	private LimitCodeService limitCodeService;
    
    @Autowired
    private ServiceCaller serviceCaller;

    @Autowired
	private CacheClient cacheClient;
    
    /**
     * 查询该限定商品是否有可使用限购码，此时不一定有productSkn
     * @param uid
	 * @param limitProductCode
	 * @param productSkn
	 * @return true:有可以使用的限购码	false：没有可用的限购码，限购码已被抢光
     */
    @RequestMapping(params = "method=app.limitcode.check")
    @ResponseBody
    public ApiResponse checkAvailableLimitCode(@RequestParam(value = "uid") Integer uid,
                                             @RequestParam(value = "limitProductCode") String limitProductCode) {
    	ProductLimitCodeReq limitCodeReq = new ProductLimitCodeReq();
    	limitCodeReq.setLimitProductCode(limitProductCode);
    	limitCodeReq.setUid(uid);
        logger.info(" limitCodeReq request is {}", limitCodeReq.toString());
        
        ResponseBean data = serviceCaller.call(CHECK_AVAILABLE_LIMITCODE_SERVICE_NAME, limitCodeReq, ResponseBean.class);
        logger.info("result data is {}", JSON.toJSONString(data));
        return new ApiResponse.ApiResponseBuilder().code(200).message(CHECK_AVAILABLE_LIMITCODE).data(data).build();
    }

    /**
     * 用户分享领取限购码，新增领取记录
     * @param uid
	 * @param limitProductCode
	 * @param productSkn
     */
    @RequestMapping(params = "method=app.limitcode.add")
    @ResponseBody
    public ApiResponse addLimitCodeReceiveRecord(@RequestParam(value = "uid") Integer uid,
            @RequestParam(value = "limitProductCode") String limitProductCode) {
    	logger.info(" come into addLimitCodeReceiveRecord, uid is {}, limitProductCode is {}", uid, limitProductCode);
    	ProductLimitCodeReq limitCodeReq = new ProductLimitCodeReq();
    	limitCodeReq.setLimitProductCode(limitProductCode);
    	limitCodeReq.setUid(uid);
        logger.info(" limitCodeReq request is {}", limitCodeReq.toString());

        LimitCodeUserBo data = serviceCaller.call(ADD_LIMITCODE_SERVICE_NAME, limitCodeReq, LimitCodeUserBo.class);
        logger.info("result data is {}", JSON.toJSONString(data));
        
        return new ApiResponse.ApiResponseBuilder().code(200).data(data).build();
    }

    /**
     * 查询我的限购码列表，筛选规则：
	 * 不展示已使用完的限购码
	 * 排序：今日发售>已经发售>即将发售>已失效
	 * 已失效规则：商品售罄
	 * 下单未付款时，码不在页面展示；如果取消订单，码释放，再次展示；可用多次的码在最后一次使用时不展示
     * @param uid
     */
    @RequestMapping(params = "method=app.limitcode.query")
    @ResponseBody
    public ApiResponse queryLimitCodeReceiveRecord(@RequestParam(value = "uid") Integer uid) {
    	logger.info(" come into queryLimitCodeReceiveRecord, uid is {}", uid);
    	ProductLimitCodeReq limitCodeReq = new ProductLimitCodeReq();
    	limitCodeReq.setUid(uid);
        logger.info(" limitCodeReq request is {}", limitCodeReq.toString());
        LimitCodeProductBo[] limitCodeProducts = serviceCaller.call(QUERY_MYLIMITCODE_SERVICE_NAME, limitCodeReq, LimitCodeProductBo[].class);
        LimitCodeVo limitCodeVo = limitCodeService.getLimitCodeVo(limitCodeProducts);
        
        // 修改缓存中的时间为当前时间
        String cacheKey = LIMITCODE_REDPOINT_KEYPRE + uid;
        Integer currentTime = DateUtil.getCurrentTimeSecond();
        logger.info(" LimitCodeController.queryLimitCodeReceiveRecord currentTime is {}", currentTime);
		cacheClient.set(cacheKey, LIMITCODE_LIST_CACHE, String.valueOf(currentTime));
        return new ApiResponse.ApiResponseBuilder().code(200).data(limitCodeVo).build();
    }
    
    /**
     * 判断我的限购码tab处是否展示小红点
     * @param uid
	 * @param limitProductCode
	 * @param productSkn
	 * @return true:展示小红点	false：不展示小红点
     */
    @RequestMapping(params = "method=app.limitcode.showredpoint")
    @ResponseBody
    public ApiResponse checkShowRedPoint(@RequestParam(value = "uid") Integer uid) {
    	logger.info(" come into checkShowRedPoint, uid is {}", uid);
    	String cacheKey = LIMITCODE_REDPOINT_KEYPRE + uid;
    	ProductLimitCodeReq limitCodeReq = new ProductLimitCodeReq();
    	limitCodeReq.setUid(uid);
        logger.info(" limitCodeReq request is {}", limitCodeReq.toString());
        
        LimitCodeProductBo[] limitCodeProducts = serviceCaller.call(CHECK_NEW_LIMITPRODUCT_SERVICE_NAME, limitCodeReq, LimitCodeProductBo[].class);
        
        boolean flag = false;
        Integer currentTime = DateUtil.getCurrentTimeSecond();
        if(limitCodeProducts==null || limitCodeProducts.length==0){
        	// 限购码列表为空
        	return new ApiResponse.ApiResponseBuilder().code(200).message("我的限购码小红点").data(flag).build();
        }
        
        // 从缓存中取出上一次点击限定频道时间，如果为空表示首次进入限售频道，此时只要有开售商品，则展示小红点；如果不为空，则判断这个时间段内是否有开售商品
 		String lastQueryTime = cacheClient.get(cacheKey, String.class);
 		logger.info(" LimitCodeController.checkShowRedPoint lastQueryTime is {}", lastQueryTime);
 		currentTime = DateUtil.getCurrentTimeSecond();
 		if(StringUtils.isNotEmpty(lastQueryTime)){
 			Integer lastTime = Integer.parseInt(lastQueryTime);
 			// 对每个限购码商品信息，排除未开售，和点击时间比较判断是否为新开售
 			for (LimitCodeProductBo limitCodeProductBo : limitCodeProducts) {
 				LimitProductPromotionBo limitProductPromotionBo = limitCodeProductBo.getLimitProductBo();
 				if(limitProductPromotionBo!=null){
 					Integer saleTime = limitProductPromotionBo.getSaleTime();
 					if(saleTime<currentTime && saleTime>lastTime){
 						flag = true;
 						break;
 					}
 				}
 			}
 		}else{
 			for (LimitCodeProductBo limitCodeProductBo : limitCodeProducts) {
 				LimitProductPromotionBo limitProductPromotionBo = limitCodeProductBo.getLimitProductBo();
 				if(limitProductPromotionBo!=null){
 					Integer saleTime = limitProductPromotionBo.getSaleTime();
 					if(saleTime<currentTime){
 						flag = true;
 						break;
 					}
 				}
 			}
 		}
 		
        return new ApiResponse.ApiResponseBuilder().code(200).message("我的限购码小红点").data(flag).build();
    }
    
    /**
     * 删除我的限购码中失效的记录
     * @param uid
	 * @param limitCode
     */
    @RequestMapping(params = "method=app.limitcode.deleteinvalid")
    @ResponseBody
    public ApiResponse deleteInvalidLimitCodeRecord(@RequestParam(value = "uid") Integer uid,
            @RequestParam(value = "limitCode") String limitCode) {
    	logger.info(" come into deleteInvalidLimitCodeRecord, uid is {}, limitCode is {}", uid, limitCode);
    	LimitCodeReq limitCodeReq = new LimitCodeReq();
    	limitCodeReq.setLimitCode(limitCode);
    	limitCodeReq.setUid(uid);
        logger.info(" limitCodeReq request is {}", limitCodeReq.toString());

        Boolean data = serviceCaller.call(DELETE_INVALID_LIMITCODE_SERVICE_NAME, limitCodeReq, Boolean.class);
        logger.info("result data is {}", JSON.toJSONString(data));
        
        return new ApiResponse.ApiResponseBuilder().code(200).message("删除成功").data(data).build();
    }
    
}