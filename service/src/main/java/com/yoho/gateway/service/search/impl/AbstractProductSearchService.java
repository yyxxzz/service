package com.yoho.gateway.service.search.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yoho.gateway.service.assist.ImageUrlAssist;
import com.yoho.gateway.service.assist.SearchConstants;
import com.yoho.gateway.utils.StringUtils;

/**
 * Created by sailing on 2015/12/22.
 */
public abstract class AbstractProductSearchService{

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    /**
     *
     * 对商品列表的信息进行修改，商品图片地址URL补全
     * @param gender
     * @param data
     * @param ignoreTags  商品列表中忽略的标签列表(商品便签不需要展示这个集合里面的便签)
     */
    protected void processProductList(String gender ,JSONArray productList, List<String> ignoreTags) {
        if (null != productList) {
            // 判别默认的商品是否将默认的图片URL赋值到skn
            boolean flag = false;
            for (int i = 0; i < productList.size(); i++) {
                JSONObject product = productList.getJSONObject(i);
                // 全球购商品不处理图片
                if(null==product || "Y".equals(product.getString("is_global")))
                {
                	continue;
                }
                // 遍历goodList、替换图片链接
                JSONArray goodsList = product.getJSONArray("goods_list");
                JSONObject firstGood = null;
                JSONObject realGood = null;
                if (null != goodsList) {
                    for (int j = 0; j < goodsList.size(); j++) {
                        JSONObject goods = goodsList.getJSONObject(j);
                        goods.replace("images_url", ImageUrlAssist.getAllProductPicUrl(goods.get("images_url"), "goodsimg", "center", "d2hpdGU="));
                        goods.replace("cover_1", ImageUrlAssist.getAllProductPicUrl(goods.get("cover_1"), "goodsimg", "center", "d2hpdGU="));
                        goods.replace("cover_2", ImageUrlAssist.getAllProductPicUrl(goods.get("cover_2"), "goodsimg", "center", "d2hpdGU="));
                        // 有可能，goodlist中的所有good都没有cover1或者cover2，最终就采用第一个good
                        if(j == 0){
                        	firstGood = goods;
                        }
                        // 如果产品没有默认图片，则需要根据gender来判断采用哪个good
                        if ("1,3".equals(gender) && StringUtils.isNotEmpty(goods.getString("cover_1"))) {
                        	realGood = goods;
                        }else if("2,3".equals(gender) && StringUtils.isNotEmpty(goods.getString("cover_2"))){
                        	realGood = goods;
                        }
                        
                        //  此skc是默认的，则将图片赋值给skn
                        if ("Y".equals(goods.get("is_default"))) {
                            product.replace("default_images", getDefaultImages(gender, goods));
                            flag = true;
                        }
                    }
                }
                // 如果还未赋值，则取第一个skc产品的默认图片
                if (flag){
                    flag = false;
                } else {
                    if (null != realGood) {
                        product.replace("default_images", getDefaultImages(gender, realGood));
                    }else if(null != firstGood){
                    	product.replace("default_images", getDefaultImages(gender, firstGood));
                    }
                }
                // 获取该产品的便签列表（是否新品、打折商品、是否有货商品...）
                product.put("tags", getProductTags(product, ignoreTags));
                //计算vip等级对应的vip价格
                if(null!=product.get("sales_price"))
                {
                	product.put("vip1_price", Double.valueOf(product.get("sales_price").toString())*SearchConstants.NodeConstants.VIP1_PRICE);
                	product.put("vip2_price", Double.valueOf(product.get("sales_price").toString())*SearchConstants.NodeConstants.VIP2_PRICE);
                	product.put("vip3_price", Double.valueOf(product.get("sales_price").toString())*SearchConstants.NodeConstants.VIP3_PRICE);
                }
            }
        }
    }

