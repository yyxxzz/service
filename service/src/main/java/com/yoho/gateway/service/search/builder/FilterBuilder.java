package com.yoho.gateway.service.search.builder;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.service.assist.SearchConstants;

/**
 * filter的包装类，装饰作用
 * Created by chenchao on 2016/4/19.
 */
public class FilterBuilder {
    private JSONObject filter;

    private FilterBuilder(){
    }

    public FilterBuilder(JSONObject filter){
        this.filter = filter;
    }
    public FilterBuilder buildPriceRange(){
        JSONObject price = filter.getJSONObject(SearchConstants.NodeConstants.FILTER_KEY_PRICE);
        filter.put(SearchConstants.NodeConstants.FILTER_KEY_PRICERANGE, price);
        return this;
    }

    public FilterBuilder buildDiscount(Object discount){
        filter.put(SearchConstants.NodeConstants.KEY_DISCOUNT, discount);
        return this;
    }

    public FilterBuilder buildGroupSort(Object  groupSort){
        filter.put(SearchConstants.NodeConstants.KEY_GROUP_SORT, groupSort);
        return this;
    }

    public JSONObject getFilter() {
        return filter;
    }
}
