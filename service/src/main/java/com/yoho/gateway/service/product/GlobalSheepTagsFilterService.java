package com.yoho.gateway.service.product;

import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.model.product.ProductVo;

/**
 * 描述:全球购羊头品牌标签过滤
 * Created by pangjie@yoho.cn on 2016/4/7.
 */
public interface GlobalSheepTagsFilterService {

    /**
     * 过滤全球购羊头商品标签--用于搜索的响应结果
     *
     * @param products
     * @return
     */
    JSONObject filterTags(JSONObject products);

    /**
     * 过滤全够购羊头品怕商品对象的标签
     *
     * @param productVo
     * @return
     */
    ProductVo filterTags(ProductVo productVo);
}
