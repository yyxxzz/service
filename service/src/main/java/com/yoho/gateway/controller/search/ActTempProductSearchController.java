/*
 * 文 件 名:  ActTempProductSearchController.java
 * 版    权:  YOHO Buy PlatForm CMS ,All rights reserved
 * 描    述:  <描述>
 * 修 改 人:  蔡青青
 * @author chenchao chao.chen@yoho.cn
 * 修改时间:  2016年4月13日
 */
package com.yoho.gateway.controller.search;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.cache.expire.product.ExpireTime;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.search.ProductSearchService;

/**
 * 活动模板商品信息
 * 
 * @author 蔡青青
 * @version [版本号, 2016年4月13日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@Controller
public class ActTempProductSearchController
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ActTempProductSearchController.class);
    private final String QUERY_TEMPID_INVALID = "请选择模板ID";
    @Autowired
    private ProductSearchService productSearchService;

    /**
     * 根据活动模板id获取商品
     * @param order
     * @param page
     * @param limit
     * @param templateId
     * @param needFilter
     * @param channel
     * @param gender
     * @return
     */
    @RequestMapping("/operations/api/{v}/activitytemplate/getProduct")
    @ResponseBody
    @Cachable(expire = ExpireTime.operations_activitytemp_getProduct)
    public ApiResponse getProduct(
        @RequestParam(value = "order", required = false) String order,
        @RequestParam(value = "page", required = false) Integer page,
        @RequestParam(value = "limit", required = false, defaultValue = "50") Integer limit,
        @RequestParam(value = "template_id", required = false) String templateId,
        @RequestParam(value = "needFilter", required = false) String needFilter,
        @RequestParam(value = "yh_channel", required = false) String channel,
        @RequestParam(value = "gender", required = false) String gender,
        @RequestParam(value = "age_level", required = false) String ageLevel,
        @RequestParam(value = "app_version", required = false) String appVersion) {
        LOGGER.info("getProduct.params -> order:{}, limit:{}, page:{}, template_id:{},yh_channel :{}, needFilter:{}, gender:{}, ageLevel:{}",
            new Object[] {order, limit, page, templateId, channel, needFilter, gender, ageLevel});
        if (StringUtils.isEmpty(templateId)) {
            return new ApiResponse.ApiResponseBuilder().code(400).message(QUERY_TEMPID_INVALID).build();
        }
        Integer status = 1;
        Integer stocknumber = 1;
        Integer needSmallSort = 1;
        needFilter = StringUtils.isNotEmpty(needFilter) && "N".equals(needFilter) ? "N" : "Y";
        
        order = StringUtils.isEmpty(order) ? "activities_desc,activities_id_asc" : order;
        
        ProductSearchReq productReq = new ProductSearchReq().setPage(page)
            .setYhChannel(channel)
            .setGender(gender)
            .setLimit(limit)
            .setActTemp(templateId)
            .setNeedFilter(needFilter)
            .setNeedSmallSort(needSmallSort)
            .setOrder(order)
            .setStatus(status)
            .setStocknumber(stocknumber)
            .setViewNum(limit)
            .setAgeLevel(ageLevel)
            .setAppVersion(appVersion)
            .setSearchFrom("activitytemplate.getproduct");
        LOGGER.info("getProduct.param[productReq] is {}", new Object[] {productReq});
        JSONObject data = productSearchService.searchActProduct(productReq);
        return new ApiResponse.ApiResponseBuilder().code(200).message("Search List.").data(data).build();
    }
    
}
