package com.yoho.gateway.controller.product.cnstore;


import java.util.List;

import com.yoho.gateway.cache.expire.product.ExpireTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoho.core.common.helpers.ImagesHelper;
import com.yoho.core.rest.client.ServiceCaller;
import com.yoho.error.exception.ServiceException;
import com.yoho.gateway.cache.Cachable;
import com.yoho.gateway.controller.ApiResponse;
import com.yoho.gateway.model.product.SimpleBrandVo;
import com.yoho.product.model.BrandBo;
import com.yoho.product.request.BaseRequest;

/**
 * 品牌
 * Created by caoyan
 */
@Controller
public class BrandController {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(BrandController.class);

    @Autowired
    private ServiceCaller serviceCaller;
    
    @RequestMapping(params = "method=cnstore.brandlist.get")
	@ResponseBody
	@Cachable(expire = ExpireTime.cnstore_brandlist_get)
	public ApiResponse getAllBrands(@RequestParam(value = "type", defaultValue="jsonp")String type,
			@RequestParam(value = "callback", defaultValue="callback")String callback) throws ServiceException {
    	LOGGER.info("Begin call method=web.brand.all. with param type is {}, callback is {}", type, callback);
    	
    	BaseRequest<Integer> req = new BaseRequest<Integer>();
        BrandBo[] boArr = serviceCaller.call("product.queryAllBrandList", req, BrandBo[].class);
        if(null == boArr || boArr.length == 0){
        	return new ApiResponse(500, "brand is null", null);
        }
        List<SimpleBrandVo> voList = buildSimpleBrandVoList(boArr);
        JSONObject obj = new JSONObject();
        obj.put("brands", voList);
		ApiResponse response = new ApiResponse.ApiResponseBuilder().message("brands list").data(obj).build();
		return response;
	}
    
    private List<SimpleBrandVo> buildSimpleBrandVoList(BrandBo[] boArr){
    	List<SimpleBrandVo> voList = Lists.newArrayList();
    	for(BrandBo bo : boArr){
    		SimpleBrandVo vo = new SimpleBrandVo();
    		vo.setId(bo.getId());
    		vo.setName(bo.getBrandName());
    		vo.setImage(ImagesHelper.getImageUrl(bo.getBrandIco(), 150, 150, 2, "brandLogo").replace("quality/80", "quality/90"));
    		voList.add(vo);
    	}
    	return voList;
    }
    
}
