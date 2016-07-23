package com.yoho.gateway.service.product.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.yoho.gateway.model.product.ProductVo;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.service.product.GlobalSheepTagsFilterService;
import com.yoho.product.constants.ProductTagsEnum;

/**
 * 描述:全球购羊头品牌标签过滤
 * Created by pangjie@yoho.cn on 2016/4/7.
 */
@Service
public class GlobalSheepTagsFilterServiceImpl implements GlobalSheepTagsFilterService {

    private static final String TAGS_NODE = "tags";

    @Override
    public JSONObject filterTags(JSONObject products) {
        if (products == null) {
            return products;
        }

        JSONArray productsJSONArray = products.getJSONArray(SearchConstants.NodeConstants.KEY_PRODUCT_LIST);
        if (productsJSONArray == null || productsJSONArray.size() == 0) {
            return products;
        }

        //遍历商品
        for (int i = 0, l = productsJSONArray.size(); i < l; i++) {
            JSONObject product = productsJSONArray.getJSONObject(i);
            //获取标签列表
            JSONArray tags = product.getJSONArray(TAGS_NODE);

            //生成新标签，并设置新标签
            List<String> newTags = genNewTags(tags);
            product.put(TAGS_NODE, newTags);
        }

        return products;
    }

    @Override
    public ProductVo filterTags(ProductVo productVo) {
        if (productVo == null) {
            return productVo;
        }

        //获取商品标签
        List<String> tags = productVo.getTags();

        //生成新标签，并设置新标签
        List<String> newTags = genNewTags(tags);
        productVo.setTags(newTags);
        return productVo;
    }

    /**
     * 根据原标签生成新标签
     *
     * @param orgTagsList
     * @return
     */
    private List<String> genNewTags(List<?> orgTagsList) {

        if (CollectionUtils.isEmpty(orgTagsList)) {
            return Lists.newArrayList(ProductTagsEnum.IS_IN_STOCK.getCode());
        }

        //新标签:原标签中包含限量则标签转变为国内现货和限量销售，否则只含有国内现货。
        List<String> newTags = orgTagsList.contains(ProductTagsEnum.IS_LIMITED.getCode()) ?
                Lists.newArrayList(ProductTagsEnum.IS_IN_STOCK.getCode(), ProductTagsEnum.IS_LIMITED.getCode()) : Lists.newArrayList(ProductTagsEnum.IS_IN_STOCK.getCode());
        return newTags;
    }
}
