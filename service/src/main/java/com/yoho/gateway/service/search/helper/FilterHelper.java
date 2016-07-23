package com.yoho.gateway.service.search.helper;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.service.assist.ImageUrlAssist;
import com.yoho.gateway.service.assist.SearchConstants;

/**
 * Created by chenchao on 2016/4/19.
 */
public final class FilterHelper {

    public static void completeBrandImg(JSONArray brandList){
        if (null != brandList) {
            int size = brandList.size();
            for (int i = 0; i < size; i++) {
                JSONObject brand = brandList.getJSONObject(i);
                brand.replace(SearchConstants.NodeConstants.KEY_BRAND_ICO,
                        ImageUrlAssist.getAllProductPicUrl(brand.get(SearchConstants.NodeConstants.KEY_BRAND_ICO),
                                "brandLogo", "center", "d2hpdGU="));
            }
        }
    }
}
