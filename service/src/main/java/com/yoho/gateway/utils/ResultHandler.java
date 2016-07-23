package com.yoho.gateway.utils;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.model.search.ProductSearchReq;
import com.yoho.gateway.service.assist.SearchConstants;

/**
 * Created by zhaoqi on 2016/7/7 0007.
 */
public class ResultHandler {

    public static void genderAdaptor(ProductSearchReq req, JSONObject data) {
        if(org.apache.commons.lang.StringUtils.isNotEmpty(req.getYhChannel()) && org.apache.commons.lang.StringUtils.isEmpty(req.getGender())){
            if(data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER)!=null){
                JSONObject genderObject = data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER).getJSONObject("gender");
                if(genderObject != null){
                    if(req.getYhChannel().contains("1")){
                        genderObject.remove("2,3");
                        genderObject.remove("1");
                    }
                    if(req.getYhChannel().contains("2")){
                        genderObject.remove("1,3");
                        genderObject.remove("2");
                    }
                    if("3".equals(req.getGender())||"4".equals(req.getGender())){
                        data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER).remove("gender");
                    }
                }
                data.getJSONObject(SearchConstants.NodeConstants.KEY_FILTER).put("gender",genderObject);
            }
        }
    }

}