    /**
     * 获取商品的标签信息
     */
    protected List<String> getProductTags(JSONObject product, List<String> igonreTags) {
        List<String> tags = new ArrayList<String>();

        //是否是yohood商品
        if (!igonreTags.contains("is_yohood") && "1".equals(product.get("yohood_id"))) {
            tags.add("is_yohood");
        }

        //是否新品
        if (!igonreTags.contains("is_new") && "Y".equals(product.get("is_new"))) {
            tags.add("is_new");
        }

        //5折以下是sale
        Object sales_price = product.get("sales_price");
        Object market_price = product.get("market_price");
        if (!igonreTags.contains("is_discount") && null != sales_price && null != market_price) {
            try {
                double salesPrice = 0;
                if (sales_price instanceof BigDecimal) {
                    salesPrice = ((BigDecimal)sales_price).doubleValue();
                }else if (sales_price instanceof Integer){
                    salesPrice = ((Integer)sales_price).doubleValue();
                }
                double marketPrice = 0;
                if (market_price instanceof BigDecimal) {
                    marketPrice = ((BigDecimal)market_price).doubleValue();
                }else {
                    marketPrice = ((Integer)market_price).doubleValue();
                }

                if (salesPrice * 2 < marketPrice) {
                    tags.add("is_discount");
                }
            } catch (Exception e) {
                LOGGER.warn("The sales_price or market_price of product is not double. product is " + product.toString(), e);
            }
        }

        //是否限量商品
        if (!igonreTags.contains("is_limited") && "Y".equals(product.get("is_limited"))) {
            tags.add("is_limited");
        }

        //即将售罄
        if (!igonreTags.contains("is_soon_sold_out") && "Y".equals(product.get("is_soon_sold_out"))) {
            tags.add("is_soon_sold_out");
        }

        // 按规则过滤商品标签
        fixTags(tags);
        return tags;
    }

    /**
     * 大促(包含yohood)>新品>打折>限量;即将售罄
     * 新品和打折组合（显示新品）
     * 新品和大促型标签组合（显示大促型标签）
     * 限量和打折组合（显示限量）
     * YOHOOD和新品组合（显示YOHOOD）
     *
     * 将标签重新处理一遍
     * @param array $tags = array(
     *      'new' => 'Y',
     *      'sales' =>'Y',
     *      'yohood' => 'Y',
     *      'limited' => 'Y',
     *      'sold_out_soon' => 'Y'
     *  );
     */
    protected void fixTags(List<String> tags) {
        if (tags.contains("is_yohood") || tags.contains("mid-year") || tags.contains("year-end") ) {
            tags.remove("is_new");
            tags.remove("is_discount");
        }

        if (tags.contains("is_new") || tags.contains("is_limited")) {
            tags.remove("is_discount");
        }

        if (tags.contains("is_yohood") && tags.contains("is_limited")) {
            tags.remove("mid-year");
            tags.remove("year-end");
        }
    }

    /**
     * 根据性别来决定  默认图片获取字段   如果是 2、3       则优先从cover2 --》 cover1 -- 》 images_url
     * 							                       否则优先从cover1 --》 cover2 -- 》 images_url
     * @param gender
     * @param goods
     * @return
     */
    protected String getDefaultImages(String gender, JSONObject goods) {
        String images_url = goods.getString("images_url");
        String conver_1 = goods.getString("cover_1");
        String conver_2 = goods.getString("cover_2");
        if("2,3".equals(gender)){
            return StringUtils.isNotBlank(conver_2) ? conver_2 : StringUtils.isNotBlank(conver_1) ? conver_1 : images_url;
        } else {
            return StringUtils.isNotBlank(conver_1) ? conver_1 : StringUtils.isNotBlank(conver_2) ? conver_2 : images_url;
        }
    }


    //假如传值1   则需要查询1,3     假如查询2 则需要查询2,3
    protected String processGender(String yh_channel, String gender) {
        if(StringUtils.isBlank(gender) && StringUtils.isNotBlank(yh_channel)) {
            if ("1".equals(yh_channel)) {
                return "1,3";
            } else if ("2".equals(yh_channel)){
                return "2,3";
            }
        }
        return gender;
    }
    //假如传值1   则需要查询1,3     假如查询2 则需要查询2,3
    protected String processGender(String yh_channel) {
    	String gender=null;
        if( StringUtils.isNotBlank(yh_channel)) {
            if ("1".equals(yh_channel)) {
                 gender="1,3";
            } else if ("2".equals(yh_channel)){
                 gender ="2,3";
            }
        }
        return gender;
    }
}
