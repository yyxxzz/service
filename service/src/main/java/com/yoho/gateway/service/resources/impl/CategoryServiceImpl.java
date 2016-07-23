package com.yoho.gateway.service.resources.impl;

import com.yoho.gateway.service.resources.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 分类管理
 * Created by sunjiexiang on 2015/12/24.
 */
@Service
public class CategoryServiceImpl implements ICategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public Map<String, String> getOneCategoryById(Map<String, String> category, String fields) {
        if (Objects.isNull(category)) {
            return Collections.EMPTY_MAP;
        }

        if (StringUtils.isEmpty(fields)) {
            fields = "id,sort_name,sort_url";
        } else if (StringUtils.equals(fields, "*")) {
            return category;
        }

        //定义返回结果
        Map<String, String> result = new HashMap<>();

        for (String field : fields.split(",")) {
            field = StringUtils.trim(field);
            if (StringUtils.isEmpty(field)) {
                continue;
            }

            if (!category.keySet().contains(field)) {
                continue;
            }

            result.put(field, category.get(field));
        }

        return result;
    }

}
