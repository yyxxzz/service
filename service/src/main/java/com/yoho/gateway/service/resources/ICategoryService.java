package com.yoho.gateway.service.resources;

import java.util.Map;

/**
 * 分类管理
 * Created by sunjiexiang on 2015/12/24.
 */
public interface ICategoryService {

    /**
     * 根据fields定义字段返回对象内容
     * @param category  数据对象
     * @param fields    返回结果key
     * @return
     */
    Map<String, String> getOneCategoryById(Map<String, String> category, String fields);
}
